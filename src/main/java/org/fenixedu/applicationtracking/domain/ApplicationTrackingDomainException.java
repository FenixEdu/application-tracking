package org.fenixedu.applicationtracking.domain;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.fenixedu.bennu.core.domain.exceptions.DomainException;

public class ApplicationTrackingDomainException extends DomainException {
    private static final String BUNDLE = "resources.ApplicationTrackingResources";

    private static final long serialVersionUID = -3698689418596535561L;

    private ApplicationTrackingDomainException(Status status, String key, String... args) {
        super(status, BUNDLE, key, args);
    }

    public ApplicationTrackingDomainException(String key, String... args) {
        super(BUNDLE, key, args);
    }

    public static void throwWhenDeleteBlocked(Collection<String> blockers) {
        if (!blockers.isEmpty()) {
            throw new ApplicationTrackingDomainException("key.return.argument", blockers.stream().collect(
                    Collectors.joining(", ")));
        }
    }
    
    public static DomainException editionNotAllowed(String slug) {
        return new ApplicationTrackingDomainException(Status.FORBIDDEN, "error.edition.not.allowed.slug", slug);
    }

    public static DomainException duplicatedSlug(String slug) {
        return new ApplicationTrackingDomainException(Status.PRECONDITION_FAILED, "error.duplicated.slug", slug);
    }

    public static DomainException periodNotFound(String slug) {
        return new ApplicationTrackingDomainException(Status.NOT_FOUND, "error.period.not.found", slug);
    }

    public static DomainException periodNotOpen(String slug) {
        return new ApplicationTrackingDomainException(Status.PRECONDITION_FAILED, "error.period.not.open", slug);
    }

    public static DomainException applicationNotFound(String slug) {
        return new ApplicationTrackingDomainException(Status.NOT_FOUND, "error.application.not.found", slug);
    }

    public static DomainException duplicateEmail(String email) {
        return new ApplicationTrackingDomainException(Status.PRECONDITION_FAILED, "error.duplicated.email", email);
    }

    public static DomainException invalidStateTransition(Application application, StateType state) {
        return new ApplicationTrackingDomainException(Status.PRECONDITION_FAILED, "error.application.invalidStateChange",
                application.getNumber(), application.getState().getLocalizedName().getContent(), state.getLocalizedName()
                        .getContent());
    }

    public static DomainException missingTemplate(String template) {
        return new ApplicationTrackingDomainException(Status.PRECONDITION_FAILED, "error.missing.template", template);
    }

    public static DomainException paymentInstructionsTemplateNotDefined() {
        return new ApplicationTrackingDomainException(Status.PRECONDITION_FAILED, "error.missing.template.paymentInstructions");
    }
}
