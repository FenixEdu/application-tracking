package org.fenixedu.applicationtracking.service;

import org.fenixedu.applicationtracking.domain.Application;
import org.fenixedu.applicationtracking.domain.StateType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class InMemoryPaymentService implements PaymentService {
    private Map<Application, String> debts = new HashMap<>();

    @Override public void notifyStateTransition(Application application) {
        if (application.getState().equals(StateType.SUBMITTED)) {
            debts.put(application, application.getNumber());
        }
    }

    @Override public Map<String, Object> getPaymentDetails(Application application) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", debts.getOrDefault(application, "none"));
        return params;
    }
}
