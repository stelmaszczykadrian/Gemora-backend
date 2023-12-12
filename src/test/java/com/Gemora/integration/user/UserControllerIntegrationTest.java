package com.Gemora.integration.user;

import com.gemora.GemoraApplication;
import com.gemora.user.User;
import com.gemora.user.UserDto;
import com.gemora.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import static com.Gemora.unit.auth.AuthenticationTestHelper.createUser;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = GemoraApplication.class)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void getCurrentUser_ReturnsOkStatus_ValidUser() throws Exception {
        //given
        String userEmail = "johndoe@gmail.com";

        User user = createUser();

        UserDto expectedUserDto = new UserDto(user);

        when(userService.getUser(userEmail)).thenReturn(expectedUserDto);

        //when
        ResultActions result = mockMvc.perform(get("/api/users/profile/{userEmail}", userEmail));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("John"))
                .andExpect(jsonPath("$.lastname").value("Doe"))
                .andExpect(jsonPath("$.email").value(userEmail))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getCurrentUser_ReturnsNotFoundStatus_ThrowsUsernameNotFoundException() throws Exception {
        //given
        String userEmail = "nonexist@example.com";

        doThrow(UsernameNotFoundException.class).when(userService).getUser(eq(userEmail));

        //when
        ResultActions result = mockMvc.perform(get("/api/users/profile/{userEmail}", userEmail));

        //then
        result.andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
