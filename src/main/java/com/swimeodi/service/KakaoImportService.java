package com.swimeodi.service;

import com.swimeodi.dto.kakao.KakaoPlace;
import com.swimeodi.dto.kakao.KakaoPreviewItem;
import com.swimeodi.dto.kakao.KakaoSearchResponse;
import com.swimeodi.entity.Pool;
import com.swimeodi.repository.PoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoImportService {

    private final RestTemplate restTemplate;
    private final PoolRepository poolRepository;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private static final String KAKAO_KEYWORD_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

    private static final List<String> REGIONS = List.of(
            // 서울 25구
            "서울 종로구", "서울 중구", "서울 용산구", "서울 성동구", "서울 광진구",
            "서울 동대문구", "서울 중랑구", "서울 성북구", "서울 강북구", "서울 도봉구",
            "서울 노원구", "서울 은평구", "서울 서대문구", "서울 마포구", "서울 양천구",
            "서울 강서구", "서울 구로구", "서울 금천구", "서울 영등포구", "서울 동작구",
            "서울 관악구", "서울 서초구", "서울 강남구", "서울 송파구", "서울 강동구",
            // 부산
            "부산 중구", "부산 서구", "부산 동구", "부산 영도구", "부산 부산진구",
            "부산 동래구", "부산 남구", "부산 북구", "부산 해운대구", "부산 사하구",
            "부산 금정구", "부산 강서구", "부산 연제구", "부산 수영구", "부산 사상구",
            // 인천
            "인천 중구", "인천 동구", "인천 미추홀구", "인천 연수구", "인천 남동구",
            "인천 부평구", "인천 계양구", "인천 서구",
            // 대구
            "대구 중구", "대구 동구", "대구 서구", "대구 남구", "대구 북구",
            "대구 수성구", "대구 달서구",
            // 광주
            "광주 동구", "광주 서구", "광주 남구", "광주 북구", "광주 광산구",
            // 대전
            "대전 동구", "대전 중구", "대전 서구", "대전 유성구", "대전 대덕구",
            // 울산
            "울산 중구", "울산 남구", "울산 동구", "울산 북구", "울산 울주군",
            // 세종
            "세종시",
            // 경기
            "수원시", "고양시", "성남시", "용인시", "부천시", "안산시", "화성시",
            "남양주시", "안양시", "평택시", "의정부시", "시흥시", "파주시", "김포시",
            "광명시", "광주시", "군포시", "하남시", "오산시", "이천시", "안성시",
            "양주시", "구리시", "의왕시", "포천시",
            // 강원
            "춘천시", "원주시", "강릉시", "동해시", "속초시", "삼척시",
            // 충북
            "청주시", "충주시", "제천시",
            // 충남
            "천안시", "아산시", "서산시", "공주시", "논산시", "당진시",
            // 전북
            "전주시", "군산시", "익산시", "정읍시", "남원시",
            // 전남
            "목포시", "여수시", "순천시", "나주시", "광양시",
            // 경북
            "포항시", "경주시", "구미시", "안동시", "김천시", "경산시",
            // 경남
            "창원시", "진주시", "김해시", "거제시", "양산시", "통영시",
            // 제주
            "제주시", "서귀포시"
    );

    public void importAll(SseEmitter emitter) {
        int saved = 0;
        int skipped = 0;

        try {
            for (int i = 0; i < REGIONS.size(); i++) {
                String region = REGIONS.get(i);
                String query = region + " 수영장";

                for (int page = 1; page <= 3; page++) {
                    try {
                        KakaoSearchResponse response = search(query, page);
                        if (response == null || response.getDocuments() == null) break;

                        for (KakaoPlace place : response.getDocuments()) {
                            if (poolRepository.existsByKakaoId(place.getId())) {
                                skipped++;
                            } else {
                                savePool(place);
                                saved++;
                            }
                        }

                        if (response.getMeta() == null || response.getMeta().isEnd()) break;

                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        log.warn("검색 실패 - region: {}, page: {}, error: {}", region, page, e.getMessage());
                    }
                }

                sendProgress(emitter, saved, skipped, region, i + 1, REGIONS.size());
            }

            emitter.send(SseEmitter.event()
                    .name("done")
                    .data("{\"saved\":" + saved + ",\"skipped\":" + skipped + "}"));
            emitter.complete();

        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    private KakaoSearchResponse search(String query, int page) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        String url = UriComponentsBuilder.fromHttpUrl(KAKAO_KEYWORD_URL)
                .queryParam("query", query)
                .queryParam("size", 15)
                .queryParam("page", page)
                .build()
                .toUriString();

        ResponseEntity<KakaoSearchResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), KakaoSearchResponse.class
        );
        return response.getBody();
    }

    private void savePool(KakaoPlace place) {
        Pool pool = new Pool();
        pool.setKakaoId(place.getId());
        pool.setName(place.getPlaceName());
        pool.setAddress(place.getRoadAddressName() != null && !place.getRoadAddressName().isBlank()
                ? place.getRoadAddressName()
                : place.getAddressName());
        pool.setLat(Double.parseDouble(place.getY()));
        pool.setLng(Double.parseDouble(place.getX()));
        pool.setPhone(place.getPhone());
        poolRepository.save(pool);
    }

    public List<KakaoPreviewItem> preview(String query) {
        List<KakaoPreviewItem> result = new ArrayList<>();
        String searchQuery = query.contains("수영장") ? query : query + " 수영장";
        for (int page = 1; page <= 3; page++) {
            try {
                KakaoSearchResponse response = search(searchQuery, page);
                if (response == null || response.getDocuments() == null) break;
                for (KakaoPlace place : response.getDocuments()) {
                    KakaoPreviewItem item = new KakaoPreviewItem();
                    item.setKakaoId(place.getId());
                    item.setName(place.getPlaceName());
                    item.setAddress(place.getRoadAddressName() != null && !place.getRoadAddressName().isBlank()
                            ? place.getRoadAddressName() : place.getAddressName());
                    item.setPhone(place.getPhone());
                    item.setLat(Double.parseDouble(place.getY()));
                    item.setLng(Double.parseDouble(place.getX()));
                    item.setAlreadyExists(poolRepository.existsByKakaoId(place.getId()));
                    result.add(item);
                }
                if (response.getMeta() == null || response.getMeta().isEnd()) break;
                Thread.sleep(100);
            } catch (Exception e) {
                log.warn("미리보기 검색 실패: {}", e.getMessage());
                break;
            }
        }
        return result;
    }

    public int importSelected(List<KakaoPreviewItem> items) {
        int count = 0;
        for (KakaoPreviewItem item : items) {
            if (!poolRepository.existsByKakaoId(item.getKakaoId())) {
                saveFromPreview(item);
                count++;
            }
        }
        return count;
    }

    private void saveFromPreview(KakaoPreviewItem item) {
        Pool pool = new Pool();
        pool.setKakaoId(item.getKakaoId());
        pool.setName(item.getName());
        pool.setAddress(item.getAddress());
        pool.setLat(item.getLat());
        pool.setLng(item.getLng());
        pool.setPhone(item.getPhone());
        poolRepository.save(pool);
    }

    private void sendProgress(SseEmitter emitter, int saved, int skipped,
                              String region, int current, int total) {
        try {
            String data = String.format(
                    "{\"saved\":%d,\"skipped\":%d,\"region\":\"%s\",\"current\":%d,\"total\":%d}",
                    saved, skipped, region, current, total
            );
            emitter.send(SseEmitter.event().name("progress").data(data));
        } catch (IOException e) {
            log.warn("SSE 전송 실패: {}", e.getMessage());
        }
    }
}
