package org.sparta.delivery.order.domain.service;

import org.sparta.delivery.order.domain.ProductInfo;

import java.util.UUID;

public interface ProductProvider {
    ProductInfo getProduct(UUID storeId, String productCode);
}
