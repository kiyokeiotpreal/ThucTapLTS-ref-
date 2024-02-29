package org.example.project_cinemas_java.payload.request.seat_request;

import lombok.Data;

@Data
public class AddSeatRequest {
    private int number;
    private int seatStatusID;
    private String line;
    private int roomID;
    private int seatTypeID;

}
