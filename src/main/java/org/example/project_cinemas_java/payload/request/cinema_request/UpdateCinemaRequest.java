package org.example.project_cinemas_java.payload.request.cinema_request;

import lombok.Data;

@Data
public class UpdateCinemaRequest {
    private int id;

    private String address;

    private String description;

    private String code;

    private String nameOfCinema;
}
