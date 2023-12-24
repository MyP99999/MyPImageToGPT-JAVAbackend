package com.example.MyPImageToGPT.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAIRequestDTO {
    private String model;
    private List<Message> messages;
    private int max_tokens;
}
