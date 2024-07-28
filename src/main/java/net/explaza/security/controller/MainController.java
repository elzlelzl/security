package net.explaza.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/main")
    public String home() {
        return "base/main";
    }

    @GetMapping("/login")
    public String login() {
        return "base/login";
    }

    @GetMapping("/denied")
    public String denied() {
        return "base/denied";
    }

    @GetMapping("/auth/login")
    public String login(@RequestParam(value = "error", required = false)String error,
                        @RequestParam(value = "exception", required = false)String exception,
                        Model model) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "base/login";
    }

    @GetMapping("/check-session")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkSession(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        if (authentication != null && authentication.isAuthenticated() && !isAnonymousUser(authentication)) {
            // 인증된 사용자인 경우
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
//                memberService.checkSessionExpiration(request.getSession(false));
                // UserDetails 인터페이스를 구현한 경우
                response.put("authenticated", true);
                response.put("message", "Authenticated user");
                // 추가적인 작업 수행
            } else {
                // 인증은 되었지만 UserDetails를 사용하지 않는 경우
                response.put("authenticated", true);
                response.put("message", "Authenticated user (custom authentication)");
            }
        } else {
            // 인증되지 않은 사용자인 경우
            response.put("authenticated", false);
            response.put("message", "Unauthenticated user");
        }

        return ResponseEntity.ok(response);
    }

    private boolean isAnonymousUser(Authentication authentication) {
        return authentication.getPrincipal() instanceof String &&
                authentication.getPrincipal().equals("anonymousUser");
    }
}