package com.api.PortfoGram.user.controller;

import com.api.PortfoGram.user.dto.Profile;
import com.api.PortfoGram.user.dto.User;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile/{id}")
    public ResponseEntity<Profile> getProfile(@PathVariable("id") Long id) {
        Profile profile = userService.getProfileById(id);
        return new ResponseEntity<>(profile,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> signUp(@Valid @RequestBody User user) {
        userService.saveUser(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/profile/profileImage")
    public ResponseEntity<Void> saveUserImage(@RequestParam("profileImage") MultipartFile profileImage) throws IOException {
        userService.saveUserImage(profileImage);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<Profile> updateMyProfile(@Valid @RequestBody Profile profile) {
        Profile updatedProfile = userService.updateMyProfile(profile);
        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }

    @DeleteMapping("/withdrawal/{id}")
    public ResponseEntity<Void> withdraw(@PathVariable("id") Long userId) {
        userService.deleteMember(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
