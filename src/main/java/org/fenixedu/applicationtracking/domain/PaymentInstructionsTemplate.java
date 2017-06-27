package org.fenixedu.applicationtracking.domain;

public class PaymentInstructionsTemplate extends PaymentInstructionsTemplate_Base {
    public PaymentInstructionsTemplate() {
        super();
    }

    public static PaymentInstructionsTemplate getInstance() {
        return ApplicationTracking.getInstance().getPaymentInstructionsTemplate();
    }
}
