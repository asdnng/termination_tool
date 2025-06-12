package termination_tool.termination_tool.util;

import org.sosy_lab.java_smt.api.Model;
import org.sosy_lab.java_smt.api.Model.ValueAssignment;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ModelFormatter {
    private ModelFormatter() {}

    private static final Pattern C_VAR = Pattern.compile("c_(\\d+)");
    private static final Pattern D_VAR = Pattern.compile("d_(\\d+)");

    /**
     * Convert SMT model to "readable" format.
     */
    public static String toReadableString(Model model) {
        Map<Integer, BigInteger> coef = new HashMap<>(); // ASCII -> c
        Map<Integer, BigInteger> constant = new HashMap<>(); // ASCII -> d

        for (ValueAssignment va : model) {
            String name = va.getName();
            Matcher mC = C_VAR.matcher(name);
            Matcher mD = D_VAR.matcher(name);
            if (mC.matches()) {
                int ascii = Integer.parseInt(mC.group(1));
                coef.put(ascii, (BigInteger) va.getValue());
            } else if (mD.matches()) {
                int ascii = Integer.parseInt(mD.group(1));
                constant.put(ascii, (BigInteger) va.getValue());
            }
        }

        StringBuilder sb = new StringBuilder();
        coef.keySet().stream().sorted().forEach(ascii -> {
            char symbol = (char) ascii.intValue();
            BigInteger c = coef.get(ascii);
            BigInteger d = constant.getOrDefault(ascii, BigInteger.ZERO);
            if (sb.length() > 0) sb.append("; ");
            sb.append(symbol).append("(x)=").append(c).append("x");
            if (d.signum() >= 0) sb.append("+").append(d);
            else sb.append(d);
        });
        return sb.toString();
    }
}