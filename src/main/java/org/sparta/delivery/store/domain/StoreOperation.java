package org.sparta.delivery.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class StoreOperation {

    private DayOfWeek dayOfWeek; // 운영 요일
    private LocalTime startHour; // 시작 시간
    private LocalTime endHour; // 종료 시간

    @Column(length=20)
    private BreakTime breakHour1; // 휴식 시간1
    @Column(length=20)
    private BreakTime breakHour2; // 휴식 시간2
}
