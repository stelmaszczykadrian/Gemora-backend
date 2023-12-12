package com.Gemora.unit.auth;

import com.gemora.GemoraApplication;
import com.gemora.auth.*;
import com.gemora.validation.exceptions.EmailAlreadyExistsException;
import com.gemora.validation.exceptions.EmailValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;

import java.util.Objects;

import static com.Gemora.unit.TestUtils.getBindingResult;
import static com.Gemora.unit.auth.AuthenticationTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void register_ReturnsOkStatus_ValidRegisterRequest() {
        //given
        RegisterRequest request = createRegisterRequest();

        AuthenticationResponse expectedResponse = createAuthenticationResponse();

        BindingResult bindingResult = getBindingResult(false);

        when(authenticationService.register(request)).thenReturn(expectedResponse);

        //when
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.register(request, bindingResult);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
    }

    @Test
    void register_ReturnsBadRequestStatus_InvalidRegisterRequest() {
        //given
        RegisterRequest request = createRegisterRequest();
        request.setEmail("invalid-email-address");

        BindingResult bindingResult = getBindingResult(false);

        String expectedErrorMessage = "Email already exists in the database.";

        doThrow(new EmailValidationException(expectedErrorMessage))
                .when(authenticationService)
                .register(any(RegisterRequest.class));

        //when
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.register(request, bindingResult);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedErrorMessage, responseEntity.getBody().getValidationErrors().get("email"));
    }

    @Test
    void register_ReturnsConflictStatus_ThrowsEmailAlreadyExistsException() {
        //given
        RegisterRequest request = createRegisterRequest();

        BindingResult bindingResult = getBindingResult(false);

        String expectedErrorMessage = "Email already exists in the database.";

        doThrow(new EmailAlreadyExistsException(expectedErrorMessage))
                .when(authenticationService)
                .register(any(RegisterRequest.class));

        //when
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.register(request, bindingResult);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void authenticate_ReturnOkStatusWithExpectedResponse_SuccessfulAuthentication() {
        //given
        AuthenticationRequest request = createAuthenticationRequest();

        AuthenticationResponse expectedResponse = createAuthenticationResponse();

        BindingResult bindingResult = getBindingResult(false);

        when(authenticationService.authenticate(request)).thenReturn(expectedResponse);

        //when
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.authenticate(request, bindingResult);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
        verify(authenticationService, times(1)).authenticate(request);
    }

    @Test
    void authenticate_ReturnBadRequestStatus_WhenBindingResultErrors() {
        //given
        AuthenticationRequest request = createAuthenticationRequest();

        BindingResult bindingResult = getBindingResult(true);

        //when
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.authenticate(request, bindingResult);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void authenticate_ReturnsBadRequestStatus_UserDoesNotExist() {
        //given
        AuthenticationRequest request = createAuthenticationRequest();

        BindingResult bindingResult = getBindingResult(false);

        String expectedErrorMessage = "User already do not exists in the database.";

        doThrow(new UsernameNotFoundException(expectedErrorMessage))
                .when(authenticationService)
                .authenticate(any(AuthenticationRequest.class));

        //when
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationController.authenticate(request, bindingResult);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(Objects.requireNonNull(responseEntity.getBody()).getValidationErrors().get("error"))
                .isEqualTo(expectedErrorMessage);
    }
}
