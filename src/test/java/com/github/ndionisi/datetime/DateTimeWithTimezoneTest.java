package com.github.ndionisi.datetime;

import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTimeWithTimezoneTest {

    private static final ZoneOffset PLUS_TWO_HOURS_OFFSET = ZoneOffset.ofHours(2);
    private static final ZoneId EUROPE_PARIS_TIMEZONE = ZoneId.of("Europe/Paris");

    @Test
    public void compareDateTimeObjects() throws Exception {
        Instant instant = Instant.parse("2015-10-23T06:56:08Z");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2015-10-23T08:56:08+02:00");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-10-23T08:56:08+02:00[Europe/Paris]");

        assertThat(instant.atOffset(PLUS_TWO_HOURS_OFFSET)).isEqualTo(offsetDateTime);
        assertThat(instant.atZone(EUROPE_PARIS_TIMEZONE)).isEqualTo(zonedDateTime);

        assertThat(offsetDateTime.toInstant()).isEqualTo(instant);
        assertThat(offsetDateTime.atZoneSameInstant(EUROPE_PARIS_TIMEZONE)).isEqualTo(zonedDateTime);

        assertThat(zonedDateTime.toInstant()).isEqualTo(instant);
        assertThat(zonedDateTime.toOffsetDateTime()).isEqualTo(offsetDateTime);
    }

    @Test
    public void computeWithDateTimeObjects() throws Exception {
        // In timezone Europe/Paris, there was Daily Saving Time changes. At 2015-10-25T03:00:00+02:00, the clock was
        // shift to 2015-10-25T03:00:00+01:00
        Instant instant = Instant.parse("2015-10-23T06:56:08Z");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2015-10-23T08:56:08+02:00");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-10-23T08:56:08+02:00[Europe/Paris]");

        assertThat(zonedDateTime.toInstant())
                .isEqualTo(offsetDateTime.toInstant())
                .isEqualTo(instant);

        Instant instantPlus5Days = instant.plus(5, ChronoUnit.DAYS);
        OffsetDateTime offsetPlus5Days = offsetDateTime.plus(5, ChronoUnit.DAYS);
        ZonedDateTime zonedDateTimePlus5Days = zonedDateTime.plus(5, ChronoUnit.DAYS);

        assertThat(instantPlus5Days).isEqualTo(offsetPlus5Days.toInstant());
        assertThat(instantPlus5Days).isNotEqualTo(zonedDateTimePlus5Days.toInstant());
        assertThat(instantPlus5Days).isEqualTo(zonedDateTimePlus5Days.minusHours(1).toInstant());

        assertThat(offsetPlus5Days.toInstant()).isNotEqualTo(zonedDateTimePlus5Days.toInstant());
        assertThat(offsetPlus5Days.toInstant()).isEqualTo(zonedDateTimePlus5Days.minusHours(1).toInstant());
    }

    @Test
    public void equalsWithDifferentOffsets() throws Exception {
        Instant instant = Instant.parse("2015-10-23T06:56:08Z");
        OffsetDateTime offsetDateTime1 = OffsetDateTime.parse("2015-10-23T08:56:08+02:00");
        OffsetDateTime offsetDateTime2 = OffsetDateTime.parse("2015-10-23T07:56:08+01:00");

        assertThat(offsetDateTime1).isNotEqualTo(offsetDateTime2);
        assertThat(offsetDateTime1.toInstant()).isEqualTo(offsetDateTime2.toInstant());
        assertThat(offsetDateTime1.toInstant()).isEqualTo(instant);
    }

    @Test
    public void durationWithDateTimeObjects() throws Exception {
        ZonedDateTime zonedDateTime1 = ZonedDateTime.parse("2015-10-23T08:56:08+02:00[Europe/Paris]");
        OffsetDateTime offsetDateTime1 = OffsetDateTime.from(zonedDateTime1);
        Instant instant1 = Instant.from(zonedDateTime1);

        assertThat(zonedDateTime1.toInstant())
                .isEqualTo(offsetDateTime1.toInstant())
                .isEqualTo(instant1);

        ZonedDateTime zonedDateTime2 = ZonedDateTime.parse("2015-10-28T08:56:08+02:00[Europe/Paris]");
        OffsetDateTime offsetDateTime2 = OffsetDateTime.from(zonedDateTime2);
        Instant instant2 = Instant.from(zonedDateTime2);

        assertThat(zonedDateTime2.toInstant())
                .isEqualTo(offsetDateTime2.toInstant())
                .isEqualTo(instant2);

        Duration durationZonedDateTime = Duration.between(zonedDateTime2, zonedDateTime1);
        Duration durationOffsetDateTime = Duration.between(offsetDateTime2, offsetDateTime1);
        Duration durationInstant = Duration.between(instant2, instant1);

        assertThat(durationZonedDateTime)
                .isEqualTo(durationOffsetDateTime)
                .isEqualTo(durationInstant);
    }
}
