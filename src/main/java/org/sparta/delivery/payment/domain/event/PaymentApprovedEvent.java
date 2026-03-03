package org.sparta.delivery.payment.domain.event;

import java.util.UUID;

public record PaymentApprovedEvent(
        UUID orderId
) {}
