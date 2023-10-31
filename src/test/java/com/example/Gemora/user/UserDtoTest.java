package com.example.Gemora.user;

import com.gemora.GemoraApplication;
import com.gemora.user.Role;
import com.gemora.user.User;
import com.gemora.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = GemoraApplication.class)
public class UserDtoTest {

    private final Integer USER_ID = 1;
    private final String USER_FIRSTNAME = "John";
    private final String USER_LASTNAME = "Doe";
    private final String USER_EMAIL = "test@example.com";
    private final Role USER_ROLE = Role.USER;
    private final Integer SECOND_USER_ID = 2;
    private final String SECOND_USER_EMAIL = "another@example.com";
    private final String SECOND_USER_FIRSTNAME = "Jane";
    private final String SECOND_USER_LASTNAME = "Smith";

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = getUserDto(USER_ID, USER_EMAIL, USER_FIRSTNAME, USER_LASTNAME);
    }

    @Test
    void createUserDto_ReturnsUserDtoWithExpectedValues_RequiredFieldsOnly() {
        //given
        userDto.setRole(Role.ADMIN);

        //then
        assertEquals(USER_FIRSTNAME, userDto.getFirstname());
        assertEquals(USER_LASTNAME, userDto.getLastname());
        assertEquals(USER_EMAIL, userDto.getEmail());
        assertEquals(Role.ADMIN, userDto.getRole());
    }

    @Test
    void testSetAndGetId_ReturnsId_SettingId() {
        //when
        Integer newId = 2;
        userDto.setId(newId);

        //then
        assertEquals(newId, userDto.getId());
    }

    @Test
    void testSetAndGetName_ReturnsName_SettingName() {
        //when
        userDto.setFirstname(USER_FIRSTNAME);

        //then
        assertEquals(USER_FIRSTNAME, userDto.getFirstname());
    }

    @Test
    void testSetAndGetName_ReturnsName_SettingLastName() {
        //when
        userDto.setLastname(USER_LASTNAME);

        //then
        assertEquals(USER_LASTNAME, userDto.getLastname());
    }

    @Test
    void testSetAndGetName_ReturnsName_SettingEmail() {
        //when
        userDto.setEmail(USER_EMAIL);

        //then
        assertEquals(USER_EMAIL, userDto.getEmail());
    }

    @Test
    void testSetAndGetRole_ReturnsRole_SettingRole() {
        //when
        userDto.setRole(USER_ROLE);

        //then
        assertEquals(USER_ROLE, userDto.getRole());
    }

    @Test
    void testTwoObjects_ReturnSameHashCodeForEqualObjects_TwoEqualUserDtoes() {
        //given
        UserDto userDto1 = getUserDto(USER_ID, USER_EMAIL, USER_FIRSTNAME, USER_LASTNAME);
        UserDto userDto2 = getUserDto(USER_ID, USER_EMAIL, USER_FIRSTNAME, USER_LASTNAME);

        //when
        int hashCode1 = userDto1.hashCode();
        int hashCode2 = userDto2.hashCode();

        //then
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testTwoObjects_ReturnDifferentHashCodeForUnequalObjects_TwoUnequalObjects() {
        //given
        Integer idSecondUser = 2;
        String emailSecondUser = "another@example.com";
        String firstnameSecondUser = "Jane";
        String lastnameSecondUser = "Smith";

        UserDto userDto1 = getUserDto(USER_ID, USER_EMAIL, USER_FIRSTNAME, USER_LASTNAME);
        UserDto userDto2 = getUserDto(idSecondUser, emailSecondUser, firstnameSecondUser, lastnameSecondUser);

        //when
        int hashCode1 = userDto1.hashCode();
        int hashCode2 = userDto2.hashCode();

        //then
        assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    void testToString_ReturnStringRepresentationOfUserDto_ContainsExpectedInformation() {
        //given
        String email = "test@example.com";
        userDto = getUserDto(USER_ID, email, USER_FIRSTNAME, USER_LASTNAME);

        //when
        String stringRepresentation = userDto.toString();

        //then
        assertTrue(stringRepresentation.contains("UserDto"));
        assertTrue(stringRepresentation.contains("id=1"));
        assertTrue(stringRepresentation.contains("firstname=John"));
        assertTrue(stringRepresentation.contains("lastname=Doe"));
        assertTrue(stringRepresentation.contains("email=test@example.com"));
        assertTrue(stringRepresentation.contains("role=USER"));
    }

    @Test
    void testEquals_ReturnsTrue_WhenObjectsAreEqual() {
        //given
        UserDto userDto1 = getUserDto(USER_ID, USER_EMAIL, USER_FIRSTNAME, USER_LASTNAME);
        UserDto userDto2 = getUserDto(USER_ID, USER_EMAIL, USER_FIRSTNAME, USER_LASTNAME);

        //when
        boolean equals = userDto1.equals(userDto2);

        //then
        assertTrue(equals);
    }

    @Test
    void testEquals_ReturnsFalse_WhenObjectsAreNotEqual() {
        //given
        UserDto userDto1 = getUserDto(USER_ID, USER_EMAIL, USER_FIRSTNAME, USER_LASTNAME);
        UserDto userDto2 = getUserDto(SECOND_USER_ID, SECOND_USER_EMAIL, SECOND_USER_FIRSTNAME, SECOND_USER_LASTNAME);

        //when
        boolean equals = userDto1.equals(userDto2);

        //then
        assertFalse(equals);
    }

    @Test
    void testHashCode_ReturnsSameHashCode_ForEqualObjects() {
        //given
        UserDto userDto1 = getUserDto(USER_ID, USER_EMAIL, USER_FIRSTNAME, USER_LASTNAME);
        UserDto userDto2 = getUserDto(USER_ID, USER_EMAIL, USER_FIRSTNAME, USER_LASTNAME);

        //when
        int hashCode1 = userDto1.hashCode();
        int hashCode2 = userDto2.hashCode();

        //then
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    void testHashCode_ReturnsDifferentHashCode_ForUnequalObjects() {
        //given
        UserDto userDto1 = getUserDto(USER_ID, USER_EMAIL, USER_FIRSTNAME, USER_LASTNAME);
        UserDto userDto2 = getUserDto(SECOND_USER_ID, SECOND_USER_EMAIL, SECOND_USER_FIRSTNAME, SECOND_USER_LASTNAME);

        //when
        int hashCode1 = userDto1.hashCode();
        int hashCode2 = userDto2.hashCode();

        //then
        assertNotEquals(hashCode1, hashCode2);
    }

    private UserDto getUserDto(Integer userId, String email, String firstname, String lastname) {
        return new UserDto(createSampleUser(userId, email, firstname, lastname));
    }

    private User createSampleUser(Integer userId, String email, String firstname, String lastname) {
        return User.builder()
                .id(userId)
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .role(Role.USER)
                .build();
    }

}


