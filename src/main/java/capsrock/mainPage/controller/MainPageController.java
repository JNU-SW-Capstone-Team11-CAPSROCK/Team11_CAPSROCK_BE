package capsrock.mainPage.controller;

import capsrock.mainPage.dto.request.MainPageRequest;
import capsrock.mainPage.service.MainPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainPageController {

    private final MainPageService mainPageService;

    @GetMapping
    public ResponseEntity<?> mainPage(MainPageRequest mainPageRequest){
        return new ResponseEntity<>(mainPageService.getMainPage(mainPageRequest), HttpStatus.OK);
    }
}
