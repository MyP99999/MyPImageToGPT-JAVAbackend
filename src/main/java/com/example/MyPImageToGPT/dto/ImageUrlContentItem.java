package com.example.MyPImageToGPT.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageUrlContentItem {
    private String type = "image_url";
    private ImageUrl image_url;
}
