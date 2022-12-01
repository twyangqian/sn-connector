package com.thoughtworks.otr.snconnector.utils;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtils {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final ZoneId UTC_TIME_ZONE = ZoneId.of("UTC");
    public static final ZoneId DEFAULT_TIME_ZONE = ZoneId.of("Asia/Shanghai");
    private static final Integer DATE_STRING_LENGTH = 10;


    public static Instant stringToInstant(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (value.length() > DATE_STRING_LENGTH) {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER)
                                .atZone(DEFAULT_TIME_ZONE)
                                .toInstant();
        }
        return LocalDate.parse(value, DATE_FORMATTER).atStartOfDay(DEFAULT_TIME_ZONE).toInstant();
    }

    public static String instantToString(Instant value) {
        if (Objects.isNull(value)) {
            return null;
        }
        return DATE_TIME_FORMATTER.withZone(DEFAULT_TIME_ZONE).format(value);
    }

    public static LocalDateTime stringToLocalDateTime(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
    }

    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(DEFAULT_TIME_ZONE).toInstant());
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
       return date.toInstant().atZone(UTC_TIME_ZONE).toLocalDateTime();
    }

    public static LocalDateTime nowadays() {
        return LocalDateTime.now();
    }


    public static boolean isWeekend(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == 1 || dayOfWeek == 7;
    }

    public static Date getDueDateByDurationFromNowInWorkDays(Duration duration) {
        AtomicReference<LocalDateTime> currentDueTime = new AtomicReference<>(nowadays().atZone(UTC_TIME_ZONE).toLocalDateTime());
        AtomicReference<LocalDateTime> currentEndWorkTime = new AtomicReference<>(currentDueTime.get().toLocalDate().atStartOfDay().plusHours(18));
        AtomicReference<Duration> leftDuration = new AtomicReference<>(duration);
        while(!leftDuration.get().isZero()) {
            if (isWeekend(localDateTimeToDate(currentDueTime.get()))) {
                currentEndWorkTime.set(getNextEndWorkDay(currentDueTime.get()));
                currentDueTime.set(getNextStartWorkDay(currentDueTime.get()));
            } else {
                Duration currentWorkDayLeftTime = Duration.between(currentDueTime.get(), currentEndWorkTime.get());
                if (leftDuration.get().compareTo(currentWorkDayLeftTime) > 0) {
                    leftDuration.set(leftDuration.get().minus(currentWorkDayLeftTime));
                    currentEndWorkTime.set(getNextEndWorkDay(currentDueTime.get()));
                    currentDueTime.set(getNextStartWorkDay(currentDueTime.get()));
                } else {
                    currentDueTime.set(currentDueTime.get().plus(leftDuration.get()));
                    leftDuration.set(Duration.ZERO);
                    break;
                }
            }

        }
        return localDateTimeToDate(currentDueTime.get());
    }

    public static LocalDateTime getNextStartWorkDay(LocalDateTime currentDate) {
        return currentDate.toLocalDate().atStartOfDay().plusDays(1).plusHours(9);
    }

    public static LocalDateTime getNextEndWorkDay(LocalDateTime currentDate) {
        return currentDate.toLocalDate().atStartOfDay().plusDays(1).plusHours(18);
    }
}
