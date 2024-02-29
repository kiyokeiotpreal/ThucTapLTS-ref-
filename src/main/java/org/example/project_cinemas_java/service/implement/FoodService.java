package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.model.Food;
import org.example.project_cinemas_java.payload.request.food_request.AddFoodRequest;
import org.example.project_cinemas_java.payload.request.food_request.UpdateFoodRequest;
import org.example.project_cinemas_java.repository.FoodRepo;
import org.example.project_cinemas_java.service.iservice.IFood;
import org.example.project_cinemas_java.utils.MessageKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FoodService implements IFood {

    @Autowired
    private FoodRepo foodRepo;
    @Override
    public Food addFood(AddFoodRequest addFoodRequest) throws Exception {
        Optional<Food> food = foodRepo.findByNameOfFood(addFoodRequest.getNameOfFood());
        if(food.isPresent()){
            throw new DataIntegrityViolationException(MessageKeys.FOOD_HAS_ALREADY_EXISTED);
        }
        Food newFood = new Food();
        newFood.setNameOfFood(addFoodRequest.getNameOfFood());
        newFood.setPrice(addFoodRequest.getPrice());
        newFood.setDescription(addFoodRequest.getDescription());
        newFood.setImage(addFoodRequest.getImage());
        newFood.setActive(true);
        foodRepo.save(newFood);
        return newFood;
    }

    @Override
    public Food updateFood(UpdateFoodRequest updateFoodRequest) throws Exception {
        Food food = foodRepo.findById(updateFoodRequest.getId()).orElse(null);
        if(food ==  null){
             throw new DataNotFoundException(MessageKeys.FOOD_NOT_FOUND);
        }
        Optional<Food> food1 = foodRepo.findByNameOfFood(updateFoodRequest.getNameOfFood());
        if(food1.isPresent()){
            throw new DataIntegrityViolationException(MessageKeys.FOOD_HAS_ALREADY_EXISTED);
        }
        food.setNameOfFood(updateFoodRequest.getNameOfFood());
        food.setPrice(updateFoodRequest.getPrice());
        food.setDescription(updateFoodRequest.getDescription());
        food.setImage(updateFoodRequest.getImage());
        foodRepo.save(food);
        return food;
    }

    @Override
    public String deleteFood(int id) throws Exception {
        Food food = foodRepo.findById(id).orElse(null);
        if(food == null){
            throw new DataNotFoundException(MessageKeys.FOOD_NOT_FOUND);
        }
        food.setActive(false);
        foodRepo.save(food);
        return "Delete successfully";
    }
}
