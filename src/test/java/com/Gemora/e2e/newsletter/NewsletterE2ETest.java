package com.Gemora.e2e.newsletter;

import com.gemora.newsletter.Newsletter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NewsletterE2ETest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void addEmail_ReturnsCreatedStatus_ValidEmail() {
        //given
        String baseUrl = "http://localhost:" + port + "/api/email";

        Newsletter newsletter = new Newsletter(1, "test@gmail.com");

        //when
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl, newsletter, String.class);

        //then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Email added successfully.", responseEntity.getBody());
    }

    @Test
    void addEmail_ReturnsBadRequestStatus_InvalidEmail() {
        //given
        String baseUrl = "http://localhost:" + port + "/api/email";

        Newsletter newsletter = new Newsletter(1, "invalidemail");

        //when
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl, newsletter, String.class);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void addEmail_ReturnsConflictStatus_EmailDuplicate() {
        //given
        String baseUrl = "http://localhost:" + port + "/api/email";

        Newsletter newsletter = new Newsletter(1, "existing@gmail.com");

        restTemplate.postForEntity(baseUrl, newsletter, String.class);

        //when
        ResponseEntity<String> responseEntity2 = restTemplate.postForEntity(baseUrl, newsletter, String.class);

        //then
        assertEquals(HttpStatus.CONFLICT, responseEntity2.getStatusCode());
    }
}
