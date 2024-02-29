package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Cinema;
import org.example.project_cinemas_java.payload.request.cinema_request.AddCinemeRequest;
import org.example.project_cinemas_java.payload.request.cinema_request.UpdateCinemaRequest;
import org.example.project_cinemas_java.repository.CinemaRepo;
import org.example.project_cinemas_java.service.iservice.ICinema;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CinemaService implements ICinema {
    @Autowired
    private CinemaRepo cinemaRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Override
    public Cinema addCinema(AddCinemeRequest addCinemeRequest) throws Exception{
        if(cinemaRepo.existsByCode(addCinemeRequest.getCode())){
            throw new DataIntegrityViolationException(MessageKeys.CODE_ALREADY_EXISTS);
        }
        if(cinemaRepo.existsByAddress(addCinemeRequest.getAddress())){
            throw new DataIntegrityViolationException(MessageKeys.ADDRESS_ALREADY_EXISTS);
        }
        Cinema newCinema = Cinema.builder()
                .address(addCinemeRequest.getAddress())
                .description(addCinemeRequest.getDescription())
                .code(addCinemeRequest.getCode())
                .nameOfCinema(addCinemeRequest.getNameOfCinema())
                .isActive(true)
                .build();
        cinemaRepo.save(newCinema);
        return newCinema;
    }

    @Override
    public Cinema updateCinema(UpdateCinemaRequest updateCinemaRequest) throws Exception {
        Cinema cinema = cinemaRepo.findById(updateCinemaRequest.getId()).orElse(null);
        if(cinema == null){
            throw new DataNotFoundException(MessageKeys.CINEMA_NOT_FOUND);
        }

        if(cinemaRepo.existsCinemaByAddressAndIdNot(updateCinemaRequest.getAddress(), updateCinemaRequest.getId())){
            throw new DataIntegrityViolationException(MessageKeys.ADDRESS_ALREADY_EXISTS);
        }
        if(cinemaRepo.existsCinemaByCodeAndIdNot(updateCinemaRequest.getCode(), updateCinemaRequest.getId())){
            throw new DataIntegrityViolationException(MessageKeys.CODE_ALREADY_EXISTS);
        }
        cinema.setAddress(updateCinemaRequest.getAddress());
        cinema.setNameOfCinema(updateCinemaRequest.getNameOfCinema());
        cinema.setDescription(updateCinemaRequest.getDescription());
        cinema.setCode(updateCinemaRequest.getCode());
        cinemaRepo.save(cinema);

        return cinema;
    }

}
