package com.pstreaming.service;

import com.pstreaming.domain.Usuario;
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

    public VoiceAuthService(WebClient.Builder builder,
            @Value("${voice.ms.url}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public boolean enroll(Usuario usuario, MultipartFile audio) {
        LinkedMultiValueMap<String, Object> data = multipart(audio, "audio");

        webClient.post()
                .uri("/enroll/" + usuario.getIdUsuario())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(data))
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        if (audio == null) {
            throw new IllegalStateException("Respuesta inválida del la verificación del micro-servicio");
        }
        return Boolean.TRUE;
    }

    public boolean verify(Usuario usuario, MultipartFile audio) {
        LinkedMultiValueMap<String, Object> data = multipart(audio, "audio");

        Map<String, Object> res = webClient.post()
                .uri("/verify/" + usuario.getIdUsuario())
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

    private LinkedMultiValueMap<String, Object> multipart(MultipartFile file, String fieldName) {
        LinkedMultiValueMap<String, Object> mp = new LinkedMultiValueMap<>();
        mp.add(fieldName, new ByteArrayResource(getBytes(file)) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename() != null ? file.getOriginalFilename() : "audio.webm";
            }
        });
        return mp;
    }

    private byte[] getBytes(MultipartFile f) {
        try {
            return f.getBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
