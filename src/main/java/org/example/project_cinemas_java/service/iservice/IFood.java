package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.model.Food;
import org.example.project_cinemas_java.payload.request.food_request.AddFoodRequest;
import org.example.project_cinemas_java.payload.request.food_request.UpdateFoodRequest;

public interface IFood {
    Food addFood(AddFoodRequest addFoodRequest) throws Exception;
    Food updateFood(UpdateFoodRequest updateFoodRequest) throws Exception;
    String deleteFood(int id) throws Exception;
}
