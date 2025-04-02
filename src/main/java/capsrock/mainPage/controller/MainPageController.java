package capsrock.mainPage.controller;

import capsrock.mainPage.dto.request.MainPageRequest;
import capsrock.mainPage.service.MainPageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MainPageController {

    private final MainPageService mainPageService;

    public MainPageController(MainPageService mainPageService) {
        this.mainPageService = mainPageService;
    }

    @GetMapping
    public ResponseEntity<?> mainPage(MainPageRequest mainPageRequest){
        return new ResponseEntity<>(mainPageService.getMainPage(mainPageRequest), HttpStatus.OK);
    }
}
