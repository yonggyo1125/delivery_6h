package org.sparta.delivery.order.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SelectedOption {
    private String optionName;     // 기본 옵션명
    private int optionPrice;       // 기본 옵션가격
    private List<SelectedSubOption> subOptions;


    @Getter
    @Builder
    @AllArgsConstructor
    public static class SelectedSubOption {
        private String name;       // 하위 옵션 명
        private int addPrice;      // 하위 옵션 가격
    }
}
