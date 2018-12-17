package me.exrates.controller.ngcontroller;

import me.exrates.model.User;
import me.exrates.model.dto.Generic2faResponseDto;
import me.exrates.service.G2faService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(value = "/info/private/v2/2FaOptions/",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class NgTwoFaController {

    private final UserService userService;
    private final G2faService g2faService;

    @Autowired
    public NgTwoFaController(UserService userService,
                             G2faService g2faService) {
        this.userService = userService;
        this.g2faService = g2faService;
    }

    @GetMapping("/google2fa/hash")
    public Generic2faResponseDto getSecurityCode() {
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        return g2faService.getGoogleAuthenticatorCodeNg(userId);
    }

    @GetMapping("/google2fa/pin")
    public ResponseEntity getSecurityPinCode(HttpServletRequest request) {
        User user = userService.findByEmail(getPrincipalEmail());
        g2faService.sendGoogleAuthPinConfirm(user, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/google2fa/submit")
    public ResponseEntity<Void> submitGoogleSecret(@RequestBody Map<String, String> body) {
        User user = userService.findByEmail(getPrincipalEmail());
        boolean result = g2faService.submitGoogleSecret(user, body);
        if (result) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/google2fa/disable")
    public ResponseEntity<Void> disableGoogleAuthentication(@RequestBody Map<String, String> body) {
        User user = userService.findByEmail(getPrincipalEmail());
        boolean result = g2faService.disableGoogleAuth(user, body);
        if (result) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/verify_google2fa")
    public ResponseEntity<Boolean> verifyGoogleAuthenticatorCode(@RequestParam String code) {
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        if (g2faService.checkGoogle2faVerifyCode(code, userId)) {
            return ResponseEntity.ok(Boolean.TRUE);
        }
        return ResponseEntity.badRequest().build();
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
