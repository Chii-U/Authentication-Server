package com.example.authenticationserver.service;

import com.example.authenticationserver.entity.Enable;
import com.example.authenticationserver.global.BaseException;
import com.example.authenticationserver.repository.EnableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Random;

import static com.example.authenticationserver.global.BaseResponseStatus.CODE_NOT_MATCH;
import static com.example.authenticationserver.global.BaseResponseStatus.UNABLE_TO_SEND_EMAIL;

@Slf4j@Service@RequiredArgsConstructor
public class MailService {
    @Autowired
    private EnableRepository enableRepository;
    @Autowired
    private UserService userService;

    private final JavaMailSender javaMailSender;
    @Autowired
    private TemplateEngine templateEngine;

    public boolean verifyInputCode(String code) throws BaseException {
        Enable enable = enableRepository.findByAuthNumber(code).orElseThrow(()-> new BaseException(CODE_NOT_MATCH));
        String userEmail = enable.getEmail();

        enableRepository.delete(enable);
        userService.setEnable(userEmail,true);
        return true;
    }

    public boolean genVerifyCodeNSendMail(String email) throws BaseException {
        String code = generateVerifyCode();
        if(sendVerifyMail(email,code)) {
            enableRepository.save(Enable.builder()
                    .authNumber(code)
                    .ttl(300) // 5분
                    .email(email)
                    .build());
            return true;
        }
        return false;
    }

    private String generateVerifyCode() {
        Random random = new Random();
        int codeInt = random.nextInt(999999);
        return String.format("%06d",codeInt);
    }

    private boolean sendVerifyMail(String email, String code) throws BaseException {
        Context context = new Context();
        context.setVariable("authentication_code", code);

        return sendEMail(email, "[통증 관리 서비스 'PainT'] 계정 활성화 안내",context,"mail-verify.html");
    }
    private boolean sendEMail(String to, String sub, Context context, String template) throws BaseException {
        try {
            MimeMessagePreparator preparatory = mimeMessage -> {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.setTo(to); // 메일 수신자
                mimeMessageHelper.setSubject(sub); // 메일 제목
                String content = templateEngine.process(template, context);
                mimeMessageHelper.setText(content, true); // 메일 본문 내용, HTML 여부
            };
            javaMailSender.send(preparatory);

        } catch (Exception e) {
            throw new BaseException(UNABLE_TO_SEND_EMAIL);
        }

        return true;
    }
}
