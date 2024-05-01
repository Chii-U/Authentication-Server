package com.example.authenticationserver.JWT;

import com.example.authenticationserver.JWT.JwtTokenProvider;
import com.example.authenticationserver.global.BaseException;
import com.example.authenticationserver.global.BaseResponse;
import com.example.authenticationserver.global.BaseResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.ErrorResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.EmptyStackException;

import static com.example.authenticationserver.global.BaseResponseStatus.LOGIN_EXPIRED;
import static com.example.authenticationserver.global.BaseResponseStatus.USER_NOT_EXISTS;

//필터 직접 구현
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        boolean isLogout = request.getRequestURI().equals("/user/logout");
        String token = parseAccessToken(request);
        //만료된건지 나눌거임
        try {
            //어 근데 너 로그아웃할거야? 그럼 처리해줄게... 만약 만료되거나 이상하거나 에러가 생기면 밑에서 받아서 처리
            if (isLogout) {
                String RT = parseRefreshToken(request);
                //검증작업 부터 진행.
                if(RT != null && jwtTokenProvider.validateRefreshToken(RT,token)) {
                    doLogout(response, RT);
                } else{
                    System.out.println("리프레시 토큰의 정보를 찾을 수 없음.");
                    throw new BaseException(LOGIN_EXPIRED);
                }
            }
            //토큰 검증하는거 구현 필요, 토큰이 없는지도 확인해서 없는거랑 이상한거랑 같은걸로 처리
            if(token != null && jwtTokenProvider.validateAccessToken(token)) {
                // 인증 정보를 토큰에서 빼주는 메서드 구현 필요
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                authentication.getAuthorities().forEach(GrantedAuthority::getAuthority);
                filterChain.doFilter(request,response);
            } else {
                filterChain.doFilter(request,response);
            }
        } catch (ExpiredJwtException ee) {
            try {
                if (isLogout) {
                } else {
                    // 일단 만료된거 가져왔고, 리프레시토큰은 있냐?? 니 액세스토큰이랑 같은사람꺼 아니면 안들여보내줌
                    String refreshToken = parseRefreshToken(request);
                    if (jwtTokenProvider.validateRefreshToken(refreshToken, token)) {
                        String newAT = jwtTokenProvider.reGenerateAccessToken(token); //있던 인증정보는 맞으니까 그냥 그거가지고 시간 연장이나 해줄게.
                        response.setHeader("Authorization", "Bearer " + newAT);
                        response.addCookie(new Cookie("REFRESH_TOKEN", jwtTokenProvider.reGenerateRefreshToken(refreshToken, newAT))); //대체저장용 메서드, RTR방식
                    }
                }
            } catch (BaseException e) {
                setErrorResponse(response, LOGIN_EXPIRED);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
            setErrorResponse(response, USER_NOT_EXISTS);
        }
    }

    private String parseRefreshToken(HttpServletRequest request) throws BaseException {
        if(request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("REFRESH_TOKEN") && cookie.getPath().equals("/"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElseThrow(()->new BaseException(LOGIN_EXPIRED));
        }
        return null;

    }

    //만약 할게 없으면 null 리턴할거임.(안되나..? 그럼 옵셔널 하고)
    private String parseAccessToken(HttpServletRequest request) {
        String AT = request.getHeader("Authorization");
        if(StringUtils.hasText(AT) && AT.startsWith("Bearer")) {
            return AT.substring(7);
        }
        return null;
    }

    private boolean doLogout(HttpServletResponse response,String refreshToken) {
        response.setHeader("Authorization","Bearer ");
        //리프레시토큰도 있는거 뺏을거야.
        response.addCookie(new Cookie("REFRESH_TOKEN","") {{
            setPath("/");
        }});
        // 리프레시토큰을 저장소에서 지우는 메서드 필요
        return jwtTokenProvider.deleteRefreshToken(refreshToken);

    }

    public static void setErrorResponse(HttpServletResponse response, BaseResponseStatus responseStatus) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        BaseResponse<ErrorResponse> error = new BaseResponse<>(responseStatus);
        String s = objectMapper.writeValueAsString(error);

        /**
         * 한글 출력을 위해 getWriter() 사용
         */
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(s);
    }
}