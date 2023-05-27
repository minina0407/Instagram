package com.api.PortfoGram.user.controller;

import com.api.PortfoGram.user.dto.Profile;
import com.api.PortfoGram.user.dto.User;
import com.api.PortfoGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    @GetMapping("/profile")
    public ResponseEntity<Profile> getProfile(@RequestParam("user_id") Long userId) {
        Profile profile = userService.searchProfileById(userId);
        return ResponseEntity.ok(profile);
    }
    @PostMapping("/join")
    public ResponseEntity<User> joinMembership(@RequestParam("nickname") String nickname, @RequestParam("profile_image") MultipartFile profileImage) throws IOException {
        User user = userService.createUser(nickname,profileImage);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/withdrawal")
    public ResponseEntity<Void> withdrawMembership(@RequestParam("id") Long userId) {
        // Delete the member based on the provided ID
        userService.deleteMember(userId);

        return ResponseEntity.ok().build();
    }


}