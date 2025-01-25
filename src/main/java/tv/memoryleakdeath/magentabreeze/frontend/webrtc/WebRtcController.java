package tv.memoryleakdeath.magentabreeze.frontend.webrtc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import tv.memoryleakdeath.magentabreeze.frontend.BaseFrontendController;

@Controller
@RequestMapping(value = "/webrtc", consumes = "application/json", produces = "application/json")
public class WebRtcController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(WebRtcController.class);

    @PostMapping("/live")
    public ResponseEntity<String> acceptStream(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestBody String request) {
        logger.info("AUTH: {} LIVE REQUEST: {}", auth, request);
        return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
