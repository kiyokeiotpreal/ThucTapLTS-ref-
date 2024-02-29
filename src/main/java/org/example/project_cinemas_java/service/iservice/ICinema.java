package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Cinema;
import org.example.project_cinemas_java.payload.request.cinema_request.AddCinemeRequest;
import org.example.project_cinemas_java.payload.request.cinema_request.UpdateCinemaRequest;

public interface ICinema {
    Cinema addCinema(AddCinemeRequest addCinemeRequest) throws Exception;
    Cinema updateCinema(UpdateCinemaRequest updateCinemaRequest) throws Exception;
}
