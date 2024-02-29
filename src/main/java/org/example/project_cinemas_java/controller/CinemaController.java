package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Cinema;
import org.example.project_cinemas_java.payload.request.cinema_request.AddCinemeRequest;
import org.example.project_cinemas_java.payload.request.cinema_request.UpdateCinemaRequest;
import org.example.project_cinemas_java.service.implement.CinemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/admin")
@RequiredArgsConstructor
public class CinemaController {
    @Autowired
    private CinemaService cinemaService;

    @PostMapping("/addCinema")
    public ResponseEntity<?>  addCinema(@RequestBody AddCinemeRequest cinema){
        try {
            Cinema newCinema = cinemaService.addCinema(cinema);
            return ResponseEntity.ok().body(newCinema);
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/updateCinema")
    public ResponseEntity<?> updateCinema(@RequestBody UpdateCinemaRequest updateCinemaRequest){
        try {
            Cinema cinema = cinemaService.updateCinema(updateCinemaRequest);
            return ResponseEntity.ok().body(cinema);
        }catch ( DataIntegrityViolationException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
