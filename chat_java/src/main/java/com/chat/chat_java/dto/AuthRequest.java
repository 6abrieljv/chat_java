package com.chat.chat_java.dto;



import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}