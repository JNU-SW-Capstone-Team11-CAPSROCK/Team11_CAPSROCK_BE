package capsrock.fineDust.controller;

import capsrock.fineDust.dto.request.FineDustRequest;
import capsrock.fineDust.dto.response.FineDustResponse;
import capsrock.fineDust.service.FineDustService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/finedust")
@RequiredArgsConstructor
public class FineDustController {

    private final FineDustService fineDustService;

    @GetMapping
    public ResponseEntity<FineDustResponse> getFineDust(@ModelAttribute FineDustRequest fineDustRequest) {
        return new ResponseEntity<>(fineDustService.getFineDustResponse(fineDustRequest), HttpStatus.OK);
    }

}
