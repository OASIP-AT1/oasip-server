package com.example.oasipserver.controllers;


import com.example.oasipserver.dtos.*;
import com.example.oasipserver.entities.User;
import com.example.oasipserver.jwt.JwtTokenUtil;
import com.example.oasipserver.services.UserService;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://intproj21.sit.kmutt.ac.th/")
//@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService service;
    @Autowired
    private JwtTokenUtil jwtUtil;

    @GetMapping("")
    public List<UserDTO> getAllUsers(){
        return service.getAllUser();
    }
    @GetMapping("/checkUnique")
    public List<UserDTO> getAllUserForCheckUnique(){
        return service.getAllUserForCheckUnique();
    }
    @GetMapping("/{userId}")
    public UserDTO getUserById(@PathVariable Integer userId) {
        return service.getUserDetail(userId);
    }
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody CreateUserDTO newUser)
    {
        return service.save(newUser);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Integer userId)
    {
        service.deleteUser(userId);
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@Valid @RequestBody User updateUser, @PathVariable Integer userId)
    {
        String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
        return service.updateUser(updateUser , userId);
    }
    @PostMapping("/login")
    public JwtDTO match(@Valid @RequestBody MatchUserDTO user) {
        return service.match(user);
    }
    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    public JwtDTO refreshToken(@RequestHeader String refreshToken){
        return service.generateNewToken(refreshToken);
    }

}