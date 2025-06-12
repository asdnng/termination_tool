package termination_tool.termination_tool.dto;

import termination_tool.termination_tool.model.RewriteRule;
import termination_tool.termination_tool.model.RewritingSystem;
import java.util.List;

public record RewritingSystemDTO(List<RewriteRule> rules) {
    public RewritingSystem toDomain() {
        return new RewritingSystem(rules);
    }
}
