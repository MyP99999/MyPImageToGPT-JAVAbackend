package com.example.MyPImageToGPT.services;

import com.example.MyPImageToGPT.Entities.History;
import com.example.MyPImageToGPT.Entities.Role;
import com.example.MyPImageToGPT.Entities.User;
import com.example.MyPImageToGPT.repostories.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;

    @Autowired
    private UserService userService;

    public List<History> findHistoryByUserId(Integer userId) {
        return historyRepository.findByUserId(userId);
    }

    public List<History> findAll() {
        return (List<History>) historyRepository.findAll();
    }
    public void saveHistory(Integer userId, String question, String answer) {
        Optional<User> userOptional = userService.findById(userId);

        if(userOptional.isPresent()){
            History history = new History();
            history.setUser(userOptional.get());
            history.setQuestion(question);
            history.setAnswer(answer);
            history.setTimestamp(LocalDateTime.now());
            historyRepository.save(history);
        } else {
            throw new RuntimeException("User with id " + userId + " not found");
        }
    }
    // Additional methods to retrieve history, etc...
}
