package com.example.MyPImageToGPT.controllers;


import com.example.MyPImageToGPT.Entities.User;
import com.example.MyPImageToGPT.dto.ChatGPTRequest;
import com.example.MyPImageToGPT.dto.ChatGptResponse;
import com.example.MyPImageToGPT.services.HistoryService;
import com.example.MyPImageToGPT.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@RestController
@RequestMapping("/bot")
@CrossOrigin("http://localhost:3000")
public class CustomBotController {

    @Value("${openai.model}")
    private String model;

    @Value(("${openai.api.url}"))
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @Autowired
    private UserService userService;

    @Autowired
    private HistoryService historyService;

    @GetMapping("/chat")
    public String chat(@RequestParam("prompt") String prompt, @RequestParam("userId") Integer userId){
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);
        ChatGptResponse chatGptResponse = template.postForObject(apiURL, request, ChatGptResponse.class);

        // Ensure non-null response before accessing its properties
        if(chatGptResponse != null && chatGptResponse.getChoices() != null && !chatGptResponse.getChoices().isEmpty()) {
            String answer = chatGptResponse.getChoices().get(0).getMessage().getContent();

            Optional<User> userOptional = userService.findById(userId);
            if(userOptional.isPresent()){
                User user = userOptional.get();
                Integer price = 5;
                userService.substractTokenBalance(user, price);
                historyService.saveHistory(user.getId(), prompt, answer, price);
            }

            return answer;
        }

        return "Failed to get a response from the model";
    }
}