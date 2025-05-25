package app.bola.smartnotesai.folder.data.model;

import app.bola.smartnotesai.user.data.model.User;
import app.bola.smartnotesai.common.data.model.BaseModel;
import app.bola.smartnotesai.note.data.model.Note;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Folder extends BaseModel {

    private String name;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private User owner;
    
    @ManyToOne
    private Folder parent;
    
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Note> notes = new LinkedHashSet<>();
    
    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Folder> children = new LinkedHashSet<>();
    
    @Override
    public String toString() {
        return new StringJoiner(", ", Folder.class.getSimpleName() + "[", "]")
                       .add("name: " + name)
                       .add("notes: " + notes)
                       .add("owner: " + owner)
                       .add("parent: " + parent)
                       .add("children: " + children)
                       .add("publicId: " + getPublicId())
                       .add("createdAt: " + getCreatedAt())
                       .add("lastModifiedAt: " + getLastModifiedAt())
                       .toString();
    }
}
