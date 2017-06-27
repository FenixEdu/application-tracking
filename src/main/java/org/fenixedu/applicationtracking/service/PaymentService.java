package org.fenixedu.applicationtracking.service;

import org.fenixedu.applicationtracking.domain.Application;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface PaymentService {
    void notifyStateTransition(Application application);

    Map<String, Object> getPaymentDetails(Application application);
}
