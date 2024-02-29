package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.payload.request.food_request.AddFoodRequest;
import org.example.project_cinemas_java.payload.request.food_request.UpdateFoodRequest;
import org.example.project_cinemas_java.service.implement.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/admin")
@RequiredArgsConstructor
public class FoodController {
    @Autowired
    private FoodService foodService;

    @PostMapping("/addFood")
    public ResponseEntity<?> addFood(@RequestBody AddFoodRequest addFoodRequest){
        try {
            return ResponseEntity.ok().body(foodService.addFood(addFoodRequest));
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/updateFood")
    public ResponseEntity<?> updateFood(@RequestBody UpdateFoodRequest updateFoodRequest){
        try {
            return ResponseEntity.ok().body(foodService.updateFood(updateFoodRequest));
        }catch (DataIntegrityViolationException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }catch ( DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteFood")
    public ResponseEntity<?> deleteFood(@RequestParam int id){
        try {
            return ResponseEntity.ok().body(foodService.deleteFood(id));
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
