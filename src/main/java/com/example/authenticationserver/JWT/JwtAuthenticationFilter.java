package com.example.authenticationserver.JWT;

import com.example.authenticationserver.JWT.JwtTokenProvider;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.EmptyStackException;

//필터 직접 구현
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain){
        boolean isLogout = request.getRequestURI().equals("/user/logout");
        String token = parseAccessToken(request);
        //만료된건지 나눌거임
        try {
            //어 근데 너 로그아웃할거야? 그럼 처리해줄게... 만약 만료되거나 이상하거나 에러가 생기면 밑에서 받아서 처리
            if (isLogout) {
                doLogout(response,parseRefreshToken(request));
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
            if(isLogout) {}
            else {
                // 일단 만료된거 가져왔고, 리프레시토큰은 있냐?? 니 액세스토큰이랑 같은사람꺼 아니면 안들여보내줌
                String refreshToken = parseRefreshToken(request);
                if(jwtTokenProvider.validateRefreshToken(refreshToken,token)) {
                    String newAT = jwtTokenProvider.reGenerateAccessToken(token); //있던 인증정보는 맞으니까 그냥 그거가지고 시간 연장이나 해줄게.
                    response.setHeader("Authorization", "Bearer " + newAT);
                    response.addCookie(new Cookie("REFRESH_TOKEN",jwtTokenProvider.reGenerateRefreshToken(token))); //대체저장용 메서드, RTR방식
                }
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String parseRefreshToken(HttpServletRequest request) {
        if(request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("REFRESH_TOKEN") && cookie.getPath().equals("/"))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElseThrow(IllegalStateException::new);
        }

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
}