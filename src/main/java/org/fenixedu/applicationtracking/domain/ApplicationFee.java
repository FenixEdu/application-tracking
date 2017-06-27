package org.fenixedu.applicationtracking.domain;

import javax.money.MonetaryAmount;

public class ApplicationFee extends ApplicationFee_Base {

    public ApplicationFee(PurposeConfiguration purposeConfiguration, MonetaryAmount amount, boolean perSlot) {
        super();
        setPurposeConfiguration(purposeConfiguration);
        setAmount(amount);
        setPerSlot(perSlot);
    }

    public void delete() {
        setPurposeConfiguration(null);
        deleteDomainObject();
    }
}
