package org.fenixedu.applicationtracking.service;

import org.fenixedu.applicationtracking.domain.Application;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Service
public interface ApplicationCertificateService {
    void download(Application application, Locale locale, HttpServletResponse response);
}
