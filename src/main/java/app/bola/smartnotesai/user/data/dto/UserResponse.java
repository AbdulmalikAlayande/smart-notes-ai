package app.bola.smartnotesai.user.data.dto;

import app.bola.smartnotesai.user.data.model.SmartNotesUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * {@link SmartNotesUser} Model Response Object
 */

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponse {

    private String publicId;
    private String email;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

}
