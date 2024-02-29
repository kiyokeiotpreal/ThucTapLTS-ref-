package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Seat;
import org.example.project_cinemas_java.payload.request.seat_request.AddSeatRequest;
import org.example.project_cinemas_java.payload.request.seat_request.UpdateSeatRequest;

public interface ISeat {
    Seat addSeat(AddSeatRequest addSeatRequest) throws Exception;
    Seat updateSeat(UpdateSeatRequest updateSeatRequest) throws Exception;
}
