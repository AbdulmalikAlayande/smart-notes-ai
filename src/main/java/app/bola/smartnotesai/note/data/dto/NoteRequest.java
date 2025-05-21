package app.bola.smartnotesai.note.data.dto;

import app.bola.smartnotesai.note.data.model.Note;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * {@link Note} model request DTO
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteRequest {
	
	@NotNull
	String title;
	String content;
	@NotNull
	String ownerId;
	String folderId;
	List<String> tags;
}

