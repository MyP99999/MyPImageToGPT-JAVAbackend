package com.example.MyPImageToGPT.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestDTO {
    private String prompt;
    private Integer userId;
    private Integer price;
    private String imageData;
    private String model;
}
