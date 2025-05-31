package app.bola.smartnotesai.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TagGeneratorResponse {
	
	/**
	 * The list of tags generated from the note content.
	 */
	private Set<String> tags;
	/**
	 * The sentiment of the note content, if applicable.
	 */
	private String sentiment;
}