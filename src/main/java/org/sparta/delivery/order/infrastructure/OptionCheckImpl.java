package org.sparta.delivery.order.infrastructure;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.order.domain.SelectedOption;
import org.sparta.delivery.order.domain.service.OptionCheck;
import org.sparta.delivery.store.domain.Product;
import org.sparta.delivery.store.domain.ProductOption;
import org.sparta.delivery.store.domain.ProductSubOption;
import org.sparta.delivery.store.domain.StoreId;
import org.sparta.delivery.store.domain.exception.ProductNotFoundException;
import org.sparta.delivery.store.domain.query.ProductQueryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OptionCheckImpl implements OptionCheck {

    private final ProductQueryRepository productQueryRepository;

    @Override
    public boolean validate(UUID storeId, String itemCode, List<SelectedOption> selectedOptions) {
        // 선택된 옵션이 없으면 검증할 대상이 없으므로 통과
        if (selectedOptions == null || selectedOptions.isEmpty()) {
            return true;
        }

        Product product = productQueryRepository.findByProductCode(StoreId.of(storeId), itemCode)
                .orElseThrow(ProductNotFoundException::new);

        List<ProductOption> dbOptions = product.getOptions();

        // 상품에 옵션이 없는데 사용자가 옵션을 선택해서 보낸 경우 거절
        if (dbOptions == null || dbOptions.isEmpty()) {
            return false;
        }

        // 효율적인 매칭을 위해 DB 옵션을 Map으로 변환 (Key: 옵션명)
        Map<String, ProductOption> dbOptionMap = dbOptions.stream()
                .collect(Collectors.toMap(ProductOption::getName, o -> o));

        // 모든 선택된 옵션이 유효해야 함 (allMatch)
        return selectedOptions.stream().allMatch(so -> {
            ProductOption dbOpt = dbOptionMap.get(so.getOptionName());

            // 옵션명이 존재하지 않거나 가격이 조작된 경우
            if (dbOpt == null || dbOpt.getPrice().getValue() != so.getOptionPrice()) {
                return false;
            }

            // 하위 옵션 검증 (있는 경우에만)
            List<SelectedOption.SelectedSubOption> sSubOptions = so.getSubOptions();
            if (sSubOptions == null || sSubOptions.isEmpty()) {
                return true;
            }

            // DB의 하위 옵션도 Map으로 변환하여 검증
            Map<String, ProductSubOption> dbSubMap = dbOpt.getSubOptions().stream()
                    .collect(Collectors.toMap(ProductSubOption::name, s -> s));

            return sSubOptions.stream().allMatch(sSub -> {
                ProductSubOption dbSub = dbSubMap.get(sSub.getName());
                return dbSub != null && dbSub.addPrice() == sSub.getAddPrice();
            });
        });
    }
}
