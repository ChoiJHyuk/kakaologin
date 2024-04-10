package com.rosoa0475.kakaologin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosoa0475.kakaologin.DTO.UserDTO;
import com.rosoa0475.kakaologin.domain.UserEntity;
import com.rosoa0475.kakaologin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@PropertySource("classpath:application-secrets.properties")
@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserRepository userRepository;

    @Value("${kakaoApi}")
    private String kakaoApi;

    public String getAccessToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //헤더 설정
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //REQUEST시에 파라미터 붙이는 방법
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&code=").append(code);
            sb.append("&redirect_uri=https://localhost:8080/kakao/callback");
            sb.append("&client_id=" + kakaoApi);
            writer.write(sb.toString());
            writer.flush();
            writer.close();
            int responseCode = conn.getResponseCode();
            System.out.println(responseCode);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.toString());
            return root.findValue("access_token").textValue();
        } catch (Exception e) {
            return "fail";
        }
    }

    public void saveUser(String accessToken) {
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(reqURL).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.toString());
            root = root.findValue("properties");
            // 안쪽에 있는 것까지는 파싱 안해준다. 따라서 안에 있는 것을 파싱하려면 해당 속성으로 타고 들어가야 됨.
            UserEntity u = mapper.treeToValue(root, UserDTO.class).getUserEntity();
            userRepository.save(u);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
