package org.fenixedu.applicationtracking.domain;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class RequirementFulfilment extends RequirementFulfilment_Base implements Comparable<RequirementFulfilment> {
    RequirementFulfilment(Application application, RequirementConfiguration configuration) {
        super();
        setRequirementConfiguration(configuration);
        setApplication(application);
        setComplete(false);
    }

    @Override
    public RequirementConfiguration getRequirementConfiguration() {
        return super.getRequirementConfiguration();
    }

    public boolean isComplete() {
        return getComplete();
    }

    @Override
    public void setComplete(boolean complete) {
        super.setComplete(complete);
    }

    public Set<Recommendation> getRecommendations() {
        return super.getRecommendationSet();
    }

    public Optional<Form> getForm() {
        return getRequirementConfiguration().getForm();
    }

    public Stream<ApplicationStep> getApplicationStepStream() {
        return ApplicationStep.steps(getForm(), this::getFormAnswer, this::setFormAnswer);
    }

    public Optional<FormAnswer> getFormAnswer() {
        return Optional.ofNullable(getFormAnswerSpec());
    }

    public void setFormAnswer(FormAnswer formAnswer) {
        setFormAnswerSpec(formAnswer);
    }

    @Override
    public int compareTo(RequirementFulfilment o) {
        return getRequirementConfiguration().compareTo(o.getRequirementConfiguration());
    }

    public void delete() {
        setApplication(null);
        setRequirementConfiguration(null);
        deleteDomainObject();
    }
}
