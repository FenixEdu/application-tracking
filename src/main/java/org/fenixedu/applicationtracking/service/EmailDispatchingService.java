package org.fenixedu.applicationtracking.service;

import org.springframework.stereotype.Service;

@Service
public interface EmailDispatchingService {
    void send(String email, String subject, String body, String htmlBody);
}
