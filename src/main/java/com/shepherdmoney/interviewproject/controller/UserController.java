package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;

import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;

import java.util.*;

@RestController
public class UserController {

    // TODO: wire in the user repository (~ 1 line)
    private final UserRepository userRepository;
    private final CreditCardRepository creditCardRepository;

    public UserController(CreditCardRepository creditCardRepository, UserRepository userRepository) {
        this.creditCardRepository = creditCardRepository;
        this.userRepository = userRepository;
    }

    @PutMapping("/user")
    public ResponseEntity<Integer> createUser(@RequestBody CreateUserPayload payload) {
        // TODO: Create an user entity with information given in the payload, store it in the database
        //       and return the id of the user in 200 OK response
        User user = new User(payload.getName(), payload.getEmail());
        userRepository.save(user);
        return ResponseEntity.ok().body(user.getId());
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam int userId) {
        // TODO: Return 200 OK if a user with the given ID exists, and the deletion is successful
        //       Return 400 Bad Request if a user with the ID does not exist
        //       The response body could be anything you consider appropriate
        Optional<User> realUser = userRepository.findById(userId);
        if (realUser.isPresent()) {
            userRepository.deleteById(userId);
            return ResponseEntity.ok().body("Deletion was successful");
        }
        else {
            return ResponseEntity.badRequest().body("A user with the ID does not exist");
        }
        //return null;
    }
}
