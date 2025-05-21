package app.bola.smartnotesai.note.data.model;

import app.bola.smartnotesai.auth.data.model.User;
import app.bola.smartnotesai.common.data.model.BaseModel;
import app.bola.smartnotesai.folder.data.model.Folder;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Note extends BaseModel {
    
    @Column(nullable = false)
    private String title;
    private String content;
    private String summary;
    @ManyToOne
    private User owner;
    @ManyToOne
    @JoinColumn(name = "folder_id", referencedColumnName = "id")
    private Folder folder;
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> tags = new LinkedHashSet<>();
    
}



