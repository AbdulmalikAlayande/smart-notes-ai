package app.bola.smartnotesai.ai.controller;

import app.bola.smartnotesai.ai.dto.NoteSummarizerResponse;
import app.bola.smartnotesai.ai.dto.TagGeneratorResponse;
import app.bola.smartnotesai.ai.service.NoteSummarizer;
import app.bola.smartnotesai.ai.service.TagGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {
    private final NoteSummarizer noteSummarizer;
    private final TagGenerator tagGenerator;

    @Autowired
    public AiController(NoteSummarizer noteSummarizer, TagGenerator tagGenerator) {
        this.noteSummarizer = noteSummarizer;
        this.tagGenerator = tagGenerator;
    }

    @Operation(
        summary = "Generate a summary for a note",
        description = "Uses AI to summarize the provided note content."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Summary generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/generate-summary")
    public ResponseEntity<NoteSummarizerResponse> summarize(
        @Parameter(description = "Request body containing note content", required = true)
        @RequestBody Map<String, String> request) {
        String noteContent = request.get("content");
        NoteSummarizerResponse summary = noteSummarizer.generateSummary(noteContent);
        return ResponseEntity.ok(summary);
    }

    @Operation(
        summary = "Generate tags for a note",
        description = "Uses AI to generate relevant tags for the provided note content."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tags generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/generate-tags")
    public ResponseEntity<TagGeneratorResponse> generateTags(
        @Parameter(description = "Request body containing note content", required = true)
        @RequestBody Map<String, String> request) {
        String noteContent = request.get("content");
        var tags = tagGenerator.generateTags(noteContent);
        return ResponseEntity.ok(tags);
    }
}
