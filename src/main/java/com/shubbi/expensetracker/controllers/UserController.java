package com.shubbi.expensetracker.controllers;

import com.shubbi.expensetracker.exceptions.EtAuthException;
import com.shubbi.expensetracker.exceptions.UserNotFoundException;
import com.shubbi.expensetracker.models.User;
import com.shubbi.expensetracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;


    @Autowired
    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user){
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    @PostMapping("/login")
    public Map<String, String> loginUser(@RequestBody Map<String, Object> userMap){
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");

        User u = userRepository.validate(email.toLowerCase());

        if (u == null) throw new EtAuthException("No user found with email: "+ email);
        if (!u.getPassword().equals(password)) throw new EtAuthException("Invalid password");

        Map<String, String> map = new HashMap<>();
        map.put("success", "true");
        return map;
    }

    @GetMapping("")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }


    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Integer userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User with id " + userId + " not found")
        );
        return user;

    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable("id") Integer userId, @RequestBody User user){
        return userRepository.findById(userId).map(
                u -> {
                    u.setName(user.getName());
                    u.setEmail(user.getEmail());
                    u.setPassword(user.getPassword());
                    return userRepository.save(u);
                }
                ).orElseGet(
                () -> {
                    user.setId(userId);
                    return userRepository.save(user);
                }
        );
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") Integer userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User with id " + userId + " not found")
        );

        userRepository.delete(user);

        return "User delete successful";
    }
}
