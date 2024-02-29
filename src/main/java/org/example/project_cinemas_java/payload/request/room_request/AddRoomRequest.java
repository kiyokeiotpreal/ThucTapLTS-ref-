package org.example.project_cinemas_java.payload.request.room_request;

import lombok.Data;

@Data
public class AddRoomRequest {
    private int capacity;

    private int type;

    private String description;

    private String code;

    private String name;

    private int cinemaID;
}
