package com.github.ndionisi.datetime;

import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class DateTimeWithTimezoneTest {

    private static final ZoneOffset PLUS_TWO_HOURS_OFFSET = ZoneOffset.ofHours(2);
    private static final ZoneId EUROPE_PARIS_TIMEZONE = ZoneId.of("Europe/Paris");

    @Test
    public void compareDateTimeObjects() throws Exception {
        Instant instant = Instant.parse("2015-10-23T06:56:08Z");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2015-10-23T08:56:08+02:00");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-10-23T08:56:08+02:00[Europe/Paris]");

        assertThat(instant.atOffset(PLUS_TWO_HOURS_OFFSET), equalTo(offsetDateTime));
        assertThat(instant.atZone(EUROPE_PARIS_TIMEZONE), equalTo(zonedDateTime));

        assertThat(offsetDateTime.toInstant(), equalTo(instant));
        assertThat(offsetDateTime.atZoneSameInstant(EUROPE_PARIS_TIMEZONE), equalTo(zonedDateTime));

        assertThat(zonedDateTime.toInstant(), equalTo(instant));
        assertThat(zonedDateTime.toOffsetDateTime(), equalTo(offsetDateTime));
    }

    @Test
    public void computeWithDateTimeObjects() throws Exception {
        // In timezone Europe/Paris, there was Daily Saving Time changes. At 2015-10-25T03:00:00+02:00, the clock was
        // shift to 2015-10-25T03:00:00+01:00
        Instant instant = Instant.parse("2015-10-23T06:56:08Z");
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2015-10-23T08:56:08+02:00");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-10-23T08:56:08+02:00[Europe/Paris]");

        Instant instantPlus5Days = instant.plus(5, ChronoUnit.DAYS);
        OffsetDateTime offsetPlus5Days = offsetDateTime.plus(5, ChronoUnit.DAYS);
        ZonedDateTime zonedDateTimePlus5Days = zonedDateTime.plus(5, ChronoUnit.DAYS);

        assertThat(instantPlus5Days.atOffset(PLUS_TWO_HOURS_OFFSET), equalTo(offsetPlus5Days));
        assertThat(instantPlus5Days.atZone(EUROPE_PARIS_TIMEZONE), not(equalTo(zonedDateTimePlus5Days)));
        assertThat(instantPlus5Days.atZone(EUROPE_PARIS_TIMEZONE), equalTo(zonedDateTimePlus5Days.minusHours(1)));

        assertThat(offsetPlus5Days.atZoneSameInstant(EUROPE_PARIS_TIMEZONE), not(equalTo(zonedDateTimePlus5Days)));
        assertThat(offsetPlus5Days.atZoneSameInstant(EUROPE_PARIS_TIMEZONE), equalTo(zonedDateTimePlus5Days.minusHours(1)));
    }

    @Test
    public void equalsWithDifferentOffsets() throws Exception {
        Instant instant = Instant.parse("2015-10-23T06:56:08Z");
        OffsetDateTime offsetDateTime1 = OffsetDateTime.parse("2015-10-23T08:56:08+02:00");
        OffsetDateTime offsetDateTime2 = OffsetDateTime.parse("2015-10-23T07:56:08+01:00");

        assertThat(offsetDateTime1, not(equalTo(offsetDateTime2)));
        assertThat(offsetDateTime1.toInstant(), equalTo(offsetDateTime2.toInstant()));
        assertThat(offsetDateTime1.toInstant(), equalTo(instant));
    }
}
