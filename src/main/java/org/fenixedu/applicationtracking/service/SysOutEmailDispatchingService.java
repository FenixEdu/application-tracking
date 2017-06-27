package org.fenixedu.applicationtracking.service;

import org.springframework.stereotype.Service;

@Service
public class SysOutEmailDispatchingService implements  EmailDispatchingService{
    @Override public void send(String email, String subject, String plainBody, String body) {
        System.out.println("Sending email to: " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Plain Body: " + plainBody);
        System.out.println("Body: " + body);
    }
}
