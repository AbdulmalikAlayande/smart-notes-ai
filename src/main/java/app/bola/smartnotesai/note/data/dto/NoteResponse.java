package app.bola.smartnotesai.note.data.dto;

import app.bola.smartnotesai.note.data.model.Note;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * {@link Note} Model Response DTO
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteResponse {
	
	String title;
	String content;
	String summary;
	String ownerId;
	String folderId;
	String publicId;
	Set<String> tags;
	LocalDateTime createdAt;
	LocalDateTime lastModifiedAt;
}
