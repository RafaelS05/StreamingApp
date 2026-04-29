import os
import shutil
from dotenv import load_dotenv

load_dotenv()

FFMPEG_DIR = os.getenv("FFMPEG_DIR", "")
FFMPEG_BIN = os.getenv("FFMPEG_BIN", "")
FFPROBE_BIN = os.getenv("FFPROBE_BIN", "")

def resolve_executable(path, dirpath, name):
    if path and os.path.isabs(path) and os.path.exists(path):
        return path
    if dirpath:
        candidate = os.path.join(dirpath, os.path.basename(path or name))
        if os.path.exists(candidate):
            return candidate
    found = shutil.which(name)
    if found:
        return found
    raise RuntimeError(f"No se encontró {name}. Configure FFMPEG_BIN/FFPROBE_BIN o instale en PATH.")

ffmpeg_path = resolve_executable(FFMPEG_BIN, FFMPEG_DIR, "ffmpeg")
ffprobe_path = resolve_executable(FFPROBE_BIN, FFMPEG_DIR, "ffprobe")

# Añade el directorio al PATH ANTES de importar pydub
os.environ["PATH"] = os.path.dirname(ffmpeg_path) + os.pathsep + os.environ.get("PATH", "")

# importa pydub y configura las rutas de ffmpeg/ffprobe
from pydub import AudioSegment
AudioSegment.converter = ffmpeg_path
AudioSegment.ffprobe = ffprobe_path


from fastapi import FastAPI, UploadFile, File, Form, HTTPException, Depends
from pydantic import BaseModel
import base64
import numpy as np
import torch
from speechbrain.inference.speaker import EncoderClassifier
import io
import soundfile as sf
from sqlalchemy import create_engine, Column, BigInteger, LargeBinary, String
from sqlalchemy.orm import declarative_base, sessionmaker

app = FastAPI()

DEVICE = "cuda" if torch.cuda.is_available() else "cpu"


classifier = EncoderClassifier.from_hparams(
    source="speechbrain/spkrec-ecapa-voxceleb",
    run_opts={"device": DEVICE}
)

MODEL_NAME = "speechbrain-ecapa-voxceleb"
THRESHOLD = 0.85 

class VerifyResponse(BaseModel):
    score: float
    is_match: bool
    threshold: float

def to_wav_16k_mono(audio_bytes: bytes) -> bytes:
    audio = AudioSegment.from_file(io.BytesIO(audio_bytes))
    audio = audio.set_frame_rate(16000).set_channels(1).set_sample_width(2)
    out = io.BytesIO()
    audio.export(out, format="wav")
    return out.getvalue()

def embedding_from_audio(audio_bytes: bytes) -> np.ndarray:
    wav_bytes = to_wav_16k_mono(audio_bytes)

    wav, sr = sf.read(io.BytesIO(wav_bytes))
    wav = wav.astype(np.float32)

    waveform = torch.tensor(wav, dtype=torch.float32).unsqueeze(0).to(DEVICE)

    with torch.no_grad():
        emb = classifier.encode_batch(waveform)

    emb = emb.squeeze(0).squeeze(0).detach().cpu().numpy()

    emb = emb / (np.linalg.norm(emb) + 1e-12)
    return emb

def cosine_similarity(a: np.ndarray, b: np.ndarray) -> float:
    return float(np.dot(a, b) / ((np.linalg.norm(a) + 1e-12) * (np.linalg.norm(b) + 1e-12)))


base = declarative_base()
class VozUsuario(base):
    __tablename__ = "voz_usuario"
    id_voz = Column(BigInteger, primary_key=True, autoincrement=True)
    voice_print = Column(LargeBinary)    #se guardan los bytes del embedding
    voz_model = Column(String(100))      # nombre del modelo
    id_usuario = Column(BigInteger, nullable=False) #FK al usuario al que pertenece la voz

DATABASE_URL = os.getenv("DATABASE_URL")
if not DATABASE_URL:
    raise RuntimeError("DATABASE_URL environment variable is not set. Please configure it in your environment.")
engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(bind=engine)

def get_db():
    db = SessionLocal()
    try:        yield db
    finally:    db.close()


@app.post("/enroll/{usuario_id}")
async def enroll(usuario_id: int, audio: UploadFile = File(...), db = Depends(get_db)):
    try:
        audio_bytes = await audio.read()
        if not audio_bytes:
            raise HTTPException(status_code=400, detail="Audio vacío")

        # Genera el embedding
        emb = embedding_from_audio(audio_bytes)

        # Verifica que emb sea válido
        if emb is None or emb.size == 0:
            raise HTTPException(status_code=500, detail="Error al generar embedding: embedding vacío")

        # Busca si el usuario ya tiene voz registrada
        registro = db.query(VozUsuario).filter_by(id_usuario=usuario_id).first()

        if registro:
            # Si existe, actualiza el embedding
            registro.voice_print = emb.tobytes()
            registro.voz_model = MODEL_NAME
        else:
            # Si no existe, crea uno nuevo
            registro = VozUsuario(
                id_usuario=usuario_id,
                voice_print=emb.tobytes(),
                voz_model=MODEL_NAME
            )
            db.add(registro)

        db.commit()
        return {"usuario_id": usuario_id, "model": MODEL_NAME}
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"Error interno: {str(e)}")

@app.post("/verify/{usuario_id}")
async def verify(usuario_id: int, audio: UploadFile = File(...), db = Depends(get_db)):
    audio_bytes = await audio.read()
    if not audio_bytes:
        raise HTTPException(status_code=400, detail="Audio vacío")

    # Busca el embedding del usuario en la DB
    registro = db.query(VozUsuario).filter_by(id_usuario=usuario_id).first()
    if not registro:
        raise HTTPException(status_code=404, detail="Usuario sin voz registrada")

    # Convierte los bytes de la DB a numpy array
    stored_emb = np.frombuffer(registro.voice_print, dtype=np.float32)

    # Genera embedding del audio recibido y compara
    new_emb = embedding_from_audio(audio_bytes)
    score = cosine_similarity(new_emb, stored_emb)

    return VerifyResponse(
        score=score,
        is_match=score >= THRESHOLD,
        threshold=THRESHOLD,
    )