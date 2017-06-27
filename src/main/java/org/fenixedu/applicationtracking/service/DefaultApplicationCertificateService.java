package org.fenixedu.applicationtracking.service;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import org.fenixedu.applicationtracking.domain.Application;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

@Service
public class DefaultApplicationCertificateService implements ApplicationCertificateService {
    @Override public void download(Application application, Locale locale, HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append("Application Certificate\n\n");

        builder.append("This document certifies that the candidate ");
        builder.append(application.getGivenNames() + " " + application.getFamilyNames());
        builder.append(" applied to ");
        builder.append(application.getPurposeFulfilment().getPurposeConfiguration().getPeriod().getName().getContent());

        response.setContentType("text/plain;charset=utf-8");
        response.setHeader("Content-disposition", "attachment; filename=certificate-" + application.getNumber() + ".txt");
        try (OutputStream out = response.getOutputStream()) {
            ByteArrayInputStream bytes = new ByteArrayInputStream(builder.toString().getBytes(Charsets.UTF_8));
            ByteStreams.copy(bytes, out);
        } catch (IOException e) {
        }
    }
}
