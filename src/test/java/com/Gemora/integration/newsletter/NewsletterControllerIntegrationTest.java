package com.Gemora.integration.newsletter;

import com.gemora.newsletter.Newsletter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.ResultActions;

import static com.Gemora.unit.TestUtils.asJsonString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NewsletterControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addEmail_ReturnsIsCreatedStatus_ValidEmail() throws Exception {
        //given
        Newsletter newsletter = new Newsletter(1, "johndoe@example.com");

        //when
        ResultActions result = mockMvc.perform(post("/api/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newsletter)));

        //then
        result.andExpect(status().isCreated())
                .andExpect(content().string("Email added successfully."));
    }

    @Test
    public void addEmail_ReturnsBadRequestStatus_EmptyEmail() throws Exception {
        //given
        Newsletter newsletter = new Newsletter(1, "");

        //when
        ResultActions result = mockMvc.perform(post("/api/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newsletter)));

        //then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void addEmail_ReturnsConflictStatus_EmailAlreadyExists() throws Exception {
        //given
        Newsletter newsletter = new Newsletter(1, "exist@example.com");

        mockMvc.perform(post("/api/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newsletter)));

        //when
        ResultActions secondResult = mockMvc.perform(post("/api/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(newsletter)));

        //then
        secondResult.andExpect(status().isConflict())
                .andExpect(content().string("Email already exists in the database."));
    }
}
