package com.example.oasipserver.services;

import com.example.oasipserver.dtos.*;
import com.example.oasipserver.entities.Categoryowner;
import com.example.oasipserver.entities.User;
import com.example.oasipserver.jwt.JwtTokenUtil;
import com.example.oasipserver.jwt.JwtUserDetailsService;
import com.example.oasipserver.repositories.CategoryOwnerRepository;
import com.example.oasipserver.repositories.UserRepository;
import com.example.oasipserver.utils.ListMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private PasswordService passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private CategoryOwnerRepository ownerRepository;

    public List<UserDTO> getAllUser() {
        List<User> userList = repository.findAll(Sort.by("name").ascending());

        return listMapper.mapList(userList, UserDTO.class, modelMapper);
    }

    public List<UserDTO> getAllUserForCheckUnique() {
        List<User> userList = repository.findAll(Sort.by("name").ascending());

        return listMapper.mapList(userList, UserDTO.class, modelMapper);
    }

    public UserDTO getUserDetail(Integer UserId) {
        User user = repository.findById(UserId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User id " + UserId + " does not exist!!"));
        return modelMapper.map(user, UserDTO.class);
    }

    public User save(CreateUserDTO newUser) {
        User user = modelMapper.map(newUser, User.class);
        user.setName(newUser.getName().trim());
        user.setEmail(newUser.getEmail().trim());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setRole(newUser.getRole().toLowerCase());

        List<User> name = repository.uniqueUserName(newUser.getName().trim().toLowerCase());
        if (name.size() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This username is already used!.");
        }
        List<User> email = repository.uniqueUserEmail(newUser.getEmail().trim().toLowerCase());
        if (email.size() != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This email is already used!.");
        }
        if (newUser.getRole().isBlank()) {
            newUser.setRole("student");
        } else if (!(enumContains(newUser.getRole().toLowerCase()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no Role.");
        }

        User savedUser = repository.saveAndFlush(user);
        user.setPassword("Protected Field");
        return savedUser;
    }

    public void deleteUser(Integer userId) {
        repository.findById(userId).orElseThrow(() -> new ResponseStatusException
                (HttpStatus.NOT_FOUND, "This id " + userId + " does not exist!!"));
        repository.deleteById(userId);
    }

    public User updateUser(User updateUser, Integer userId) {
        User user = repository.findById(userId).map(existUser -> mapUser(existUser, updateUser)).orElseGet(() ->
        {
            updateUser.setId(userId);
            return updateUser;
        });
        List<User> name = repository.uniqueUserName(updateUser.getName().trim());
        if (name.size() != 0 && name.get(0).getId() != userId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This username is already used!.");
        }
        List<User> email = repository.uniqueUserEmail(updateUser.getEmail().trim().toLowerCase());
        if (email.size() != 0 && email.get(0).getId() != userId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This email is already used!.");
        }
        return repository.saveAndFlush(user);
    }

    private User mapUser(User existUser, User updateUser) {
        if (updateUser.getName() != null) {
            existUser.setName(updateUser.getName().trim());
        }
        if (updateUser.getEmail() != null) {
            existUser.setEmail(updateUser.getEmail().trim());
        }
        if (updateUser.getRole().isEmpty()) {
            updateUser.setRole("student");
        }
        if (updateUser.getRole() != null && enumContains(updateUser.getRole())) {
            existUser.setRole(updateUser.getRole().toLowerCase());
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no Role.");
        return existUser;
    }

    public static boolean enumContains(String userRole) {
        for (Role role : Role.values()) {
            if (role.name().equals(userRole.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public JwtDTO match(MatchUserDTO user) throws ResponseStatusException {
        User user1 = repository.findEmail(user.getEmail());
        if (user1 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email does not exist");
        }
        String userRole = user1.getRole();
        if (!matchPassword(user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password not match >:(");
        }
        return new JwtDTO(generateToken(user), generateRefreshToken(user), userRole);
    }

    public boolean matchPassword(MatchUserDTO user) {
        User user1 = modelMapper.map(user, User.class);
        User match = repository.findEmail(user.getEmail());
        if (match == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email does not exist");
        }
        return passwordEncoder.matches(user1.getPassword(), match.getPassword());
    }

    private String generateToken(MatchUserDTO user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        UserDetails userDetail = userDetailsService.loadUserByUsername(user.getEmail());
        return jwtTokenUtil.generateToken(userDetail);

    }

    public String generateRefreshToken(MatchUserDTO user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        UserDetails userDetail = userDetailsService.loadUserByUsername(user.getEmail());
        return jwtTokenUtil.generateRefreshToken(userDetail);
    }

    public JwtDTO generateNewToken(String refreshToken){
        UserDetails userDetail = userDetailsService.loadUserByUsername(jwtTokenUtil.getUsernameFromToken(refreshToken));
        User user = repository.findEmail(userDetail.getUsername());
        String userRole = user.getRole();
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token is from email that does not exist!!");
        }
        if(!jwtTokenUtil.tokenExpired(refreshToken)){
            return new JwtDTO(jwtTokenUtil.generateToken(userDetail),refreshToken, userRole);
        }else return null;
    }
}

