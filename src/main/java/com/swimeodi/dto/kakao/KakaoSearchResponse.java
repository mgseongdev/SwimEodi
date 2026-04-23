package com.swimeodi.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class KakaoSearchResponse {
    private List<KakaoPlace> documents;

    @JsonProperty("meta")
    private KakaoMeta meta;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class KakaoMeta {
        @JsonProperty("is_end")
        private boolean end;

        @JsonProperty("total_count")
        private int totalCount;
    }
}
