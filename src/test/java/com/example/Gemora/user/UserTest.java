package com.example.Gemora.user;

import com.gemora.GemoraApplication;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.context.SpringBootTest;

import com.gemora.user.Role;
import com.gemora.user.User;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = GemoraApplication.class)
public class UserTest {

    private final Integer USER_1_ID  = 1;
    private final Integer USER_2_ID  = 2;
    private final String USER_FIRSTNAME = "John";
    private final String USER_LASTNAME = "Doe";
    private final String USER_EMAIL = "johndoe@example.com";
    private final String USER_PASSWORD = "password123";

    @Test
    void testUserBuilder_BuildsValidUser_AllPropertiesAreSet() {
        //given
        Role role = Role.USER;

        //when
        User user = createUser(User.builder()
                .id(USER_1_ID)
                .firstname(USER_FIRSTNAME)
                .lastname(USER_LASTNAME)
                .email(USER_EMAIL)
                .password(USER_PASSWORD)
                .role(role));

        //then
        assertEquals(USER_1_ID, user.getId());
        assertEquals(USER_FIRSTNAME, user.getFirstname());
        assertEquals(USER_LASTNAME, user.getLastname());
        assertEquals(USER_EMAIL, user.getEmail());
        assertEquals(USER_PASSWORD, user.getPassword());
        assertEquals(role, user.getRole());
    }

    @Test
    void testUserSetters_SetId() {
        //given
        User user = createUser(User.builder().id(null));

        //when
        user.setId(USER_1_ID);

        //then
        assertEquals(USER_1_ID, user.getId());
    }

    @Test
    void testUserSetters_SetFirstname() {
        //given
        User user = createUser(User.builder().firstname(null));

        //when
        user.setFirstname(USER_FIRSTNAME);

        //then
        assertEquals(USER_FIRSTNAME, user.getFirstname());
    }

    @Test
    void testUserSetters_SetLastname() {
        //given
        User user = createUser(User.builder().lastname(null));

        //when
        user.setLastname(USER_LASTNAME);

        //then
        assertEquals(USER_LASTNAME, user.getLastname());
    }

    @Test
    void testUserSetters_SetEmail() {
        //given
        User user = createUser(User.builder().email(null));

        //when
        user.setEmail(USER_EMAIL);

        //then
        assertEquals(USER_EMAIL, user.getEmail());
    }

    @Test
    void testUserSetters_SetPassword() {
        //given
        User user = createUser(User.builder().password(null));

        //when
        user.setPassword(USER_PASSWORD);

        //then
        assertEquals(USER_PASSWORD, user.getPassword());
    }

    @Test
    void testUserGetUsername_ReturnsEmail() {
        //given
        User user = createUser(User.builder().email(USER_EMAIL));

        //when
        assertEquals(USER_EMAIL, user.getUsername());
    }

    @Test
    void testUserSetters_SetRole() {
        //given
        Role role = Role.ADMIN;
        User user = createUser(User.builder().role(null));

        //when
        user.setRole(role);

        //then
        assertEquals(role, user.getRole());
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    void testUserAuthorities_ReturnsRoleAsAuthority(Role role) {
        //given
        int expectedSize = 1;

        String roleValue = String.valueOf(role);
        User user = createUser(User.builder().role(role));

        //when
        var authorities = user.getAuthorities();

        //then
        assertEquals(expectedSize, authorities.size());
        assertEquals(roleValue, authorities.iterator().next().getAuthority());
    }

    @Test
    void testIsAccountNonExpired_ReturnsTrue_ForNewlyCreatedUser() {
        //given
        User user = createUser(User.builder());

        //when
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked_ReturnsTrue_ForNewlyCreatedUser() {
        //given
        User user = createUser(User.builder());

        //when
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired_ReturnsTrue_ForNewlyCreatedUser() {
        //given
        User user = createUser(User.builder());

        //when
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled_ReturnsTrue_ForNewlyCreatedUser() {
        //given
        User user = createUser(User.builder());

        //when
        assertTrue(user.isEnabled());
    }

    @Test
    void testUserEqualsMethod_ReturnsTrue_ForSameObject() {
        //given
        User user = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));

        //when
        assertEquals(user, user);
    }

    @Test
    void testUserEqualsMethod_ReturnsFalse_ForNullObject() {
        //given
        User user = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));

        //when
        assertNotEquals(null, user);
    }

    @Test
    void testUserEqualsMethod_ReturnsFalse_ForNullEmail() {
        //given
        User user1 = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));
        User user2 = createUser(User.builder().id(USER_1_ID).email(null));

        //when
        assertNotEquals(user1, user2);
    }

    @Test
    void testUserEqualsMethod_ReturnsFalse_ForDifferentCaseEmails() {
        //given
        User user1 = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));
        User user2 = createUser(User.builder().id(USER_1_ID).email("JOHNdoe@example.com"));

        //when
        assertNotEquals(user1, user2);
    }

    @Test
    void testUserEqualsMethod_ReturnsTrue_ForEqualObjects() {
        //given
        User user1 = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));
        User user2 = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));

        //then
        assertEquals(user1, user2);
    }

    @Test
    void testUserEqualsMethod_ReturnsFalse_ForUnequalObjects() {
        //given
        User user1 = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));
        User user2 = createUser(User.builder().id(USER_2_ID).email("jane.doe@example.com"));

        //then
        assertNotEquals(user1, user2);
    }

    @Test
    void testUserEqualsMethod_ReturnsFalse_ForDifferentTypes() {
        User user = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));
        Object otherObject = new Object();

        assertNotEquals(user, otherObject);
    }

    @Test
    void testUserEqualsMethod_ReturnsFalse_ForDifferentIds() {
        //given
        User user1 = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));
        User user2 = createUser(User.builder().id(USER_2_ID).email(USER_EMAIL));

        //when
        assertNotEquals(user1, user2);
    }

    @Test
    void testUserEqualsMethod_ReturnsFalse_ForDifferentEmails() {
        //given
        User user1 = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));
        User user2 = createUser(User.builder().id(USER_2_ID).email("jane.doe@example.com"));

        //when
        assertNotEquals(user1, user2);
    }

    @Test
    void testUserEqualsMethod_ReturnsFalse_ForDifferentObjects() {
        //given
        User user = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));

        //when
        assertNotEquals("Not a User object", user);
    }

    @Test
    void testUserHashCodeMethod_ReturnsSameHashCode_ForEqualObjects() {
        //given
        User user1 = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));
        User user2 = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));

        //when
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testUserHashCodeMethod_ReturnsDifferentHashCode_ForDifferentIds() {
        //given
        User user1 = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));
        User user2 = createUser(User.builder().id(USER_2_ID).email(USER_EMAIL));

        //when
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testUserHashCodeMethod_ReturnsDifferentHashCode_ForDifferentEmails() {
        //given
        User user1 = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));
        User user2 = createUser(User.builder().id(USER_2_ID).email(USER_EMAIL));

        //when
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testUserToString_ReturnsStringRepresentation() {
        //given
        User user = createUser(User.builder().id(USER_1_ID).email(USER_EMAIL));

        //when
        String stringRepresentation = user.toString();

        //then
        assertNotNull(stringRepresentation);
        assertTrue(stringRepresentation.contains(user.getId().toString()));
        assertTrue(stringRepresentation.contains(user.getEmail()));
    }

    private User createUser(User.UserBuilder userEmail) {
        return userEmail.build();
    }
}
