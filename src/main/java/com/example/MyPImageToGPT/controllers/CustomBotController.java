package com.example.MyPImageToGPT.controllers;

import com.example.MyPImageToGPT.Entities.User;
import com.example.MyPImageToGPT.dto.*;
import com.example.MyPImageToGPT.services.HistoryService;
import com.example.MyPImageToGPT.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/bot")
public class CustomBotController {

    @Value("${openai.api.url}")
    private String apiURL;

    @Value("${openai.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private HistoryService historyService;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequestDTO chatRequestDTO) {
        List<Object> contentItems = new ArrayList<>();
        contentItems.add(new TextContentItem("text", chatRequestDTO.getPrompt()));
        if (!"gpt-4-vision-preview".equals(chatRequestDTO.getModel()) && chatRequestDTO.getImageData() != null && !chatRequestDTO.getImageData().isEmpty()) {
            chatRequestDTO.setModel("gpt-4-vision-preview");
        }

        if (chatRequestDTO.getImageData() != null && !chatRequestDTO.getImageData().isEmpty()) {
            ImageUrl imageUrl = new ImageUrl(chatRequestDTO.getImageData());
            contentItems.add(new ImageUrlContentItem("image_url", imageUrl));
        }

        List<Message> messages = List.of(new Message("user", contentItems));
        OpenAIRequestDTO openAIRequest = new OpenAIRequestDTO(chatRequestDTO.getModel(), messages, 3100);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonRequest = mapper.writeValueAsString(openAIRequest);
            System.out.println("Request JSON: " + jsonRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpEntity<OpenAIRequestDTO> entity = new HttpEntity<>(openAIRequest, headers);
        System.out.println(entity);
        try {
            ResponseEntity<ChatGptResponse> responseEntity = restTemplate.exchange(
                    apiURL, HttpMethod.POST, entity, ChatGptResponse.class);

            if (responseEntity.getBody() != null && responseEntity.getBody().getChoices() != null
                    && !responseEntity.getBody().getChoices().isEmpty()) {
                System.out.println(responseEntity);
                ChatGptResponse.Message responseMessage = responseEntity.getBody().getChoices().get(0).getMessage();
                if (responseMessage != null && responseMessage.getContent() != null) {
                    String answer = responseMessage.getContent();

                    Optional<User> userOptional = userService.findById(chatRequestDTO.getUserId());
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        userService.substractTokenBalance(user, chatRequestDTO.getPrice());
                        historyService.saveHistory(user.getId(), chatRequestDTO.getPrompt(), answer, chatRequestDTO.getPrice());
                    }

                    return new ResponseEntity<>(answer, HttpStatus.OK);
                }
            }
            return new ResponseEntity<>("No valid response from OpenAI", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error processing the OpenAI response: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

