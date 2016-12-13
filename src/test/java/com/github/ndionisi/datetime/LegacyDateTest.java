package com.github.ndionisi.datetime;

import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

public class LegacyDateTest {

    @Test
    public void computeWithDateLegacyDateObjectsHonorDailySavingTimeChanges() throws Exception {
        // In timezone Europe/Paris, there was Daily Saving Time changes. At 2015-10-25T03:00:00+02:00, the clock was
        // shift to 2015-10-25T03:00:00+01:00
        Calendar calendar = getCalendar(2015, Calendar.OCTOBER, 23, 8, 56, 8);
        calendar.add(Calendar.DAY_OF_YEAR, 5);

        Date computedDatePlus5Days = calendar.getTime();

        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-10-23T08:56:08+02:00[Europe/Paris]");
        ZonedDateTime zonedDateTimePlus5Days = zonedDateTime.plusDays(5);

        assertThat(computedDatePlus5Days.toInstant()).isEqualTo(zonedDateTimePlus5Days.toInstant());
    }

    private Calendar getCalendar(int year, int month, int date, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hourOfDay, minute, second);
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
