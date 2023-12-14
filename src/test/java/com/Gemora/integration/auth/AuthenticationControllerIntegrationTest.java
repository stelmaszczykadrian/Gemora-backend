package com.Gemora.integration.auth;

import com.gemora.GemoraApplication;
import com.gemora.auth.AuthenticationRequest;
import com.gemora.auth.AuthenticationResponse;
import com.gemora.auth.AuthenticationService;
import com.gemora.auth.RegisterRequest;
import com.gemora.validation.exceptions.EmailAlreadyExistsException;
import com.gemora.validation.exceptions.EmailValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.Gemora.unit.TestUtils.asJsonString;
import static com.Gemora.unit.auth.AuthenticationTestHelper.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {GemoraApplication.class})
@AutoConfigureMockMvc
public class AuthenticationControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void register_ReturnsOkStatus_ValidRegisterRequest() throws Exception {
        //given
        RegisterRequest registerRequest = createRegisterRequest();

        AuthenticationResponse mockResponse = createAuthenticationResponse();

        when(authenticationService.register(eq(registerRequest))).thenReturn(mockResponse);

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
                .content(asJsonString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("sampleAccessToken"))
                .andExpect(jsonPath("$.refresh_token").value("sampleRefreshToken"));
        verify(authenticationService, times(1)).register(eq(registerRequest));
    }

    @Test
    void register_ReturnsBadRequestStatus_ThrowsEmailValidationException() throws Exception {
        //given
        RegisterRequest registerRequest = invalidRegisterRequest();
        doThrow(EmailValidationException.class).when(authenticationService).register(eq(registerRequest));

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
                .content(asJsonString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isBadRequest());
        verify(authenticationService, times(0)).register(eq(registerRequest));
    }

    @Test
    void register_ReturnsConflictStatus_ThrowsEmailAlreadyExistsException() throws Exception {
        //given
        RegisterRequest registerRequest = createRegisterRequest();

        doThrow(EmailAlreadyExistsException.class).when(authenticationService).register(eq(registerRequest));

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
                .content(asJsonString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isConflict());
    }

    @Test
    void authenticate_ReturnsOkStatus_ValidAuthenticationRequest() throws Exception {
        //given
        AuthenticationRequest request = createAuthenticationRequest();

        AuthenticationResponse mockResponse = createAuthenticationResponse();

        when(authenticationService.authenticate(eq(request))).thenReturn(mockResponse);

        //when
        ResultActions result = mockMvc.perform(post("/api/v1/auth/authenticate")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk());
        verify(authenticationService, times(1)).authenticate(eq(request));
    }

    @Test
    void authenticate_ReturnsBadRequestStatus_ThrowsUsernameNotFoundException() throws Exception {
        // given
        AuthenticationRequest invalidRequest = AuthenticationRequest.builder()
                .email("invalidEmail")
                .password("invalidPassword")
                .build();

        doThrow(UsernameNotFoundException.class).when(authenticationService).authenticate(eq(invalidRequest));

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/auth/authenticate")
                .content(asJsonString(invalidRequest))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isBadRequest());
    }

    private RegisterRequest invalidRegisterRequest() {
        return RegisterRequest.builder()
                .firstname("")
                .lastname("")
                .email("")
                .password("")
                .build();
    }
}
