package termination_tool.termination_tool.controller;

import termination_tool.termination_tool.dto.RewritingSystemDTO;
import termination_tool.termination_tool.dto.TerminationResult;
import termination_tool.termination_tool.model.RewritingSystem;
import termination_tool.termination_tool.service.PolynomialInterpretationService;
import termination_tool.termination_tool.util.ModelFormatter;


import org.sosy_lab.java_smt.api.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.TimeUnit;
import java.util.Optional;

@RestController
@RequestMapping("/termination")
public class TerminationController {

    private final PolynomialInterpretationService service;

    public TerminationController(PolynomialInterpretationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TerminationResult> check(@RequestBody RewritingSystemDTO dto) {
        long start = System.nanoTime();                 // Start timing the operation

        RewritingSystem sys = dto.toDomain();
        Optional<Model> modelOpt = service.findInterpretation(sys);

        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(  //(ms)
                System.nanoTime() - start);

        String raw = modelOpt.map(Object::toString).orElse(null);
        String pretty = modelOpt.map(ModelFormatter::toReadableString).orElse(null);

        return ResponseEntity.ok(
                new TerminationResult(modelOpt.isPresent(), raw, pretty, elapsedMs)
        );
    }

}
