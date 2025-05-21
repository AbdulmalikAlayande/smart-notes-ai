package app.bola.smartnotesai.folder.data.dto;

import app.bola.smartnotesai.note.data.dto.NoteResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.StringJoiner;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FolderResponse {
	
	String name;
	String ownerId;
	String publicId;
	String parentId;
	LocalDateTime createdAt;
	Set<NoteResponse> notes;
	LocalDateTime lastModifiedAt;
	Set<FolderResponse> children;
	
	@Override
	public String toString() {
		return new StringJoiner(", ", FolderResponse.class.getSimpleName() + "[", "]")
				       .add("name: " + name)
				       .add("ownerId: " + ownerId)
				       .add("publicId: " + publicId)
				       .add("parentId: " + parentId)
				       .add("createdAt: " + createdAt)
				       .add("notes: " + notes)
				       .add("lastModifiedAt: " + lastModifiedAt)
				       .add("children: " + children)
				       .toString();
	}
}
