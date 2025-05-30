package app.bola.smartnotesai.note.data.dto;

import app.bola.smartnotesai.note.data.model.Note;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * {@link Note} model update request DTO
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteUpdateRequest {
	
	String noteId;
	String title;
	String content;
	String summary;
	List<String> tags;
}

