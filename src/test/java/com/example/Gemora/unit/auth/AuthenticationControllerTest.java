package com.example.Gemora.unit.auth;

import com.gemora.GemoraApplication;
import com.gemora.auth.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = GemoraApplication.class)
public class AuthenticationControllerTest {

    private AuthenticationController authenticationController;
    @Mock
    private AuthenticationService authenticationService;

    @BeforeEach
    void init() {
        authenticationController = new AuthenticationController(authenticationService);
    }

    @Test
    void register_ReturnOkResponse_RegisterRequestIsValid() {
        //given
        RegisterRequest request = createRegisterRequest();
        AuthenticationResponse expectedResponse = createAuthenticationResponse();
        BindingResult bindingResult = mock(BindingResult.class);

        when(authenticationService.register(request)).thenReturn(expectedResponse);
        when(bindingResult.hasErrors()).thenReturn(false);

        //when
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.register(request, bindingResult);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void register_ReturnBadRequest_EmailNotValid() {
        //given
        RegisterRequest request = createRegisterRequest();
        request.setEmail("invalid-email-address");

        BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "RegisterRequest");
        bindingResult.rejectValue("email", "error.email", "Email is not valid");

        //when
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.register(request, bindingResult);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void register_ReturnConflict_UserAlreadyExists() {
        //given
        RegisterRequest request = createRegisterRequest();
        BindingResult bindingResult = mock(BindingResult.class);

        when(authenticationService.userExists(request.getEmail())).thenReturn(true);

        //when
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.register(request, bindingResult);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void authenticate_ReturnOkResponseWithExpectedResponse_SuccessfulAuthentication() {
        //given
        AuthenticationRequest request = createValidAuthenticationRequest();
        AuthenticationResponse expectedResponse = createAuthenticationResponse();
        BindingResult bindingResult = mock(BindingResult.class);

        when(authenticationController.userExists(request.getEmail())).thenReturn(true);
        when(authenticationService.authenticate(request)).thenReturn(expectedResponse);

        //when
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.authenticate(request, bindingResult);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
        verify(authenticationService, times(1)).authenticate(request);
    }

    @Test
    void authenticate_ReturnBindingResultErrors_BadRequest() {
        //given
        AuthenticationRequest request = createValidAuthenticationRequest();
        BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "AuthenticationRequest");
        bindingResult.reject("field", "error message");

        //when
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.authenticate(request, bindingResult);

        //then
        assertBadRequestResponse(responseEntity);
    }

    @Test
    void authenticate_ReturnBadRequest_UserDoesNotExist() {
        //given
        AuthenticationRequest request = createValidAuthenticationRequest();
        BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "AuthenticationRequest");

        when(authenticationService.userExists(request.getEmail())).thenReturn(false);

        //when
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.authenticate(request, bindingResult);

        //then
        assertBadRequestResponse(responseEntity);
        verify(authenticationService, times(0)).authenticate(request);
    }

    private void assertBadRequestResponse(ResponseEntity<AuthenticationResponse> responseEntity) {
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).isNull();
    }

    private AuthenticationRequest createValidAuthenticationRequest() {
        return AuthenticationRequest.builder()
                .email("johndoe@example.com")
                .password("password")
                .build();
    }

    private RegisterRequest createRegisterRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstname("John");
        registerRequest.setLastname("Doe");
        registerRequest.setEmail("johndoe@example.com");
        registerRequest.setPassword("password");
        return registerRequest;
    }

    private AuthenticationResponse createAuthenticationResponse() {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccessToken("test_access_token");
        authenticationResponse.setRefreshToken("test_refresh_token");
        return authenticationResponse;
    }
}
