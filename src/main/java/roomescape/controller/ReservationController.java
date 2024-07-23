package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import roomescape.domain.Reservation;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ReservationController {

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

}
