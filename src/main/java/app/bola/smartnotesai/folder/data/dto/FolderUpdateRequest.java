package app.bola.smartnotesai.folder.data.dto;

import app.bola.smartnotesai.folder.data.model.Folder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Update Request DTO for {@link Folder} model
 * */


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FolderUpdateRequest {
	
	String name;
	String parentId;
	String publicId;
}
