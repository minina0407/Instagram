package com.api.PortfoGram.user.controller;

import com.api.PortfoGram.exception.dto.BadRequestException;
import com.api.PortfoGram.exception.dto.ExceptionEnum;
import com.api.PortfoGram.user.dto.Profile;
import com.api.PortfoGram.user.dto.User;
import com.api.PortfoGram.user.entity.UserEntity;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    @GetMapping("/profile")
    public ResponseEntity<Profile> getProfile(@RequestParam("user_id") Long userId) {
        Profile profile = userService.searchProfileById(userId);
        return ResponseEntity.ok(profile);
    }
    @PostMapping
    public ResponseEntity<User> signUp(@Valid @RequestBody User user) {
        userService.saveUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/withdrawal")
    public ResponseEntity<Void> withdrawMembership(@RequestParam("id") Long userId) {
        // Delete the member based on the provided ID
        userService.deleteMember(userId);

        return ResponseEntity.ok().build();
    }


}