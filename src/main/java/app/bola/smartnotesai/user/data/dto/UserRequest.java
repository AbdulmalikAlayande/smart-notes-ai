package app.bola.smartnotesai.user.data.dto;

import app.bola.smartnotesai.user.data.model.SmartNotesUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * {@link SmartNotesUser} Model Request Object
 */

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest implements Serializable {

    String email;
    String username;
    String password;
    
}
