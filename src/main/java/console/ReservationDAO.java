package console;

import web.domain.Reservation;
import web.domain.Theme;

import java.sql.*;
import java.util.Optional;

public class ReservationDAO {

    public void addReservation(Reservation reservation) {
        try(Connection con = DriverManager.getConnection("jdbc:h2:~/test;AUTO_SERVER=true", "sa", "")) {
            System.out.println("정상적으로 연결되었습니다.");
            String sql = "INSERT INTO reservation (date, time, name, theme_name, theme_desc, theme_price) VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setDate(1, Date.valueOf(reservation.getDate()));
            ps.setTime(2, Time.valueOf(reservation.getTime()));
            ps.setString(3, reservation.getName());
            ps.setString(4, reservation.getTheme().getName());
            ps.setString(5, reservation.getTheme().getDesc());
            ps.setInt(6, reservation.getTheme().getPrice());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("연결 오류:" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Optional<Reservation> findById(Long reservationId) {
//        Connection con = null;

        // 드라이버 연결
        try(Connection con = DriverManager.getConnection("jdbc:h2:~/test;AUTO_SERVER=true", "sa", "")) {

            System.out.println("정상적으로 연결되었습니다.");
            String sql = "SELECT * FROM reservation WHERE id=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, reservationId);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return Optional.empty();
            }

            final Theme theme = new Theme(rs.getString(5),
                    rs.getString(6),
                    rs.getInt(7));

            final Reservation reservation = new Reservation(
                    rs.getLong(1),
                    rs.getDate(2).toLocalDate(),
                    rs.getTime(3).toLocalTime(),
                    rs.getString(4),
                    theme
            );

            return Optional.of(reservation);
        } catch (SQLException e) {
            System.err.println("연결 오류:" + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean deleteById(Long reservationId) {
        try(Connection con = DriverManager.getConnection("jdbc:h2:~/test;AUTO_SERVER=true", "sa", "")) {

            System.out.println("정상적으로 연결되었습니다.");
            String sql = "DELETE FROM reservation WHERE id=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, reservationId);

            int rowNum = ps.executeUpdate();
            con.close();

            return rowNum > 0;
        } catch (SQLException e) {
            System.err.println("연결 오류:" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
