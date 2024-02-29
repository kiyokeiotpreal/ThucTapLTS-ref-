package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.payload.request.seat_request.AddSeatRequest;
import org.example.project_cinemas_java.payload.request.seat_request.UpdateSeatRequest;
import org.example.project_cinemas_java.service.implement.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/admin")
@RequiredArgsConstructor
public class SeatController {
    @Autowired
    private SeatService seatService;

    @PostMapping("/addSeat")
    public ResponseEntity<?> addSeat(@RequestBody AddSeatRequest addSeatRequest){
        try {
            return ResponseEntity.ok().body(seatService.addSeat(addSeatRequest));
        }catch (DataNotFoundException | DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/updateSeat")
    public ResponseEntity<?> updateSeat(@RequestBody UpdateSeatRequest updateSeatRequest){
        try {
            return ResponseEntity.ok().body(seatService.updateSeat(updateSeatRequest));

        }catch (DataNotFoundException | DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
