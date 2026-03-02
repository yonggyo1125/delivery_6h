package org.sparta.delivery.order.application;

import lombok.RequiredArgsConstructor;
import org.sparta.delivery.order.application.dto.OrderServiceDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderService {


    @Transactional
    @PreAuthorize("hasRole('USER')")
    public UUID create(OrderServiceDto.Create dto) {

        return null;
    }
}