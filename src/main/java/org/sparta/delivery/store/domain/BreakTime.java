package org.sparta.delivery.store.domain;

import java.time.LocalTime;

public record BreakTime(
        LocalTime start,
        LocalTime end
) {}
