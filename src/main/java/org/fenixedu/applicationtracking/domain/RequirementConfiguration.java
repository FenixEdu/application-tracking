package org.fenixedu.applicationtracking.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;

import org.fenixedu.bennu.core.i18n.BundleUtil;

public final class RequirementConfiguration extends RequirementConfiguration_Base implements Comparable<RequirementConfiguration> {
    public RequirementConfiguration(Period period, Requirement requirement, BigDecimal weigth) {
        super();
        setRequirement(requirement);
        setOrder(period.getRequirementConfigurations().size());
        setPeriod(period);
        setWeigth(weigth);
        setForm(requirement.getForm().orElse(null));
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
        if (!getRequirementFulfilmentSet().isEmpty()) {
            blockers.add(BundleUtil.getString("resources.ApplicationTrackingResources",
                    "error.cannotDeleteRequirementConfigurationWithFulfilments"));
        }
    }

    public void delete() {
        ApplicationTrackingDomainException.throwWhenDeleteBlocked(getDeletionBlockers());
        Period period = getPeriod();
        setPeriod(null);
        // Reorder the remaining RequirementConfigurations
        period.reorderRequirements(period.getRequirementConfigurations());
        setRequirement(null);
        if (getRecommendationConfiguration() != null) {
            getRecommendationConfiguration().delete();
        }
        deleteDomainObject();
    }

    @Override
    public Period getPeriod() {
        return super.getPeriod();
    }

    @Override
    public Requirement getRequirement() {
        return super.getRequirement();
    }

    public Optional<Form> getForm() {
        return Optional.ofNullable(getFormSpec());
    }

    public void setForm(Form form) {
        setFormSpec(form);
    }

    @Override
    public int compareTo(RequirementConfiguration o) {
        return Integer.compare(getOrder(), o.getOrder());
    }

    public void handleStateTransition(Application application, StateType previousState, RequirementFulfilment fulfilment) {
        getRequirement().handleStateTransition(application, previousState, fulfilment);
    }

    @Override
    public int getOrder() {
        return super.getOrder();
    }
}
