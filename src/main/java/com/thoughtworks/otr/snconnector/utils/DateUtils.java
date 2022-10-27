package com.thoughtworks.otr.snconnector.utils;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtils {

    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    public static final ZoneId UTC_TIME_ZONE = ZoneId.of("UTC");


    public static LocalDateTime stringToLocalDateTime(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
    }

    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(UTC_TIME_ZONE).toInstant());
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
       return date.toInstant().atZone(UTC_TIME_ZONE).toLocalDateTime();
    }

    public static Duration betweenDurationInWorkDays(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (Objects.isNull(startDateTime) || Objects.isNull(endDateTime)) {
            return null;
        }
        long betweenDay = Duration.between(startDateTime, endDateTime).toDays();
        LocalDateTime startDate = startDateTime.toLocalDate().atStartOfDay();
        LocalDateTime endDate = endDateTime.toLocalDate().atStartOfDay();
        assert startDate != null;
        assert endDate != null;
        Duration betweenDayDuration = Duration.ofHours(9).multipliedBy(betweenDay);
        if (betweenDay >= 1) {
            return Duration.between(startDateTime, startDate.plusHours(18))
                           .plus(betweenDayDuration)
                           .plus(Duration.between(endDate.plusHours(9), endDateTime));
        }
        return Duration.between(startDateTime, endDateTime);
    }
}
