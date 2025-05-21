package app.bola.smartnotesai.auth.service;

import app.bola.smartnotesai.auth.data.dto.UserRequest;
import app.bola.smartnotesai.auth.data.dto.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SmartNoteAuthServiceTest {
    
    @Autowired
    AuthService userService;
    UserRequest userRequest;

    @BeforeEach
    void setUp() {
        userRequest = UserRequest.builder()
                .email("michealjordan@gmail.com")
                .password("micheal-jordan-angelo54")
                .username("Micheal")
                .build();
    }
    
    @AfterEach
    void tearDown() {
    }
    
    @Test
    public void createNewUserTest(){
        UserResponse response = userService.create(userRequest);
        assertNotNull(response);
        assertNotNull(response.getPublicId());
        assertEquals(userRequest.getEmail(), response.getEmail());
    }

    @Test
    public void getUserTest(){

    }
}