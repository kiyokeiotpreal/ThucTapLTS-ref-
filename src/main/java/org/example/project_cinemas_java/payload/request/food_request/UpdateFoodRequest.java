package org.example.project_cinemas_java.payload.request.food_request;

import lombok.Data;

@Data
public class UpdateFoodRequest {
    private int id;

    private double price;

    private String description;

    private String image;

    private String nameOfFood;

}
