package web.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import web.domain.Reservation;
import web.domain.Theme;
import web.dto.response.ReservationIdDto;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.util.List;

@Repository
public class ReservationJdbcRepository {

    public ReservationJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    public List<Long> findAllReservationWithDateAndTime(Reservation reservation) {

        String selectSql = "SELECT id FROM reservation WHERE date = (?) AND time = (?) LIMIT 1 ";

        return jdbcTemplate.query(selectSql, ((rs, rowNum) ->
                rs.getLong("id")), Date.valueOf(reservation.getDate()), Time.valueOf(reservation.getTime()));
    }

    public ReservationIdDto createReservation(Reservation reservation) {
        String sql = "INSERT INTO reservation (date, time, name, theme_name, theme_desc, theme_price) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final Theme theme = reservation.getTheme();

        this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setDate(1, Date.valueOf(reservation.getDate()));
            ps.setTime(2, Time.valueOf(reservation.getTime()));
            ps.setString(3, reservation.getName());
            ps.setString(4, theme.getName());
            ps.setString(5, theme.getDesc());
            ps.setInt(6, theme.getPrice());
            return ps;
        }, keyHolder);

        return new ReservationIdDto((Long) keyHolder.getKey());
    }
}
