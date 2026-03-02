package org.sparta.delivery.order.domain.service;

import org.sparta.delivery.order.domain.SelectedOption;

import java.util.List;
import java.util.UUID;

// 옵션 유효성 검사
public interface OptionCheck {
     boolean validate(UUID storeId, String itemCode, List<SelectedOption> selectedOptions);
}
