package com.api.PortfoGram.reply.controller;

import com.api.PortfoGram.reply.dto.Reply;
import com.api.PortfoGram.reply.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/replies")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping
    public ResponseEntity<Reply> createReply(@RequestBody Reply request) {
        Reply response = replyService.createReply(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reply> updateReply(@PathVariable("id") Long id, @RequestBody Reply request) {
        Reply response = replyService.updateReply(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReply(@PathVariable("id") Long id) {
        replyService.deleteReply(id);
        return ResponseEntity.ok().build();
    }
}
