package app.bola.smartnotesai.note.data.model;

import app.bola.smartnotesai.auth.data.model.User;
import app.bola.smartnotesai.common.data.model.BaseModel;
import app.bola.smartnotesai.folder.data.model.Folder;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Collection;
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
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(length = 1000)
    private String summary;
    
    @ManyToOne
    private User owner;
    
    @ManyToOne
    @JoinColumn(name = "folder_id", referencedColumnName = "id")
    private Folder folder;
    
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> tags = new LinkedHashSet<>();
    
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> keyPoints = new LinkedHashSet<>();
    
    
    @Override
    public String toString() {
        return "{\n" +
		               "  \"id\": \"" + escapeJson(getId()) + "\",\n" +
		               "  \"publicId\": \"" + escapeJson(getPublicId()) + "\",\n" +
		               "  \"title\": \"" + escapeJson(getTitle()) + "\",\n" +
		               "  \"content\": \"" + escapeJson(getContent()) + "\",\n" +
		               "  \"summary\": \"" + escapeJson(getSummary()) + "\",\n" +
		               "  \"owner\": " + (getOwner() != null ? getOwner().toString() : "null") + ",\n" +
		               "  \"folder\": " + (getFolder() != null ? getFolder().toString() : "null") + ",\n" +
		               "  \"tags\": " + formatCollection(getTags()) + ",\n" +
		               "  \"keyPoints\": " + formatCollection(getKeyPoints()) + ",\n" +
		               "  \"createdAt\": \"" + getCreatedAt() + "\",\n" +
		               "  \"lastModifiedAt\": \"" + getLastModifiedAt() + "\"\n" +
		               "}";
    }
    
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                       .replace("\"", "\\\"")
                       .replace("\n", "\\n")
                       .replace("\r", "\\r")
                       .replace("\t", "\\t");
    }
    
    private String formatCollection(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        boolean first = true;
        for (Object item : collection) {
            if (!first) sb.append(",\n");
            sb.append("    \"").append(escapeJson(item.toString())).append("\"");
            first = false;
        }
        sb.append("\n  ]");
        return sb.toString();
    }
}



