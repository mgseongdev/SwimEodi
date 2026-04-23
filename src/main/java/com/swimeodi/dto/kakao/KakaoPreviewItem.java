package com.swimeodi.dto.kakao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoPreviewItem {
    private String kakaoId;
    private String name;
    private String address;
    private String phone;
    private double lat;
    private double lng;
    private boolean alreadyExists;
}
