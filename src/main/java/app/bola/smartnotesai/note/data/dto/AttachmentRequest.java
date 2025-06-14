package app.bola.smartnotesai.note.data.dto;

import app.bola.smartnotesai.note.data.model.Attachment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * Request DTO for {@link Attachment}
 */

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttachmentRequest implements Serializable {
	
	String description;
	String mediaType;
	@NotNull
	MultipartFile file;
	
}