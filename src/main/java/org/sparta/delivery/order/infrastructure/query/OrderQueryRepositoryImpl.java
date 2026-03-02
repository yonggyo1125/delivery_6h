package org.sparta.delivery.order.infrastructure.query;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.order.domain.Order;
import org.sparta.delivery.order.domain.OrderId;
import org.sparta.delivery.order.domain.query.OrderQueryDto;
import org.sparta.delivery.order.domain.query.OrderQueryRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepositoryImpl implements OrderQueryRepository {
    @Override
    public Optional<Order> findById(OrderId orderId) {
        return Optional.empty();
    }

    @Override
    public Optional<Order> findAllByStore(UUID storeId, OrderQueryDto.Search search, Pageable pageable) {
        return Optional.empty();
    }

    @Override
    public Optional<Order> findAllByUser(UUID userId, OrderQueryDto.Search search, Pageable pageable) {
        return Optional.empty();
    }

    @Override
    public Optional<Order> findAll(OrderQueryDto.Search search, Pageable pageable) {
        return Optional.empty();
    }
}
