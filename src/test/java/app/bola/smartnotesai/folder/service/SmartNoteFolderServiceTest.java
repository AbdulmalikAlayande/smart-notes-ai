package app.bola.smartnotesai.folder.service;

import app.bola.smartnotesai.folder.data.dto.FolderUpdateRequest;
import app.bola.smartnotesai.note.data.dto.NoteRequest;
import app.bola.smartnotesai.note.data.dto.NoteResponse;
import app.bola.smartnotesai.note.service.NoteService;
import app.bola.smartnotesai.user.data.dto.UserRequest;
import app.bola.smartnotesai.user.data.dto.UserResponse;
import app.bola.smartnotesai.user.service.UserService;
import app.bola.smartnotesai.folder.data.dto.FolderRequest;
import app.bola.smartnotesai.folder.data.dto.FolderResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmartNoteFolderServiceTest {
	
	@Autowired
	FolderService folderService;
	@Autowired
	UserService userService;
	@Autowired
	NoteService noteService;
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
	
	@Nested
	@DisplayName("Create Folder Tests")
	class CreateFolderTests {
		
		@Test
		@DisplayName("Should create a folder successfully without parent")
		public void createNewFolderTest() {
			FolderResponse response = folderService.create(FolderRequest.builder()
					                                               .name("My Folder")
					                                               .ownerId(user.getPublicId())
					                                               .build());
			assertNotNull(response);
			assertThat(response).hasNoNullFieldsOrPropertiesExcept("parentId", "children", "notes");
			assertEquals("My Folder", response.getName());
			assertEquals(user.getPublicId(), response.getOwnerId());
		}
		
		@Test
		@DisplayName("Should create a folder with a parent folder")
		public void createNewFolderWithExistingParentFolderTest() {
			// Create parent folder
			FolderResponse parentFolder = folderService.create(FolderRequest.builder()
					                                                   .name("Parent Folder")
					                                                   .ownerId(user.getPublicId())
					                                                   .build());
			
			// Create child folder
			FolderResponse childFolder = folderService.create(FolderRequest.builder()
					                                                  .name("Child Folder")
					                                                  .ownerId(user.getPublicId())
					                                                  .parentId(parentFolder.getPublicId())
					                                                  .build());
			
			assertNotNull(childFolder);
			assertEquals("Child Folder", childFolder.getName());
			assertEquals(user.getPublicId(), childFolder.getOwnerId());
			assertEquals(parentFolder.getPublicId(), childFolder.getParentId());
		}
		
		@Test
		@DisplayName("Should throw exception when creating folder with non-existent owner")
		public void createFolderWithNonExistentOwnerTest() {
			FolderRequest request = FolderRequest.builder()
					                        .name("Invalid Owner Folder")
					                        .ownerId(UUID.randomUUID().toString())
					                        .build();
			
			assertThrows(EntityNotFoundException.class,
					() -> folderService.create(request),
					"Folder owner with Id does not exist");
		}
		
		@Test
		@DisplayName("Should throw exception when creating folder with non-existent parent")
		public void createFolderWithNonExistentParentTest() {
			FolderRequest request = FolderRequest.builder()
					                        .name("Invalid Parent Folder")
					                        .ownerId(user.getPublicId())
					                        .parentId(UUID.randomUUID().toString())
					                        .build();
			
			assertThrows(EntityNotFoundException.class,
					() -> folderService.create(request),
					"Parent folder with Id does not exist");
		}
	}
	
	@Nested
	@DisplayName("Find Folder Tests")
	class FindFolderTests {
		
		private FolderResponse createdFolder;
		
		@BeforeEach
		void setUp() {
			createdFolder = folderService.create(FolderRequest.builder()
					                                     .name("Test Folder")
					                                     .ownerId(user.getPublicId())
					                                     .build());
		}
		
		@Test
		@DisplayName("Should find a folder by public ID")
		public void findFolderByPublicIdTest() {
			FolderResponse foundFolder = folderService.findByPublicId(createdFolder.getPublicId());
			
			assertNotNull(foundFolder);
			assertEquals(createdFolder.getPublicId(), foundFolder.getPublicId());
			assertEquals(createdFolder.getName(), foundFolder.getName());
			assertEquals(createdFolder.getOwnerId(), foundFolder.getOwnerId());
		}
		
		@Test
		@DisplayName("Should throw exception when finding non-existent folder")
		public void findNonExistentFolderTest() {
			String nonExistentId = UUID.randomUUID().toString();
			
			assertThrows(EntityNotFoundException.class,
					() -> folderService.findByPublicId(nonExistentId),
					"Folder not found");
		}
		
		@Test
		@DisplayName("Should find all folders")
		public void findAllFoldersTest() {
			// Create additional folders
			for (int i = 0; i < 3; i++) {
				folderService.create(FolderRequest.builder()
						                     .name("Additional Folder " + i)
						                     .ownerId(user.getPublicId())
						                     .build());
			}
			
			Set<FolderResponse> folders = folderService.findAll();
			
			assertNotNull(folders);
			assertThat(folders.size()).isGreaterThanOrEqualTo(4); // At least the 4 we created
		}
		
		@Test
		@DisplayName("Should find all folders by owner")
		public void findAllFoldersByOwnerTest() {
			// Create another user
			UserResponse anotherUser = userService.create(UserRequest.builder()
					                                              .email("another@example.com")
					                                              .username("AnotherUser")
					                                              .password("password123")
					                                              .build());
			
			// Create folders for both users
			for (int i = 0; i < 3; i++) {
				folderService.create(FolderRequest.builder()
						                     .name("User1 Folder " + i)
						                     .ownerId(user.getPublicId())
						                     .build());
				
				folderService.create(FolderRequest.builder()
						                     .name("User2 Folder " + i)
						                     .ownerId(anotherUser.getPublicId())
						                     .build());
			}
			
			// Find folders for first user
			Set<FolderResponse> user1Folders = folderService.findByOwner(user.getPublicId());
			
			assertNotNull(user1Folders);
			assertThat(user1Folders.size()).isGreaterThanOrEqualTo(4); // At least 4 folders for user1
			
			for (FolderResponse folder : user1Folders) {
				assertEquals(user.getPublicId(), folder.getOwnerId());
			}
			
			// Cleanup
			userService.delete(anotherUser.getPublicId());
		}
	}
	
	@Nested
	@DisplayName("Update Folder Tests")
	class UpdateFolderTests {
		
		private FolderResponse createdFolder;
		
		@BeforeEach
		void setUp() {
			createdFolder = folderService.create(FolderRequest.builder()
					                                     .name("Original Folder")
					                                     .ownerId(user.getPublicId())
					                                     .build());
		}
		
		@Test
		@DisplayName("Should update folder name")
		public void updateFolderNameTest() {
			FolderUpdateRequest updateRequest = new FolderUpdateRequest();
			updateRequest.setPublicId(createdFolder.getPublicId());
			updateRequest.setName("Updated Folder Name");
			
			FolderResponse updatedFolder = folderService.update(updateRequest);
			
			assertNotNull(updatedFolder);
			assertEquals("Updated Folder Name", updatedFolder.getName());
			assertEquals(createdFolder.getPublicId(), updatedFolder.getPublicId());
			assertEquals(createdFolder.getOwnerId(), updatedFolder.getOwnerId());
		}
		
		@Test
		@DisplayName("Should update folder parent")
		public void updateFolderParentTest() {
			// Create a new parent folder
			FolderResponse newParent = folderService.create(FolderRequest.builder()
					                                                .name("New Parent Folder")
					                                                .ownerId(user.getPublicId())
					                                                .build());
			
			FolderUpdateRequest updateRequest = new FolderUpdateRequest();
			updateRequest.setPublicId(createdFolder.getPublicId());
			updateRequest.setParentId(newParent.getPublicId());
			
			FolderResponse updatedFolder = folderService.update(updateRequest);
			
			assertNotNull(updatedFolder);
			assertEquals(newParent.getPublicId(), updatedFolder.getParentId());
		}
		
		@Test
		@DisplayName("Should throw exception when updating non-existent folder")
		public void updateNonExistentFolderTest() {
			FolderUpdateRequest updateRequest = new FolderUpdateRequest();
			updateRequest.setPublicId(UUID.randomUUID().toString());
			updateRequest.setName("Updated Name");
			
			assertThrows(EntityNotFoundException.class,
					() -> folderService.update(updateRequest),
					"Folder not found");
		}
		
		@Test
		@DisplayName("Should throw exception when updating with non-existent parent")
		public void updateWithNonExistentParentTest() {
			FolderUpdateRequest updateRequest = new FolderUpdateRequest();
			updateRequest.setPublicId(createdFolder.getPublicId());
			updateRequest.setParentId(UUID.randomUUID().toString());
			
			assertThrows(EntityNotFoundException.class,
					() -> folderService.update(updateRequest),
					"Parent folder not found");
		}
	}
	
	@Nested
	@DisplayName("Delete Folder Tests")
	class DeleteFolderTests {
		
		private FolderResponse createdFolder;
		
		@BeforeEach
		void setUp() {
			createdFolder = folderService.create(FolderRequest.builder()
					                                     .name("Folder to Delete")
					                                     .ownerId(user.getPublicId())
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
		@DisplayName("Should delete a folder")
		public void deleteFolderTest() {
			folderService.delete(createdFolder.getPublicId());
			
			assertThrows(EntityNotFoundException.class,
					() -> folderService.findByPublicId(createdFolder.getPublicId()),
					"Folder not found");
		}
		
		@Test
		@DisplayName("Should delete a folder with notes")
		public void deleteFolderWithNotesTest() {
			// Create a note in the folder
			NoteRequest noteRequest = NoteRequest.builder()
					                          .title("Test Note")
					                          .content("This is a test note")
					                          .ownerId(user.getPublicId())
					                          .folderId(createdFolder.getPublicId())
					                          .build();
			
			NoteResponse noteResponse = noteService.create(noteRequest);
			
			// Delete the folder
			folderService.delete(createdFolder.getPublicId());
			
			// Verify folder is deleted
			assertThrows(EntityNotFoundException.class,
					() -> folderService.findByPublicId(createdFolder.getPublicId()),
					"Folder not found");
			
			// Verify note is still accessible but no longer has a folder
			NoteResponse updatedNote = noteService.findByPublicId(noteResponse.getPublicId());
			assertNotNull(updatedNote);
			assertNull(updatedNote.getFolderId());
		}
		
		@Test
		@DisplayName("Should delete a folder with child folders")
		public void deleteFolderWithChildFoldersTest() {
			// Create a child folder
			FolderResponse childFolder = folderService.create(FolderRequest.builder()
					                                                  .name("Child Folder")
					                                                  .ownerId(user.getPublicId())
					                                                  .parentId(createdFolder.getPublicId())
					                                                  .build());
			
			// Delete the parent folder
			folderService.delete(createdFolder.getPublicId());
			
			// Verify parent folder is deleted
			assertThrows(EntityNotFoundException.class,
					() -> folderService.findByPublicId(createdFolder.getPublicId()),
					"Parent folder not found");
			
			// Verify child folder is still accessible but no longer has a parent
			FolderResponse updatedChildFolder = folderService.findByPublicId(childFolder.getPublicId());
			assertNotNull(updatedChildFolder);
			assertNull(updatedChildFolder.getParentId());
		}
	}
	
	@Nested
	@DisplayName("Edge Cases Tests")
	class EdgeCasesTests {
		
		@Test
		@DisplayName("Should handle folder with special characters in name")
		public void folderWithSpecialCharactersTest() {
			FolderRequest request = FolderRequest.builder()
					                        .name("Special Folder !@#$%^&*()_+{}|:\"<>?~`-=[]\\;',./χψω")
					                        .ownerId(user.getPublicId())
					                        .build();
			
			FolderResponse response = folderService.create(request);
			
			assertNotNull(response);
			assertEquals(request.getName(), response.getName());
		}
		
		@Test
		@DisplayName("Should handle folder with very long name")
		public void folderWithLongNameTest() {
			String longName = "Very Long Folder Name ".repeat(20); // 400+ characters
			
			FolderRequest request = FolderRequest.builder()
					                        .name(longName)
					                        .ownerId(user.getPublicId())
					                        .build();
			
			FolderResponse response = folderService.create(request);
			
			assertNotNull(response);
			assertEquals(longName, response.getName());
		}
		
		@Test
		@DisplayName("Should handle folder with null name")
		public void folderWithNullNameTest() {
			FolderRequest request = FolderRequest.builder()
					                        .name(null)
					                        .ownerId(user.getPublicId())
					                        .build();
			
			assertThrows(Exception.class, () -> folderService.create(request));
		}
		
		@Test
		@DisplayName("Should handle folder with empty name")
		public void folderWithEmptyNameTest() {
			FolderRequest request = FolderRequest.builder()
					                        .name("")
					                        .ownerId(user.getPublicId())
					                        .build();
			
			assertThrows(Exception.class, () -> folderService.create(request));
		}
	}
}