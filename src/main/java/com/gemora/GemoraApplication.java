package com.gemora;

import com.gemora.auth.AuthenticationResponse;
import com.gemora.auth.AuthenticationService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GemoraApplication {
    private final AuthenticationService authenticationService;

	public GemoraApplication(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}


	public static void main(String[] args) {
        SpringApplication.run(GemoraApplication.class, args);
    }

	@PostConstruct
	public AuthenticationResponse init() {
		return authenticationService.initializeAdminUserAndTokens();
	}

}
