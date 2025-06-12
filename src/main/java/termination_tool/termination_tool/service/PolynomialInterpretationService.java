package termination_tool.termination_tool.service;

import termination_tool.termination_tool.model.RewritingSystem;
import org.springframework.stereotype.Service;
import org.sosy_lab.java_smt.api.Model;
import termination_tool.termination_tool.util.PolynomialUtils;

import java.util.Optional;

@Service

public class PolynomialInterpretationService {

    //private final WeightSolver weightSolver;
    private final Z3Solver solver;

    public PolynomialInterpretationService(Z3Solver solver) {
        this.solver = solver;
    }

    /**
     * Attempts to find a polynomial interpretation proving termination.
     */
    public Optional<Model> findInterpretation(RewritingSystem system) {
        // 1. Encode alphabet: assign polynomial for each symbol
        // 2. For every rule l -> r encode: P(l) > P(r)
        // 3. Enforce monotonicity & positivity constraints
        // 4. Ask Z3 for satisfiable model
        // x + d first
        Optional<Model> unitSlopeModel = solver.solve(system, /*unitSlope=*/true);
        if (unitSlopeModel.isPresent()) {
            return unitSlopeModel;
        }
        //and then cx + d
        return solver.solve(system, /*unitSlope=*/false);
    }
}
