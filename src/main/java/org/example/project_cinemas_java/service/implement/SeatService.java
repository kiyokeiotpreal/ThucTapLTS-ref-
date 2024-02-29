package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Room;
import org.example.project_cinemas_java.model.Seat;
import org.example.project_cinemas_java.model.SeatStatus;
import org.example.project_cinemas_java.model.SeatType;
import org.example.project_cinemas_java.payload.request.seat_request.AddSeatRequest;
import org.example.project_cinemas_java.payload.request.seat_request.UpdateSeatRequest;
import org.example.project_cinemas_java.repository.RoomRepo;
import org.example.project_cinemas_java.repository.SeatRepo;
import org.example.project_cinemas_java.repository.SeatStatusRepo;
import org.example.project_cinemas_java.repository.SeatTypeRepo;
import org.example.project_cinemas_java.service.iservice.ISeat;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeatService implements ISeat {
    @Autowired
    private SeatRepo seatRepo;
    @Autowired
    private SeatStatusRepo seatStatusRepo;
    @Autowired
    private SeatTypeRepo seatTypeRepo;
    @Autowired
    private RoomRepo roomRepo;


    @Override
    public Seat addSeat(AddSeatRequest addSeatRequest) throws Exception {
        Room room = roomRepo.findById(addSeatRequest.getRoomID()).orElse(null);
        if(room == null){
            throw new DataNotFoundException(MessageKeys.ROOM_NOT_FOUND);
        }
        SeatStatus seatStatus = seatStatusRepo.findById(addSeatRequest.getSeatStatusID()).orElse(null);
        if(seatStatus == null){
            throw new DataNotFoundException(MessageKeys.SEAT_STATUS_NOT_FOUND);
        }
        SeatType seatType = seatTypeRepo.findById(addSeatRequest.getSeatTypeID()).orElse(null);
        if(seatType == null){
            throw new DataNotFoundException(MessageKeys.SEAT_TYPE_NOT_FOUND);
        }
        if(seatRepo.existsByRoomAndLineAndNumber(room,addSeatRequest.getLine(), addSeatRequest.getNumber())){
            throw new DataIntegrityViolationException(MessageKeys.SEAT_HAS_ALREADY_EXISTED);
        }
        Seat seat = new Seat();
        seat.setRoom(room);
        seat.setSeatsStatus(seatStatus);
        seat.setSeatType(seatType);
        seat.setLine(addSeatRequest.getLine());
        seat.setNumber(addSeatRequest.getNumber());
        seatRepo.save(seat);
        return seat;
    }

    @Override
    public Seat updateSeat(UpdateSeatRequest updateSeatRequest) throws Exception {
        Seat seat = seatRepo.findById(updateSeatRequest.getId()).orElse(null);
        if(seat == null){
            throw new DataNotFoundException(MessageKeys.SEAT_NOT_FOUND);
        }
        if(seatRepo.existsByLineAndNumber(updateSeatRequest.getLine(), updateSeatRequest.getNumber())){
            throw new DataIntegrityViolationException(MessageKeys.SEAT_WANT_TO_UPDATE_HAS_ALREADY_EXISTED);
        }
        SeatStatus seatStatus = seatStatusRepo.findById(updateSeatRequest.getSeatStatusID()).orElse(null);
        if(seatStatus == null){
            throw new DataNotFoundException(MessageKeys.SEAT_STATUS_NOT_FOUND);
        }
        SeatType seatType = seatTypeRepo.findById(updateSeatRequest.getSeatTypeID()).orElse(null);
        if(seatType == null){
            throw new DataNotFoundException(MessageKeys.SEAT_TYPE_NOT_FOUND);
        }
        seat.setSeatType(seatType);
        seat.setSeatsStatus(seatStatus);
        seat.setLine(updateSeatRequest.getLine());
        seat.setNumber(updateSeatRequest.getNumber());
        seatRepo.save(seat);

        return seat;
    }
}
