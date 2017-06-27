package org.fenixedu.applicationtracking.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public interface CaptchaService {

    public String generateCaptcha(HttpServletRequest request);

    public boolean isValid(HttpServletRequest request);

}
