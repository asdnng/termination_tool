package termination_tool.termination_tool.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TerminationResult(
        boolean terminating,
        String rawModel,
        String pretty,
        @JsonProperty("elapsed_ms") long elapsedMs  // JSON 키를 snake_case로
) {
    public TerminationResult(boolean terminating, String rawModel, String pretty) {
        this(terminating, rawModel, pretty, -1);
    }
}