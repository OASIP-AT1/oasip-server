package com.example.oasipserver.controllers;

import com.example.oasipserver.dtos.CategoryDTO;
import com.example.oasipserver.dtos.EventDTO;
import com.example.oasipserver.entities.Eventcategory;
import com.example.oasipserver.services.EventCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.Valid;
import java.util.List;



@RestController
@CrossOrigin(origins = "http://intproj21.sit.kmutt.ac.th/")
//@CrossOrigin(origins = "*")
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private EventCategoryService service;
    @GetMapping("")
    public List<CategoryDTO> getAllCategory() {
        String email = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("account");
        return service.getAllCategory(email);
    }

    @GetMapping("/{categoryId}/events")
    private List<EventDTO> getEventByCategory(@PathVariable Integer categoryId) {
        return service.getEventByCategory(categoryId);
    }

    @PutMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Eventcategory updateCategory(@Valid @RequestBody Eventcategory updateCategory, @PathVariable Integer categoryId){
        return service.updateCategory(updateCategory , categoryId);
    }
}
