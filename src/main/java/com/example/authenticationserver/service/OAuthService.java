package com.example.authenticationserver.service;

import com.example.authenticationserver.dto.IDPWDto;
import com.example.authenticationserver.dto.JwtToken;
import com.example.authenticationserver.dto.SignUpDTO;
import com.example.authenticationserver.global.BaseException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service@Slf4j@RequiredArgsConstructor
public class OAuthService extends DefaultOAuth2UserService {
    @Autowired
    Environment env;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private final UserService userService;

    @Autowired
    private final LoginService loginService;
    
    
    /**     구글 로그인      **/
    public JwtToken googleLogin(String code, String registrationId, HttpServletResponse response) throws BaseException {
        System.out.println(code);
        String accessToken = getAccessToken(code, registrationId);
        JsonNode userResourceNode = getUserResource(accessToken, registrationId);
        System.out.println("userResourceNode = " + userResourceNode);

        String id = userResourceNode.get("sub").asText();
        String email = userResourceNode.get("email").asText();

        String name = userResourceNode.get("name").asText();
        //성별이랑 나이는 미제공이라서 안ㄴ넘어옴 -> 이건 일단 프린트 찍히는거 보고 제공으로 바꿔서 테스트 고고링
        // -> 은 성별 공개 허용해도 같이 안넘어온다.

        // 유저 정보가 이미 있다면
        UsernamePasswordAuthenticationToken authenticationToken;
        if(!userService.existsByUsername(id)){
            //유저를 새로 추가해야한다면
            userService.signup(new SignUpDTO(id, id+env.getProperty("social.signup.password.salt"), name, email, null, null, false),true);
        }

        //내 형식대로 로그인해서 반환하기
        return loginService.login(new IDPWDto(id,id+env.getProperty("social.signup.password.salt")),response);
    }


    /**     카카오 로그인     **/

    public JwtToken kakaoLogin(String code, String registrationId, HttpServletResponse response) {
        String accessToken = getAccessToken(code, registrationId);
        JsonNode userResourceNode = getUserResource(accessToken, registrationId);
        System.out.println("userResourceNode = " + userResourceNode);

        // 받을 수 있는게 이름밖에 없어서 최소 이메일, 뭔가 이 유저를 카카오 서버에서 식별할 정보 정도는 알고 있어야 나중에 요청을 하든 말든 할 것 같다.

        return new JwtToken("Bearer","","");
    }


    // 내부 공통 메서드
    private JsonNode getUserResource(String accessToken, String registrationId) {
        String resourceUri = env.getProperty("spring.security.oauth2.client.provider."+registrationId+".user-info-uri");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
    }

    private String getAccessToken(String authorizationCode, String registrationId) {

        String clientId = env.getProperty("spring.security.oauth2.client.registration."+registrationId+".client-id");
        String clientSecret = env.getProperty("spring.security.oauth2.client.registration." + registrationId + ".client-secret");
        String redirectUri = env.getProperty("spring.security.oauth2.client.registration."+registrationId+".redirect-uri");
        String tokenUri = env.getProperty("spring.security.oauth2.client.provider."+registrationId+".token-uri");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode); // google
        params.add("client_id", clientId); // 환경변수 고대로(발급받은거)
        params.add("redirect_uri", redirectUri); // 다시 재귀적으로 들어올텐데..는 아니다!!
        params.add("grant_type", "authorization_code");
        params.add("client_secret", clientSecret); // 환경변수 (발급받은거)


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity entity = new HttpEntity(params, headers);

        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class);
        JsonNode accessTokenNode = responseNode.getBody();

        return accessTokenNode.get("access_token").asText();
    }


    public JwtToken appleLogin(String code, String registrationId, HttpServletResponse response) {
        // Apple Developer 등록 이슈로 안하기로 함.
        return new JwtToken("Bearer","","");
    }
}
