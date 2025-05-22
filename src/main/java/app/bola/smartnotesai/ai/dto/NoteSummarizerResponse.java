package app.bola.smartnotesai.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.StringJoiner;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NoteSummarizerResponse {
	
	private String summary;
	private List<String> keyPoints;
	private List<String> tags;
	
	@Override
	public String toString() {
		return new StringJoiner(", ", NoteSummarizerResponse.class.getSimpleName() + "[\n", "]")
				       .add("summary: " + summary + "\n")
				       .add("keyPoints: " + keyPoints + "\n")
				       .add("tags: " + tags + "\n")
				       .toString();
	}
}
