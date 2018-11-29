package me.exrates.controller.ngcontroller;

import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class LanguageController {

    private final UserService userService;

    @Autowired
    public LanguageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user-language")
    public ResponseEntity<String> getUserLanguage(Principal principal) {
        String email = principal.getName();
        Optional<String> language = Optional.ofNullable(userService.getPreferedLangByEmail(email));
        language.ifPresent(ResponseEntity::ok);
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @PatchMapping("/user-language/update")
    @ResponseBody
    public ResponseEntity updateUserLanguage(Principal principal, @RequestParam(name = "language") String language) {
        Integer userId = userService.findByEmail(principal.getName()).getId();
        boolean result = this.userService.setPreferedLang(userId, new Locale(language));
        if (result) {
            return new ResponseEntity(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
