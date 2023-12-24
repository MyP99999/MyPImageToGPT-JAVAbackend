package com.example.MyPImageToGPT.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextContentItem {
    private String type = "text";
    private String text;
}
