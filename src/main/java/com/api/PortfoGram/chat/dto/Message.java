package com.api.PortfoGram.chat.dto;

import com.amazonaws.annotation.Beta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String type;
    private String channelId;
    private Long senderId;
    private Object data;
    private Object content;

    public void setSender(Long sender) {
        this.senderId = senderId;
    }
    public void newConnect(){
        this.type = "new";
    }
    public void closeConnect(){
        this.type = "close";
    }
}
