package com.chat.chat_java.dto;



import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String name;
    private String email;
    private String avatar;
}