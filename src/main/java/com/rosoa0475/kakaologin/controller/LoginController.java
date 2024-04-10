package com.rosoa0475.kakaologin.controller;

import com.rosoa0475.kakaologin.repository.UserRepository;
import com.rosoa0475.kakaologin.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@PropertySource("classpath:application-secrets.properties")
@Controller
@RequestMapping("/kakao")
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @Value("${kakaoApi}")
    private String kakaoApi;

    @GetMapping("/login")
    public String login() {
        return "redirect:https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + kakaoApi + "&redirect_uri=https://localhost:8080/kakao/callback";
    }

    @GetMapping("/callback")
    @ResponseBody
    public void callback(@RequestParam(name = "code") String code) {
        String token = loginService.getAccessToken(code);
        loginService.saveUser(token);
    }
}
