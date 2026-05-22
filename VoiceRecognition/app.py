import io
import os
import shutil
from typing import Any, Generator

import numpy as np
import torch
from dotenv import load_dotenv

load_dotenv()

FFMPEG_DIR = os.getenv("FFMPEG_DIR", "")
FFMPEG_BIN = os.getenv("FFMPEG_BIN", "")
FFPROBE_BIN = os.getenv("FFPROBE_BIN", "")


def resolve_executable(path: str, dirpath: str, name: str) -> str:
    if path and os.path.isabs(path) and os.path.exists(path):
        return path
    if dirpath:
        candidate = os.path.join(dirpath, os.path.basename(path or name))
        if os.path.exists(candidate):
            return candidate
    found = shutil.which(name)
    if found:
        return found
    raise RuntimeError(
        f"No se encontró {name}. Configure FFMPEG_BIN/FFPROBE_BIN o instale en PATH."
    )


ffmpeg_path: str = resolve_executable(FFMPEG_BIN, FFMPEG_DIR, "ffmpeg")
ffprobe_path: str = resolve_executable(FFPROBE_BIN, FFMPEG_DIR, "ffprobe")

# Añade el directorio al PATH ANTES de importar pydub
os.environ["PATH"] = (
    os.path.dirname(ffmpeg_path) + os.pathsep + os.environ.get("PATH", "")
)

# importa pydub y configura las rutas de ffmpeg/ffprobe
from pydub import AudioSegment  # noqa: E402  # type: ignore[import-untyped]

AudioSegment.converter = ffmpeg_path
AudioSegment.ffprobe = ffprobe_path  # type: ignore[attr-defined]

import soundfile as sf  # noqa: E402  # type: ignore[import-untyped]
from fastapi import Depends, FastAPI, File, HTTPException, UploadFile  # noqa: E402
from pydantic import BaseModel  # noqa: E402
from speechbrain.inference.speaker import EncoderClassifier  # noqa: E402  # type: ignore[import-untyped]
from sqlalchemy import BigInteger, Column, LargeBinary, String, create_engine  # noqa: E402
from sqlalchemy.orm import Session, declarative_base, sessionmaker  # noqa: E402

app = FastAPI()

DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

classifier: EncoderClassifier = EncoderClassifier.from_hparams(  # type: ignore[assignment]
    source="speechbrain/spkrec-ecapa-voxceleb", run_opts={"device": DEVICE}
)

MODEL_NAME = "speechbrain-ecapa-voxceleb"
THRESHOLD = 0.72


class VerifyResponse(BaseModel):
    score: float
    is_match: bool
    threshold: float


def to_wav_16k_mono(audio_bytes: bytes) -> bytes:
    audio: Any = AudioSegment.from_file(io.BytesIO(audio_bytes))
    audio = audio.set_frame_rate(16000).set_channels(1).set_sample_width(2)
    out = io.BytesIO()
    audio.export(out, format="wav")
    return out.getvalue()  # type: ignore[no-any-return]


def embedding_from_audio(audio_bytes: bytes) -> np.ndarray:
    wav_bytes = to_wav_16k_mono(audio_bytes)

    wav_raw, _sr = sf.read(io.BytesIO(wav_bytes))
    wav: np.ndarray = np.asarray(wav_raw, dtype=np.float32)

    waveform = torch.tensor(wav, dtype=torch.float32).unsqueeze(0).to(DEVICE)

    with torch.no_grad():
        emb_tensor: Any = classifier.encode_batch(waveform)

    emb_np: np.ndarray = np.asarray(
        emb_tensor.squeeze(0).squeeze(0).detach().cpu().numpy()
    )
    emb_np = emb_np / (np.linalg.norm(emb_np) + 1e-12)
    return emb_np


def cosine_similarity(a: np.ndarray, b: np.ndarray) -> float:
    return float(
        np.dot(a, b) / ((np.linalg.norm(a) + 1e-12) * (np.linalg.norm(b) + 1e-12))
    )


base = declarative_base()


class VozUsuario(base):  # type: ignore[valid-type, misc]
    __tablename__ = "voz_usuario"
    id_voz = Column(BigInteger, primary_key=True, autoincrement=True)
    voice_print = Column(LargeBinary)
    voz_model = Column(String(100))
    id_usuario = Column("id_usuario_FK", String(100))


DATABASE_URL = os.getenv("DATABASE_URL")
if not DATABASE_URL:
    raise RuntimeError(
        "DATABASE_URL environment variable is not set. Please configure it in your environment."
    )
engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(bind=engine)


def get_db() -> Generator[Session, None, None]:
    db: Session = SessionLocal()
    try:
        yield db
    finally:
        db.close()


@app.post("/enroll/{usuario_id}")
async def enroll(
    usuario_id: str,
    audio: UploadFile = File(...),  # noqa: B008
    db: Session = Depends(get_db),  # noqa: B008
) -> dict[str, Any]:
    try:
        audio_bytes = await audio.read()
        if not audio_bytes:
            raise HTTPException(status_code=400, detail="Audio vacío")

        emb = embedding_from_audio(audio_bytes)

        if emb.size == 0:
            raise HTTPException(
                status_code=500, detail="Error al generar embedding: embedding vacío"
            )

        registro = db.query(VozUsuario).filter_by(id_usuario=usuario_id).first()

        if registro:
            registro.voice_print = emb.tobytes()  # type: ignore[assignment]
            registro.voz_model = MODEL_NAME  # type: ignore[assignment]
        else:
            registro = VozUsuario(
                id_usuario=usuario_id, voice_print=emb.tobytes(), voz_model=MODEL_NAME
            )
            db.add(registro)

        db.commit()
        return {"usuario_id": usuario_id, "model": MODEL_NAME}
    except HTTPException:
        raise
    except Exception as e:
        db.rollback()
        raise HTTPException(status_code=500, detail=f"Error interno: {str(e)}") from e


@app.post("/verify/{usuario_id}")
async def verify(
    usuario_id: str,
    audio: UploadFile = File(...),  # noqa: B008
    db: Session = Depends(get_db),  # noqa: B008
) -> VerifyResponse:
    audio_bytes = await audio.read()
    if not audio_bytes:
        raise HTTPException(status_code=400, detail="Audio vacío")

    registro = db.query(VozUsuario).filter_by(id_usuario=usuario_id).first()
    if not registro:
        raise HTTPException(status_code=404, detail="Usuario sin voz registrada")

    # Convierte los bytes de la DB a numpy array
    raw_bytes: bytes = bytes(registro.voice_print)  # type: ignore[arg-type]
    stored_emb: np.ndarray = np.frombuffer(raw_bytes, dtype=np.float32)

    new_emb = embedding_from_audio(audio_bytes)
    score = cosine_similarity(new_emb, stored_emb)

    return VerifyResponse(
        score=score,
        is_match=score >= THRESHOLD,
        threshold=THRESHOLD,
    )