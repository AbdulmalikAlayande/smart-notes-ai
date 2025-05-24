package app.bola.smartnotesai.note.service;

import app.bola.smartnotesai.auth.data.dto.UserRequest;
import app.bola.smartnotesai.auth.data.dto.UserResponse;
import app.bola.smartnotesai.auth.service.AuthService;
import app.bola.smartnotesai.auth.service.UserService;
import app.bola.smartnotesai.folder.data.dto.FolderRequest;
import app.bola.smartnotesai.folder.data.dto.FolderResponse;
import app.bola.smartnotesai.folder.service.FolderService;
import app.bola.smartnotesai.note.data.dto.NoteRequest;
import app.bola.smartnotesai.note.data.dto.NoteResponse;
import app.bola.smartnotesai.note.data.dto.NoteUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmartNoteServiceTest {
	
	@Autowired
	AuthService authService;
	@Autowired
	UserService userService;
	@Autowired
	NoteService smartNoteService;
	@Autowired
	FolderService folderService;
	NoteRequest noteRequest;
	UserResponse user;
	
	@BeforeEach
	void setUp() {
		if (user != null) {
			userService.delete(user.getPublicId());
		}
		
		user = authService.create(UserRequest.builder()
				                                       .email("michealdpay1@gmail.com")
				                                       .password("password")
				                                       .username("Mike dPay")
				                                       .build());
		noteRequest = NoteRequest.builder()
				              .title("My Diary")
				              .content("This is my first note, wow! wow!! wow!!!")
				              .ownerId(user.getPublicId())
				              .build();
	}
	
	@AfterEach
	void tearDown() {
		if (user != null) {
			userService.delete(user.getPublicId());
		}
		user = null;
	}
	
	@Nested
	@DisplayName("Create Note Tests")
	class CreateNoteTests {
		
		@Test
		@DisplayName("Should create a note successfully without folder")
		public void createNewNoteTest() {
			NoteResponse response = smartNoteService.create(noteRequest);
			assertNotNull(response);
			assertThat(response).hasNoNullFieldsOrPropertiesExcept("tags", "summary", "folderId");
		}
		
		@Test
		public void createNewNoteUnderFolderTest(){
			FolderResponse folder = folderService.create(FolderRequest.builder()
					                                             .name("My Folder")
					                                             .ownerId(user.getPublicId())
					                                             .build());
			noteRequest.setFolderId(folder.getPublicId());
			
			NoteResponse response = smartNoteService.create(noteRequest);
			assertNotNull(response);
			assertThat(response).hasNoNullFieldsOrPropertiesExcept("tags", "summary", "folderId");
		}
		
		@Test
		@DisplayName("Should throw exception when creating note with non-existent owner")
		public void createNewNoteWithNonExistentOwnerTest() {
			noteRequest.setOwnerId(UUID.randomUUID().toString());
			
			assertThrows(EntityNotFoundException.class,
					() -> smartNoteService.create(noteRequest),
					"User not found");
		}
		
		@Test
		@DisplayName("Should throw exception when creating note with non-existent folder")
		public void createNewNoteWithNonExistentFolderTest() {
			noteRequest.setFolderId(UUID.randomUUID().toString());
			
			assertThrows(EntityNotFoundException.class,
					() -> smartNoteService.create(noteRequest),
					"Folder not found");
		}
		
		@Test
		@DisplayName("Should create a note with maximum content size")
		public void createNoteWithLargeContentTest() {
			noteRequest.setContent("abc ".repeat(10240));
			
		}
	}
	
	@Nested
	@DisplayName("Find Note Tests")
	class FetchNoteTests {
		
		private NoteResponse createdNote;
		
		@BeforeEach
		void setUp() {
			createdNote = smartNoteService.create(noteRequest);
		}
		
		@Test
		@DisplayName("Should find a note by public ID")
		public void findNoteByPublicIdTest() {
			NoteResponse foundNote = smartNoteService.findByPublicId(createdNote.getPublicId());
			assertNotNull(foundNote);
			assertEquals(createdNote.getPublicId(), foundNote.getPublicId());
			assertEquals(createdNote.getTitle(), foundNote.getTitle());
			assertEquals(createdNote.getContent(), foundNote.getContent());
		}
		
		@Test
		@DisplayName("Should throw exception when finding non-existent note")
		public void findNonExistentNoteTest() {
			String nonExistentId = UUID.randomUUID().toString();
			
			assertThrows(EntityNotFoundException.class,
					() -> smartNoteService.findByPublicId(nonExistentId),
					"Note not found");
		}
		
		@Test
		@DisplayName("Should find all notes by owner")
		public void findAllNotesByOwnerTest() {
			for (int count = 0; count < 5; count++) {
				NoteRequest request = NoteRequest.builder()
						                      .title("New Note " + count)
						                      .content("This is my number " + count+1 + " note")
						                      .ownerId(user.getPublicId())
						                      .build();
				smartNoteService.create(request);
			}
			Set<NoteResponse> notes = smartNoteService.findAllByOwnerId(user.getPublicId());
			
			assertNotNull(notes);
			assertThat(notes.size()).isGreaterThanOrEqualTo(5);
			for (NoteResponse note : notes) {
				assertEquals(user.getPublicId(), note.getOwnerId());
			}
		}
		
		@Test
		@DisplayName("Should return empty list for non-existent owner")
		public void findNotesByNonExistentOwnerTest() {
			String nonExistentId = UUID.randomUUID().toString();
			
			Set<NoteResponse> notes = smartNoteService.findAllByOwnerId(nonExistentId);
			
			assertNotNull(notes);
			assertThat(notes.size()).isEqualTo(0);
		}
		
		@Test
		@DisplayName("Should find all notes in a folder")
		public void findAllNotesByFolderTest() {
			
			FolderResponse folder = folderService.create(FolderRequest.builder()
					                                     .name("Test Folder")
					                                     .ownerId(user.getPublicId())
					                                     .build());
			
			for (int i = 0; i < 3; i++) {
				NoteRequest request = NoteRequest.builder()
						                      .title("Folder Note " + i)
						                      .content("Folder Content " + i)
						                      .ownerId(user.getPublicId())
						                      .folderId(folder.getPublicId())
						                      .build();
				smartNoteService.create(request);
			}
			
			Set<NoteResponse> notes = smartNoteService.findAllByFolderId(folder.getPublicId());
			
			assertNotNull(notes);
			assertThat(notes.size()).isEqualTo(3);
			for (NoteResponse note : notes) {
				assertEquals(folder.getPublicId(), note.getFolderId());
			}
		}
		
		@Test
		@DisplayName("Should return empty list for non-existent folder")
		public void findNotesByNonExistentFolderTest() {
			String nonExistentId = UUID.randomUUID().toString();
			
			Set<NoteResponse> notes = smartNoteService.findAllByFolderId(nonExistentId);
			
			assertNotNull(notes);
			assertThat(notes.size()).isEqualTo(0);
		}
		
	}
		
	@Nested
	@DisplayName("Update Note Tests")
	class UpdateNoteTest {
		
		private NoteResponse createdNote;
		
		@BeforeEach
		public void setUp() {
			createdNote = smartNoteService.create(noteRequest);
		}
		
		@Test
		@DisplayName("Should update note's title and content successfully")
		public void updateNotesTitleAndContentTest() {
			
			NoteUpdateRequest updateRequest = NoteUpdateRequest.builder()
					                            .title("Updated Title")
					                            .content("This is the updated content")
					                            .build();
			NoteResponse updatedNote = smartNoteService.update(createdNote.getPublicId(), updateRequest);
			
			assertNotNull(updatedNote);
			assertEquals("Updated Title", updatedNote.getTitle());
			assertEquals("This is the updated content", updatedNote.getContent());
			assertEquals(createdNote.getPublicId(), updatedNote.getPublicId());
		}
		
		@Test
		@DisplayName("Should update only the title")
		public void updateOnlyTitleTest() {
			NoteUpdateRequest updateRequest = NoteUpdateRequest.builder()
					                            .title("Only Title Updated")
					                            .content(createdNote.getContent())
					                            .build();
			
			NoteResponse updatedNote = smartNoteService.update(createdNote.getPublicId(), updateRequest);
			
			assertNotNull(updatedNote);
			assertEquals("Only Title Updated", updatedNote.getTitle());
			assertEquals(createdNote.getContent(), updatedNote.getContent());
		}
		
		@Test
		@DisplayName("Should update only the content")
		public void updateOnlyContentTest() {
			NoteUpdateRequest updateRequest = NoteUpdateRequest.builder()
					                            .title(createdNote.getTitle())
					                            .content("Only content updated")
					                            .build();
			
			NoteResponse updatedNote = smartNoteService.update(createdNote.getPublicId(), updateRequest);
			
			assertNotNull(updatedNote);
			assertEquals(createdNote.getTitle(), updatedNote.getTitle());
			assertEquals("Only content updated", updatedNote.getContent());
		}
		
		@Test
		@DisplayName("Should throw exception when updating non-existent note")
		public void updateNonExistentNoteTest(){
			String nonExistentId = UUID.randomUUID().toString();
			NoteUpdateRequest updateRequest = NoteUpdateRequest.builder()
					                            .title("Updated Title")
					                            .content("Updated content")
					                            .build();
			
			assertThrows(EntityNotFoundException.class, () -> smartNoteService.update(nonExistentId, updateRequest));
		}
	}
	
	@Nested
	@DisplayName("Edge Cases Tests")
	class EdgeCasesTest {
		
		@Test
		@DisplayName("Should handle create note with special characters")
		public void createNoteWithSpecialCharactersTest() {
			
			NoteRequest request = NoteRequest.builder()
					                         .title("Special Characters Test")
					                         .content(
												"""
												This note contains special characters:
												!@#$%^&*()_+{}|:"<>?~`-=[]\\;',./χψω
												你好, こんにちは, 안녕하세요, مرحبا
												"""
					                         )
					                         .ownerId(user.getPublicId())
					                         .build();
			
			NoteResponse response = smartNoteService.create(request);
			
			assertNotNull(response);
			assertEquals(request.getTitle(), response.getTitle());
			assertEquals(request.getContent(), response.getContent());
		}
		
		@Test
		@DisplayName("Should handle create note with HTML content")
		public void createNoteWithHtmlContentTest() {
			NoteRequest htmlRequest = NoteRequest.builder()
					                          .title("HTML Content")
					                          .content("<h1>This is a heading</h1><p>This is a paragraph with <b>bold</b> and <i>italic</i> text.</p><script>alert('XSS attempt');</script>")
					                          .ownerId(user.getPublicId())
					                          .build();
			
			NoteResponse response = smartNoteService.create(htmlRequest);
			
			assertNotNull(response);
			assertNotNull(response.getContent());
			
		}
		
		@Test
		@DisplayName("Should handle create note with null title")
		public void createNoteWithNullTitleTest() {
			noteRequest.setTitle(null);
			assertThrows(Exception.class, () -> smartNoteService.create(noteRequest));
		}
		
		@Test
		@DisplayName("Should handle create note with null content")
		public void createNoteWithNullContentTest() {
			noteRequest.setContent(null);
			assertThrows(Exception.class, () -> smartNoteService.create(noteRequest));
		}
		
		@Test
		@DisplayName("Should handle concurrent modifications")
		public void concurrentModificationsTest() {
		
		}
		
		@Test
		@DisplayName("Should handle emoji in note content")
		public void emojiInContentTest() {
			
			NoteRequest emojiRequest = NoteRequest.builder()
					                           .title("Emoji Test ")
					                           .content("This note contains emoji: ")
					                           .ownerId(user.getPublicId())
					                           .build();
			
			NoteResponse response = smartNoteService.create(emojiRequest);
			
			assertNotNull(response);
			assertEquals(emojiRequest.getTitle(), response.getTitle());
			assertEquals(emojiRequest.getContent(), response.getContent());
			
		}
		
		@Test
		@DisplayName("Should handle multi-language content")
		public void multiLanguageContentTest() {
			NoteRequest multiLangRequest = NoteRequest.builder()
					                               .title("Multi-language Note")
					                               .content("English text. Texto en español. 中文文本. Русский текст. عربى نص. हिंदी पाठ.")
					                               .ownerId(user.getPublicId())
					                               .build();
			
			NoteResponse response = smartNoteService.create(multiLangRequest);
			
			assertNotNull(response);
			assertEquals(multiLangRequest.getContent(), response.getContent());
		}
		
	}
	
	@Nested
	@DisplayName("AI Feature Tests")
	class AIFeatureTests {
		
		@BeforeEach
		public void setUp() {
		
		}
		
		@SneakyThrows
		@Test
		@DisplayName("Should generate summary for note on creation")
		public void generateSummaryOnNoteCreationTest() {
			NoteRequest contentRequest = NoteRequest.builder()
					                               .title("My Diary")
					                               .content("""
													**Artificial Intelligence (AI) Overview**
													Artificial Intelligence (AI) is a subfield of computer science that focuses on creating intelligent machines that can perform tasks that typically require human intelligence. AI involves the development of algorithms and statistical models that enable machines to learn from data, reason, and interact with humans.
													\s
													**Spring AI Project**
													The Spring AI project is a part of the Spring Framework, which is a popular Java-based framework for building enterprise-level applications. The Spring AI project provides a set of libraries and tools for building AI-powered applications using Java. However, as seen in the provided text, there are some issues with the Spring AI project, such as:
													* The `spring-ai-openai-spring-boot-starter` dependency is not found due to a change in the group ID from `org.springframework.ai` to `org.springframework.experimental.ai` when the project was in the experimental GitHub organization.
													* The `OpenAiChatClient` class has been renamed to `OpenAiChatMode1`.
													\s
													**AI Components**
													Some of the key components of AI include:
													* **Machine Learning (ML)**: A type of AI that involves training algorithms on data to make predictions or decisions.
													* **Deep Learning (DL)**: A type of ML that involves the use of neural networks to analyze data.
													* **Natural Language Processing (NLP)**: A type of AI that involves the analysis and processing of human language.
													* **Computer Vision**: A type of AI that involves the analysis and processing of visual data.
													\s
													**AI Applications**
													AI has a wide range of applications, including:
													* **Virtual Assistants**: AI-powered virtual assistants, such as Siri, Alexa, and Google Assistant, can perform tasks such as setting reminders, sending messages, and making calls.
													* **Image Recognition**: AI-powered image recognition systems can analyze images and identify objects, people, and scenes.
													* **Predictive Maintenance**: AI-powered predictive maintenance systems can analyze data from sensors and predict when maintenance is required to prevent equipment failures.
													* **Chatbots**: AI-powered chatbots can interact with humans and provide customer support.
													""")
					                               .ownerId(user.getPublicId())
					                               .build();
			
			NoteResponse response = smartNoteService.create(contentRequest);
			
			assertNotNull(response);
			assertNotNull(response.getPublicId());
			
			Thread.sleep(15000);
			
			NoteResponse updatedNote = smartNoteService.findByPublicId(response.getPublicId());
			
			assertNotNull(updatedNote);
			assertNotNull(updatedNote.getTags());
			assertFalse(updatedNote.getTags().isEmpty());
			assertNotNull(updatedNote.getSummary());
			assertFalse(updatedNote.getSummary().isEmpty());
			
			// Check if tags contain relevant AI-related content
			boolean hasRelevantTags = updatedNote.getTags().stream()
					                          .anyMatch(tag -> tag.toLowerCase().contains("ai") ||
							                                           tag.toLowerCase().contains("artificial") ||
							                                           tag.toLowerCase().contains("intelligence") ||
							                                           tag.toLowerCase().contains("technology") ||
							                                           tag.toLowerCase().contains("programming"));
			
			assertTrue(hasRelevantTags, "Expected tags to contain AI-related content");
		}
		
		
	}
}