package app.bola.smartnotesai.auth.data.model;

import app.bola.smartnotesai.common.data.model.BaseModel;
import app.bola.smartnotesai.folder.data.model.Folder;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringJoiner;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseModel {

    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    private String password;
    
    @Builder.Default
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Folder> folders = new LinkedHashSet<>();
    
    
    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                       .add("id: " + getId())
                       .add("publicId: " + getPublicId())
                       .add("email: " + email)
                       .add("username: " + username)
                       .add("password: " + password)
                       .toString();
    }
}
