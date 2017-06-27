package org.fenixedu.applicationtracking.domain;

import java.util.Optional;
import java.util.Set;

import org.fenixedu.commons.i18n.LocalizedString;

public class Requirement extends Requirement_Base {

    public Requirement() {
        super();
        setRoot(ApplicationTracking.getInstance());
    }

    public Requirement(LocalizedString name, LocalizedString description) {
        this();
        setName(name);
        setDescription(description);
    }

    public Set<RequirementConfiguration> getRequirementConfigurations() {
        return super.getRequirementConfigurationSet();
    }

    public Optional<Form> getForm() {
        return Optional.ofNullable(getFormSpec());
    }

    public void setForm(Form form) {
        setFormSpec(form);
    }

    public void handleStateTransition(Application application, StateType previousState, RequirementFulfilment fulfilment) {
        // Nothing done here, subclasses may override this
    }

    public void delete() {
        ApplicationTrackingDomainException.throwWhenDeleteBlocked(getDeletionBlockers());
        setRoot(null);
        for (RequirementConfiguration requirementConfiguration : getRequirementConfigurationSet()) {
            requirementConfiguration.delete();
        }
        deleteDomainObject();
    }

}