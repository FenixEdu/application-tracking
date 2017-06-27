package org.fenixedu.applicationtracking.service;

import java.math.BigDecimal;

import org.fenixedu.applicationtracking.domain.Actor;
import org.fenixedu.applicationtracking.domain.Application;
import org.fenixedu.applicationtracking.domain.ApplicationActivityLog;
import org.fenixedu.applicationtracking.domain.ApplicationTrackingDomainException;
import org.fenixedu.applicationtracking.domain.Comment;
import org.fenixedu.applicationtracking.domain.Period;
import org.fenixedu.applicationtracking.domain.RequirementFulfilment;
import org.fenixedu.applicationtracking.domain.StateType;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.security.Authenticate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Service
public class ApplicationService {
    private final PaymentService paymentService;

    @Autowired
    public ApplicationService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Atomic(mode = TxMode.WRITE)
    public Application create(Period period, String givenNames, String familyNames, String email) {
        if (period.isOpen()) {
            return new Application(period, givenNames, familyNames, email);
        } else {
            throw ApplicationTrackingDomainException.periodNotOpen(period.getSlug());
        }
    }

    @Atomic(mode = TxMode.WRITE)
    public void comment(Application application, String text, String role) {
        Comment comment = new Comment(application, text);
        ApplicationActivityLog.logComment(comment, Authenticate.getUser(), role);
    }

    @Atomic(mode = TxMode.WRITE)
    public void submitByApplicant(Application application) {
        changeStateByApplicant(application, StateType.SUBMITTED);
    }

    @Atomic(mode = TxMode.WRITE)
    public void cancelByApplicant(Application application) {
        changeStateByApplicant(application, StateType.CANCELLED);
    }

    private void changeStateByApplicant(Application application, StateType state) {
        application.changeState(state);
        paymentService.notifyStateTransition(application);
        Actor applicant = application.getApplicant();
        ApplicationActivityLog.logStateChange(application, state, applicant, applicant.getActorType());
    }

    @Atomic(mode = TxMode.WRITE)
    public void changeStateByServices(Application application, StateType state, String role) {
        application.changeState(state);
        paymentService.notifyStateTransition(application);
        User user = Authenticate.getUser();
        ApplicationActivityLog.logStateChange(application, state, user, role);
    }

    @Atomic(mode = TxMode.WRITE)
    public void grade(Application application, RequirementFulfilment fulfilment, BigDecimal grade, String role) {
        fulfilment.setGrade(grade);
        fulfilment.setComplete(grade != null);
        User user = Authenticate.getUser();
        if (grade != null) {
            ApplicationActivityLog
                    .log(application, BundleUtil.getLocalizedString("resources.ApplicationTrackingResources",
                            "log.gradedFulfilment", fulfilment.getRequirementConfiguration().getRequirement().getName()
                                    .getContent(), grade.toString()), user, role);
        } else {
            ApplicationActivityLog.log(application, BundleUtil.getLocalizedString("resources.ApplicationTrackingResources",
                    "log.removedGradeOnFulfilment", fulfilment.getRequirementConfiguration().getRequirement().getName()
                            .getContent()), user, role);
        }
    }
}
