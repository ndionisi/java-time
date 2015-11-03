package com.github.ndionisi.datetime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.TypeMismatchDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.*;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JavaTimeApplication.class)
@Transactional
public class PostgresqlDateTimeITest {

    @Inject
    private JdbcTemplate jdbcTemplate;

    private static final ZoneOffset PLUS_TWO_HOURS_OFFSET = ZoneOffset.ofHours(2);
    private static final ZoneId EUROPE_PARIS_TIMEZONE = ZoneId.of("Europe/Paris");

    @Test
    public void instant() {
        Instant instant = Instant.parse("2015-10-23T06:56:08Z");
        jdbcTemplate.update("INSERT INTO date_time (timestamp_with_timezone) VALUES (?)", Date.from(instant));

        Timestamp readTimestamp = jdbcTemplate.queryForObject("SELECT timestamp_with_timezone FROM date_time", Timestamp.class);
        Instant readInstant = readTimestamp.toInstant();
        assertThat(readInstant, equalTo(instant));
    }

    @Test
    public void offsetDateTime() {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2015-10-23T08:56:08+02:00");
        jdbcTemplate.update("INSERT INTO date_time (timestamp_with_timezone) VALUES (?)", Date.from(offsetDateTime.toInstant()));

        Timestamp readTimestamp = jdbcTemplate.queryForObject("SELECT timestamp_with_timezone FROM date_time", Timestamp.class);
        OffsetDateTime readOffsetDateTime = readTimestamp.toInstant().atOffset(PLUS_TWO_HOURS_OFFSET);
        assertThat(readOffsetDateTime, equalTo(offsetDateTime));
    }

    @Test
    public void zonedDateTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2015-10-23T08:56:08+02:00[Europe/Paris]");
        jdbcTemplate.update("INSERT INTO date_time (timestamp_with_timezone) VALUES (?)", Date.from(zonedDateTime.toInstant()));

        Timestamp readTimestamp = jdbcTemplate.queryForObject("SELECT timestamp_with_timezone FROM date_time", Timestamp.class);
        ZonedDateTime readZonedDateTime = readTimestamp.toInstant().atZone(EUROPE_PARIS_TIMEZONE);
        assertThat(readZonedDateTime, equalTo(zonedDateTime));
    }

    @Test
    public void readDateTimeTypes() throws Exception {
        String timestampWithoutTimezone = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITHOUT TIME ZONE '2015-10-23 06:56:08'", String.class);
        String timestampWithTimezoneOffset = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08+02'", String.class);
        String timestampWithTimezoneTimezone = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08 Europe/Paris'", String.class);

        assertThat(timestampWithoutTimezone, equalTo("2015-10-23 06:56:08"));
        assertThat(timestampWithTimezoneOffset, equalTo("2015-10-23 08:56:08+02"));
        assertThat(timestampWithTimezoneTimezone, equalTo("2015-10-23 08:56:08+02"));

    }

    @Test
    public void compareTimestampWithTimezone() throws Exception {
        Boolean offsetAndTimezoneEquals = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08+02' = TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08 Europe/Paris'", Boolean.class);
        assertThat(offsetAndTimezoneEquals, is(true));
    }

    @Test
    public void computeWithTimestampTypesEuropeParis() throws Exception {
        // In timezone Europe/Paris, there was Daily Saving Time changes. At 2015-10-25T03:00:00+02:00, the clock was
        // shift to 2015-10-25T03:00:00+01:00

        jdbcTemplate.execute("SET TIME ZONE 'Europe/Paris'");
        String timestampWithoutTimezone = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITHOUT TIME ZONE '2015-10-23 06:56:08' + '5 DAYS'", String.class);
        String timestampWithTimezoneOffset = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08+02' + '5 DAYS'", String.class);
        String timestampWithTimezoneTimezone = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08 Europe/Paris' + '5 DAYS'", String.class);

        assertThat(timestampWithoutTimezone, equalTo("2015-10-28 06:56:08"));
        assertThat(timestampWithTimezoneOffset, equalTo("2015-10-28 08:56:08+01"));
        assertThat(timestampWithTimezoneTimezone, equalTo("2015-10-28 08:56:08+01"));
    }

    @Test
    public void computeWithTimestampTypesEuropeLondonAfricaAlgier() throws Exception {
        // There was no Daily Saving Time changes in Africa/Algiers between 2015-10-23 and 2015-10-28
        jdbcTemplate.execute("SET TIME ZONE 'Africa/Algiers'");
        String timestampWithoutTimezone = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITHOUT TIME ZONE '2015-10-23 06:56:08' + '5 DAYS'", String.class);
        String timestampWithTimezoneOffset = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08+01' + '5 DAYS'", String.class);
        String timestampWithTimezoneTimezone = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08 Africa/Algiers' + '5 DAYS'", String.class);

        assertThat(timestampWithoutTimezone, equalTo("2015-10-28 06:56:08"));
        assertThat(timestampWithTimezoneOffset, equalTo("2015-10-28 08:56:08+01"));
        assertThat(timestampWithTimezoneTimezone, equalTo("2015-10-28 08:56:08+01"));
    }

    @Test
    public void checkOffsetAccordingToTimezoneAsString() throws Exception {
        jdbcTemplate.execute("SET TIME ZONE 'Europe/Paris'");
        String parisTimestamp = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08+02'", String.class);

        jdbcTemplate.execute("SET TIME ZONE 'America/Los_Angeles'");
        String losAngelesTimestamp = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08+02'", String.class);

        assertThat(parisTimestamp, equalTo("2015-10-23 08:56:08+02"));
        assertThat(losAngelesTimestamp, equalTo("2015-10-22 23:56:08-07"));
    }

    @Test
    public void checkOffsetAccordingToTimezoneAsTimestamp() throws Exception {
        jdbcTemplate.execute("SET TIME ZONE 'Europe/Paris'");
        Timestamp parisTimestamp = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08+02'", Timestamp.class);

        jdbcTemplate.execute("SET TIME ZONE 'America/Los_Angeles'");
        Timestamp losAngelesTimestamp = jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08+02'", Timestamp.class);

        assertThat(parisTimestamp, equalTo(losAngelesTimestamp));
    }

    @Test(expected = TypeMismatchDataAccessException.class)
    public void java8InstantJdbcSupport() throws Exception {
        jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08+02'", Instant.class);
    }

    @Test(expected = TypeMismatchDataAccessException.class)
    public void java8OffsetDateTimeJdbcSupport() throws Exception {
        jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08+02'", OffsetDateTime.class);
    }

    @Test(expected = TypeMismatchDataAccessException.class)
    public void java8ZonedDateTimeJdbcSupport() throws Exception {
        jdbcTemplate.queryForObject("SELECT TIMESTAMP WITH TIME ZONE '2015-10-23 08:56:08+02'", ZonedDateTime.class);
    }
}
