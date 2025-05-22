package app.bola.smartnotesai.folder.service;

import app.bola.smartnotesai.auth.data.dto.UserRequest;
import app.bola.smartnotesai.auth.data.dto.UserResponse;
import app.bola.smartnotesai.auth.service.UserService;
import app.bola.smartnotesai.folder.data.dto.FolderRequest;
import app.bola.smartnotesai.folder.data.dto.FolderResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmartNoteFolderServiceTest {
	
	@Autowired
	FolderService folderService;
	@Autowired
	UserService userService;
	UserResponse user;
	
	
	@BeforeEach
	void setUp() {
		user = userService.create(UserRequest.builder()
				                          .email("aminaolojede@gmail.com")
				                          .username("Olojede")
				                          .password("amide555")
				                          .build());
	}
	
	@AfterEach
	void tearDown() {
		if (user != null) {
			userService.delete(user.getPublicId());
		}
		user = null;
	}
	
	@Test
	public void createNewFolderTest(){
		FolderResponse response = folderService.create(FolderRequest.builder()
				                                               .name("My Folder")
				                                               .ownerId(user.getPublicId())
				                                               .build());
		assertNotNull(response);
		assertThat(response).hasNoNullFieldsOrPropertiesExcept("parentId", "children", "notes");
	}
	
	@Test
	public void createNewFolderWithExistingParentFolderTest(){
	
	}
}