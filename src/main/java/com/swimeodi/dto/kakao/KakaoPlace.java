package com.swimeodi.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoPlace {
    private String id;

    @JsonProperty("place_name")
    private String placeName;

    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("road_address_name")
    private String roadAddressName;

    private String phone;

    private String x; // 경도 (longitude)
    private String y; // 위도 (latitude)

    @JsonProperty("category_name")
    private String categoryName;
}
