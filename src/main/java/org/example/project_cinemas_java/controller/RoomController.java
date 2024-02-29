package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.payload.request.room_request.AddRoomRequest;
import org.example.project_cinemas_java.payload.request.room_request.UpdateRoomRequest;
import org.example.project_cinemas_java.service.implement.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/admin")
@RequiredArgsConstructor
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/addRoom")
    public ResponseEntity<?> addRoom(@RequestBody AddRoomRequest addRoomRequest){
        try {
            return ResponseEntity.ok().body(roomService.addRoom(addRoomRequest));

        }catch (DataIntegrityViolationException | DataNotFoundException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/updateRoom")
    public ResponseEntity<?> updateRoom(@RequestBody UpdateRoomRequest updateRoomRequest){
        try {
            return ResponseEntity.ok(roomService.updateRoom(updateRoomRequest));
        }catch (DataNotFoundException | DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
