package org.fenixedu.applicationtracking.domain;

import java.util.Optional;
import java.util.Set;

import org.fenixedu.commons.i18n.LocalizedString;

public class Purpose extends Purpose_Base {

    public Purpose() {
        super();
        setRoot(ApplicationTracking.getInstance());
    }

    public Purpose(LocalizedString name, LocalizedString description) {
        this();
        setName(name);
        setDescription(description);
    }

    public Set<PurposeConfiguration> getPurposeConfigurations() {
        return super.getPurposeConfigurationSet();
    }

    public Optional<Form> getForm() {
        return Optional.ofNullable(getFormSpec());
    }

    public void setForm(Form form) {
        setFormSpec(form);
    }

    public void handleStateTransition(Application application, StateType previousState) {
        // Nothing to do here
    }

    public String placeApplicant(Application application) {
        // Nothing to do here
        return null;
    }

    public void delete() {
        ApplicationTrackingDomainException.throwWhenDeleteBlocked(getDeletionBlockers());
        setRoot(null);
        for (PurposeConfiguration purposeConfiguration : getPurposeConfigurationSet()) {
            purposeConfiguration.delete();
        }
        deleteDomainObject();
    }
}
