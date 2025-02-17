package com.chat.SunScript.dto.userdto.messagedto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {

    //private String sender;
    private String recipient;
    private String content;

    public MessageDto() {}

    public MessageDto(String recipient, String content/*, LocalDateTime timestamp*/) {
        this.recipient = recipient;
        this.content = content;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
