package com.pstreaming.service;

import com.pstreaming.domain.Usuario;
import com.pstreaming.domain.VozUsuario;
import com.pstreaming.repository.VozUsuarioRepository;
import java.util.Base64;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class VoiceAuthService {

    private WebClient webClient;
    private VozUsuarioRepository vozRepository;

    public VoiceAuthService(WebClient.Builder builder,
            VozUsuarioRepository vozRepository,
            @Value("${voice.ms.url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.vozRepository = vozRepository;
    }

    public void enroll(Usuario usuario, MultipartFile audio) {
        LinkedMultiValueMap<String, Object> data = multipart(audio, "audio");
        
        Map<String, Object> res = webClient.post()
                .uri("/enroll")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(data))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();

        if (res == null || res.get("embedding_b64") == null) {
            throw new IllegalStateException("Respuesta inválida del registro de voz del micro-servicio");
        }
        String embB64 = (String) res.get("embedding_b64");
        byte[] embBytes = Base64.getDecoder().decode(embB64);
        String model = (String) res.getOrDefault("model", "unknown");

        VozUsuario voz = new VozUsuario();
        voz.setUsuario(usuario);
        voz.setVoicePrint(embBytes);
        voz.setVozModel(model);

        vozRepository.save(voz);
    }

    public boolean verify(Usuario usuario, MultipartFile audio) {
        VozUsuario voz = vozRepository.findByUsuario_IdUsuarioOrderByIdVozDesc(usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalStateException("Usuario sin voz registrada"));

        String emb64 = Base64.getEncoder().encodeToString(voz.getVoicePrint());

        LinkedMultiValueMap<String, Object> data = multipart(audio, "audio");
        data.add("embedding_b64", emb64);

        Map<String, Object> res = webClient.post()
                .uri("/verify")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(data))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
        
        if (res == null || res.get("is_match") == null) {
            throw new IllegalStateException("Respuesta inválida del la verificación del micro-servicio");
        }
        return Boolean.TRUE.equals(res.get("is_match"));
    }
    
    private LinkedMultiValueMap<String, Object> multipart(MultipartFile file, String fieldName){
        LinkedMultiValueMap<String, Object> mp = new LinkedMultiValueMap<>();
        mp.add(fieldName, new ByteArrayResource(getBytes(file)){
            @Override public String getFilename(){
                return file.getOriginalFilename() != null ? file.getOriginalFilename() : "audio.webm";
            }
        });
        return mp;
    }
    
    private byte[] getBytes(MultipartFile f){
        try { return f.getBytes(); 
        
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
