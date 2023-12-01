package com.Gemora.integration.email;

import com.gemora.email.Email;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultActions;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EmailControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addEmail_ReturnsIsCreatedStatus_ValidEmail() throws Exception {
        //given
        Email email = new Email(1, "johndoe@example.com");

        //when
        ResultActions result = mockMvc.perform(post("/api/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(email)));

        //then
        result.andExpect(status().isCreated())
                .andExpect(content().string("Email added successfully."));
    }

    @Test
    public void addEmail_ReturnsBadRequestStatus_EmptyEmail() throws Exception {
        //given
        Email email = new Email(1, "");

        //when
        ResultActions result = mockMvc.perform(post("/api/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(email)));

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void addEmail_ReturnsConflictStatus_EmailAlreadyExists() throws Exception {
        //given
        Email email = new Email(1, "exist@example.com");

        //when
        ResultActions firstResult = mockMvc.perform(post("/api/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(email)));

        ResultActions secondResult = mockMvc.perform(post("/api/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(email)));

        //then
        firstResult.andExpect(status().isCreated())
                .andExpect(content().string("Email added successfully."));

        secondResult.andExpect(status().isConflict())
                .andExpect(content().string("Email already exists in the database."));

    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
