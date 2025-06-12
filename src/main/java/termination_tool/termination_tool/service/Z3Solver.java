package termination_tool.termination_tool.service;

import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import scala.annotation.meta.param;
import termination_tool.termination_tool.model.RewriteRule;
import termination_tool.termination_tool.model.RewritingSystem;
import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.api.*;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Optional;
import java.util.*;
@Component
public class Z3Solver {

    private final SolverContext ctx;
    private final FormulaManager fmgr;
    private final IntegerFormulaManager imgr;
    private final BooleanFormulaManager bmgr;

    public Z3Solver() throws SolverException, IOException, InterruptedException, InvalidConfigurationException {
        ctx = SolverContextFactory.createSolverContext(
                Configuration.defaultConfiguration(),
                LogManager.createNullLogManager(),
                ShutdownManager.create().getNotifier(),
                SolverContextFactory.Solvers.Z3);
        fmgr = ctx.getFormulaManager();
        imgr = fmgr.getIntegerFormulaManager();
        bmgr = fmgr.getBooleanFormulaManager();
    }
    private record LinExpr(IntegerFormula m, IntegerFormula p) {}

    public Optional<Model> solve(RewritingSystem sys) {
        return solve(sys, false);
    }

    public Optional<Model> solve(RewritingSystem sys, boolean unitSlope) {
        // 1. Collect alphabet
        Set<Character> alphabet = new HashSet<>();
        for (RewriteRule rule : sys.rules()) {
            rule.left().chars().forEach(c -> alphabet.add((char) c));
            rule.right().chars().forEach(c -> alphabet.add((char) c));
        }

        // 2. Create an integer variable for each symbol weight
        Map<Character, LinExpr> symbolInterp = new HashMap<>();
        try (ProverEnvironment prover = ctx.newProverEnvironment(SolverContext.ProverOptions.GENERATE_MODELS)) {
            IntegerFormula one = imgr.makeNumber(1);
            IntegerFormula zero = imgr.makeNumber(0);

            for (char c : alphabet) {

                IntegerFormula da = imgr.makeVariable("d_" + (int) c);
                prover.addConstraint(imgr.greaterOrEquals(da, zero));    // d_a ≥ 0
                IntegerFormula ca;

                if (unitSlope) {
                    ca = imgr.makeVariable("c_" + (int) c);
                    prover.addConstraint(imgr.equal(ca, one)); // c_a = 1 fix ..(x+d)
                } else {
                    ca = imgr.makeVariable("c_" + (int) c);
                    prover.addConstraint(imgr.greaterOrEquals(ca, one));  // c_a ≥ 1  (cx+d interpretation)
                }
                symbolInterp.put(c, new LinExpr(ca, da));
            }

            // 3. For each rule  ℓ -> r  add (mℓ>mr) ∨ (mℓ=mr ∧ pℓ>pr)
            for (RewriteRule rule : sys.rules()) {
                LinExpr leftExpr  = interpret(rule.left(), symbolInterp);
                LinExpr rightExpr = interpret(rule.right(), symbolInterp);

                //mℓ − mr
                IntegerFormula mDiff = imgr.subtract(leftExpr.m(), rightExpr.m());
                //pℓ − pr
                IntegerFormula pDiff = imgr.subtract(leftExpr.p(), rightExpr.p());
                //decrease condition (mℓ > mr)  ∨  (mℓ = mr = (0slope)  ∧  pℓ > pr)
                BooleanFormula decrease = bmgr.or(
                        imgr.greaterThan(mDiff, zero),
                        bmgr.and(imgr.equal(mDiff, zero), imgr.greaterThan(pDiff, zero))
                );
                prover.addConstraint(decrease);
            }

            // 4. SAT check
            if (prover.isUnsat()) {
                return Optional.empty();
            }
            return Optional.of(prover.getModel());
        } catch (SolverException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //Compose interpretations from right to left and return m, p so that  [word](x)=m·x+p
    private LinExpr interpret(String word, Map<Character, LinExpr> table) {
        IntegerFormula m = imgr.makeNumber(1);   // identity: x ↦ 1·x + 0
        IntegerFormula p = imgr.makeNumber(0);
        for (int i = word.length() - 1; i >= 0; i--) {
            LinExpr cd = table.get(word.charAt(i));
            // new_m = c·m
            IntegerFormula newM = imgr.multiply(cd.m(), m);
            // new_p = c·p + d
            IntegerFormula newP = imgr.add(imgr.multiply(cd.m(), p), cd.p());
            m = newM;
            p = newP;
        }
        return new LinExpr(m, p);
    }
}
