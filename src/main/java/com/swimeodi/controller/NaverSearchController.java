package com.swimeodi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class NaverSearchController {

    @Value("${naver.search.client-id}")
    private String clientId;

    @Value("${naver.search.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;

    @GetMapping("/local")
    public ResponseEntity<String> searchLocal(@RequestParam String query) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://openapi.naver.com/v1/search/local.json")
                .queryParam("query", query)
                .queryParam("display", 5)
                .queryParam("sort", "comment")
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        ResponseEntity<String> naverResponse = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(naverResponse.getBody());
    }
}
