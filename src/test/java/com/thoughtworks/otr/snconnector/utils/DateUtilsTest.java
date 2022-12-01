package com.thoughtworks.otr.snconnector.utils;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

class DateUtilsTest {

    @Test
    void should_return_due_date_when_curren_day_is_saturday_and_duration_less_than_9() {
        // current date 2022-12-03 12:00:00 duration 2
        // end date 2022-12-05 11:00:00
        LocalDateTime mockNowadays = DateUtils.stringToLocalDateTime("2022-12-03 12:00:00");
        try(MockedStatic<DateUtils> mockDateUtil = mockStatic(DateUtils.class, Mockito.CALLS_REAL_METHODS)) {
            mockDateUtil.when(DateUtils::nowadays).thenReturn(mockNowadays);
            assertEquals(DateUtils.localDateTimeToDate(mockNowadays.plusDays(2).minusHours(1)),
                    DateUtils.getDueDateByDurationFromNowInWorkDays(Duration.of(2, ChronoUnit.HOURS)));
        }
    }

    @Test
    void should_return_due_date_when_curren_day_is_saturday_and_duration_more_than_9() {
        // current date 2022-12-03 12:00:00 duration 10
        // end date 2022-12-06 10:00:00
        LocalDateTime mockNowadays = DateUtils.stringToLocalDateTime("2022-12-03 12:00:00");
        try(MockedStatic<DateUtils> mockDateUtil = mockStatic(DateUtils.class, Mockito.CALLS_REAL_METHODS)) {
            mockDateUtil.when(DateUtils::nowadays).thenReturn(mockNowadays);
            assertEquals(DateUtils.localDateTimeToDate(mockNowadays.plusDays(3).minusHours(2)),
                    DateUtils.getDueDateByDurationFromNowInWorkDays(Duration.of(10, ChronoUnit.HOURS)));
        }
    }

    @Test
    void should_return_due_date_when_curren_day_is_saturday_and_duration_more_than_30() {
        // current date 2022-12-03 12:00:00 duration 32
        // end date 2022-12-08 14:00:00
        LocalDateTime mockNowadays = DateUtils.stringToLocalDateTime("2022-12-03 12:00:00");
        try(MockedStatic<DateUtils> mockDateUtil = mockStatic(DateUtils.class, Mockito.CALLS_REAL_METHODS)) {
            mockDateUtil.when(DateUtils::nowadays).thenReturn(mockNowadays);
            assertEquals(DateUtils.localDateTimeToDate(mockNowadays.plusDays(5).plusHours(2)),
                    DateUtils.getDueDateByDurationFromNowInWorkDays(Duration.of(32, ChronoUnit.HOURS)));
        }
    }

    @Test
    void should_return_due_date_when_current_day_is_not_weekend_and_duration_less_than_9() {
        // current date 2022-11-30 12:00:00 duration 2
        // end date 2022-11-30 14:00:00
        LocalDateTime mockNowadays = DateUtils.stringToLocalDateTime("2022-11-30 12:00:00");
        try (MockedStatic<DateUtils> mockDateUtil = mockStatic(DateUtils.class, Mockito.CALLS_REAL_METHODS)) {
            mockDateUtil.when(DateUtils::nowadays).thenReturn(mockNowadays);
            assertEquals(DateUtils.localDateTimeToDate(mockNowadays.plusHours(2)),
                    DateUtils.getDueDateByDurationFromNowInWorkDays(Duration.of(2, ChronoUnit.HOURS)));
        }
    }

    @Test
    void should_return_due_date_when_current_day_is_not_weekend_and_duration_more_than_9() {
        // current date 2022-11-30 12:00:00 duration 10
        // end date 2022-12-01 13:00:00
        LocalDateTime mockNowadays = DateUtils.stringToLocalDateTime("2022-11-30 12:00:00");
        try (MockedStatic<DateUtils> mockDateUtil = mockStatic(DateUtils.class, Mockito.CALLS_REAL_METHODS)) {
            mockDateUtil.when(DateUtils::nowadays).thenReturn(mockNowadays);
            assertEquals(DateUtils.localDateTimeToDate(mockNowadays.plusDays(1).plusHours(1)),
                    DateUtils.getDueDateByDurationFromNowInWorkDays(Duration.of(10, ChronoUnit.HOURS)));
        }
    }

    @Test
    void should_return_due_date_when_current_day_is_not_weekend_and_duration_more_than_32() {
        // current date 2022-11-30 12:00:00 duration 32
        // end date 2022-12-05 17:00:00
        LocalDateTime mockNowadays = DateUtils.stringToLocalDateTime("2022-11-30 12:00:00");
        try (MockedStatic<DateUtils> mockDateUtil = mockStatic(DateUtils.class, Mockito.CALLS_REAL_METHODS)) {
            mockDateUtil.when(DateUtils::nowadays).thenReturn(mockNowadays);
            assertEquals(DateUtils.localDateTimeToDate(mockNowadays.plusDays(5).plusHours(5)),
                    DateUtils.getDueDateByDurationFromNowInWorkDays(Duration.of(32, ChronoUnit.HOURS)));
        }
    }
}
