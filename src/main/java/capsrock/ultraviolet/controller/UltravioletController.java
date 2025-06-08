package capsrock.ultraviolet.controller;

import capsrock.ultraviolet.dto.request.UltravioletRequest;
import capsrock.ultraviolet.dto.response.UltravioletResponse;
import capsrock.ultraviolet.service.UltravioletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ultraviolet")
public class UltravioletController {

    private final UltravioletService ultravioletService;

    @GetMapping
    public ResponseEntity<UltravioletResponse> getUltravioletResponse(@ModelAttribute UltravioletRequest ultravioletRequest) {
        return new ResponseEntity<>(ultravioletService.getUltravioletResponse(ultravioletRequest), HttpStatus.OK);
    }
}
