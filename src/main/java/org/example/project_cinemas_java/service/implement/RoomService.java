package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Cinema;
import org.example.project_cinemas_java.model.Room;
import org.example.project_cinemas_java.payload.request.room_request.AddRoomRequest;
import org.example.project_cinemas_java.payload.request.room_request.UpdateRoomRequest;
import org.example.project_cinemas_java.repository.CinemaRepo;
import org.example.project_cinemas_java.repository.RoomRepo;
import org.example.project_cinemas_java.service.iservice.IRoom;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService implements IRoom {

    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private CinemaRepo cinemaRepo;
    @Override
    public Room addRoom(AddRoomRequest addRoomRequest) throws Exception {
        if(roomRepo.existsByCode(addRoomRequest.getCode())){
            throw new DataIntegrityViolationException(MessageKeys.CODE_ALREADY_EXISTS);
        }
        Cinema cinema = cinemaRepo.findById(addRoomRequest.getCinemaID()).orElse(null);
        if(cinema == null){
            throw new DataNotFoundException(MessageKeys.CINEMA_NOT_FOUND);
        }
        Room newRoom = new Room();
        newRoom.setCapacity(addRoomRequest.getCapacity());
        newRoom.setActive(true);
        newRoom.setName(addRoomRequest.getName());
        newRoom.setCode(addRoomRequest.getCode());
        newRoom.setCinema(cinema);
        newRoom.setDescription(addRoomRequest.getDescription());
        newRoom.setType(addRoomRequest.getType());
        roomRepo.save(newRoom);
        return newRoom;
    }

    @Override
    public Room updateRoom(UpdateRoomRequest updateRoomRequest) throws Exception {
        Cinema cinema = cinemaRepo.findById(updateRoomRequest.getCinemaID()).orElse(null);
        if(cinema == null){
            throw new DataNotFoundException(MessageKeys.CINEMA_NOT_FOUND);
        }
        Room room = roomRepo.findById(updateRoomRequest.getId()).orElse(null);
        if(room == null){
            throw new DataNotFoundException(MessageKeys.ROOM_NOT_FOUND);
        }
        if(roomRepo.existsByCodeAndIdNot(updateRoomRequest.getCode(), updateRoomRequest.getId())){
            throw new DataIntegrityViolationException(MessageKeys.CODE_ALREADY_EXISTS);
        }

        room.setCapacity(updateRoomRequest.getCapacity());
        room.setType(updateRoomRequest.getType());
        room.setDescription(updateRoomRequest.getDescription());
        room.setCode(updateRoomRequest.getCode());
        room.setName(updateRoomRequest.getName());
        room.setCinema(cinema);
        roomRepo.save(room);

        return room;
    }
}
