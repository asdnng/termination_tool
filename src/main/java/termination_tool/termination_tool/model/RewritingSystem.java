package termination_tool.termination_tool.model;

import java.util.List;

public class RewritingSystem {
    private final List<RewriteRule> rules;

    public RewritingSystem(List<RewriteRule> rules) {
        this.rules = rules;
    }

    public List<RewriteRule> rules() {
        return rules;
    }
}