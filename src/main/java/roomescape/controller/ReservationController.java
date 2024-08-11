package roomescape.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import roomescape.domain.Reservation;
import roomescape.exception.NotFoundException;

import java.net.URI;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class ReservationController {

    private AtomicLong index = new AtomicLong(1);
    private List<Reservation> reservations = new ArrayList<>();
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReservationController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 예약 관리 페이지 응답
    @GetMapping("/reservation")
    public String reservation() {
        return "reservation";
    }

    // 예약 목록 조회 API
    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> reservations() {
        String sql = "SELECT id, name, date, time FROM reservation";
        List<Reservation> reservations = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Reservation(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getString("time")
                )
        );
        return ResponseEntity.ok().body(reservations);
    }

    // 예약 추가 API 구현
    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {

        if (reservation.getName() == null || reservation.getName().isEmpty()) {
            throw new IllegalArgumentException("Reservation id cannot be null");
        }
        if (reservation.getDate() == null || reservation.getDate().isEmpty()) {
            throw new IllegalArgumentException("Reservation Date cannot be null");
        }
        if (reservation.getTime() == null || reservation.getTime().isEmpty()) {
            throw new IllegalArgumentException("Reservation Time cannot be null");
        }

//        Reservation newReservation = Reservation.toEntity(reservation, index.getAndIncrement());
//        reservations.add(newReservation);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql, new String[]{"id"});
            ps.setString(1, reservation.getName());
            ps.setString(2, reservation.getDate());
            ps.setString(3, reservation.getTime());
            return ps;
        }, keyHolder);

        Long newId = keyHolder.getKey().longValue();

        Reservation newReservation = new Reservation(newId, reservation.getName(), reservation.getDate(), reservation.getTime());


        return ResponseEntity.created(URI.create("/reservations/" + newReservation.getId())).body(newReservation);
    }

    // 예약 취소 API 구현
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable long id) {
//        Reservation reservation = reservations.stream()
//                .filter(it -> Objects.equals(it.getId(), id))
//                .findFirst()
//                .orElseThrow(() -> new NotFoundException("Reservation not found"));
//
//        reservations.remove(reservation);

        String sql = "DELETE FROM reservation WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);


        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().build();
    }


}
