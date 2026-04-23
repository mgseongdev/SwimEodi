package com.swimeodi.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;

@ControllerAdvice
public class SpaController {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<byte[]> spa(NoResourceFoundException ex) throws IOException {
        String path = ex.getResourcePath();
        if (path != null && (path.startsWith("api/") || path.contains("."))) {
            return ResponseEntity.notFound().build();
        }
        ClassPathResource resource = new ClassPathResource("static/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource.getContentAsByteArray());
    }
}
