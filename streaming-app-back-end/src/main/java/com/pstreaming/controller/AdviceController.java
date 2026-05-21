package com.pstreaming.controller;

import com.pstreaming.domain.RegistroError;
import com.pstreaming.dto.ErrorResponse;
import com.pstreaming.repository.ErrorRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class AdviceController {
    
    @Autowired
    private ErrorRepository errorRepository;


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        RegistroError log = new RegistroError();
        log.setMensaje(ex.getMessage());
        log.setFecha(LocalDateTime.now());
        errorRepository.save(log);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }
}
