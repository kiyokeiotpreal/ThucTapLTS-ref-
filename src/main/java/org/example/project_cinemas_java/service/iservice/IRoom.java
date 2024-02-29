package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Room;
import org.example.project_cinemas_java.payload.request.room_request.AddRoomRequest;
import org.example.project_cinemas_java.payload.request.room_request.UpdateRoomRequest;

public interface IRoom {
    public Room addRoom(AddRoomRequest addRoomRequest) throws Exception;
    public Room updateRoom(UpdateRoomRequest updateRoomRequest) throws Exception;
}
