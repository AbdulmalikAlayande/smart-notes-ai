package app.bola.smartnotesai.note.data.model;

import app.bola.smartnotesai.common.data.model.BaseModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Attachment extends BaseModel {

    private String fileName;
    
    @URL(protocol = "", host = "", regexp = "")
    String fileUrl;
    
    private String mediaType;
    private long fileSize;
    private String description;
    
    @ManyToOne
    private Note note;
}