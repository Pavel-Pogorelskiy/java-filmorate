package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest {
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;
    public static final String PATH = "/users";

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void createNormalUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("controller/request/NormalUser.json")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(
                        getContentFromFile("controller/response/NormalUser.json")));
    }

    @Test
    void validateUserErrorEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("controller/request/UserErrorEmail.json")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void validateUserEmptyEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("controller/request/UserEmptyEmail.json")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void validateUserBlankLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("controller/request/UserLoginBlank.json")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void validateUserBirthdayFuture() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getContentFromFile("controller/request/UserBirthdayFuture.json")))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    void emptyRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    private String getContentFromFile(String filename) {
        try {
            return Files.readString(ResourceUtils.getFile("classpath:" + filename).toPath(),
                    StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return "";
        }
    }
}