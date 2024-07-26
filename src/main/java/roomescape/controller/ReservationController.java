package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import roomescape.domain.Reservation;
import roomescape.exception.NotFoundException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class ReservationController {

    private AtomicLong index = new AtomicLong(1);
    private List<Reservation> reservations = new ArrayList<>();

    // 예약 관리 페이지 응답
    @GetMapping("/reservation")
    public String reservation() {
        return "reservation";
    }

    // 예약 목록 조회 API
    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> reservations() {
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

        Reservation newReservation = Reservation.toEntity(reservation, index.getAndIncrement());
        reservations.add(newReservation);
        return ResponseEntity.created(URI.create("/reservations/" + newReservation.getId())).body(newReservation);
    }

    // 예약 취소 API 구현
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable long id) {
        Reservation reservation = reservations.stream()
                .filter(it -> Objects.equals(it.getId(), id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        reservations.remove(reservation);

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    public ResponseEntity<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().build();
    }
}
