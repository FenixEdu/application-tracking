package org.fenixedu.applicationtracking.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.domain.User;

public class Application extends Application_Base {

    private static final String APPLICANT_TYPE = "applicant";

    public Application(Period period, String givenNames, String familyNames, String email) {
        super();
        setNumber(generateNumber(period));
        if (period.findApplicationByEmail(email).isPresent()) {
            throw ApplicationTrackingDomainException.duplicateEmail(email);
        }
        setPeriod(period);
        setGivenNames(givenNames);
        setFamilyNames(familyNames);
        setEmail(email);
        setState(StateType.DRAFT);
        setCreation(LocalDateTime.now());
        setApplicant(new Actor(this, givenNames.trim() + " " + familyNames.trim(), APPLICANT_TYPE));
        new PurposeFulfilment(this);
        for (RequirementConfiguration configuration : period.getRequirementConfigurationSet()) {
            new RequirementFulfilment(this, configuration);
        }
    }

    public Application(Period period, User user) {
        this(period, user.getProfile().getGivenNames(), user.getProfile().getFamilyNames(), user.getProfile().getEmail());
        if (user.getApplicationSet().stream().map(Application::getPeriod).anyMatch(p -> p.equals(period))) {
            throw ApplicationTrackingDomainException.duplicateEmail(user.getProfile().getEmail());
        }
        setUser(user);
    }

    private String generateNumber(Period period) {
        while (true) {
            String number = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            if (!period.findApplication(number).isPresent()) {
                return number;
            }
        }
    }

    @Override
    public void setGivenNames(String givenNames) {
        super.setGivenNames(givenNames);
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
    }

    @Override
    public void setFamilyNames(String familyNames) {
        super.setFamilyNames(familyNames);
    }

    @Override
    public String getGivenNames() {
        return super.getGivenNames();
    }

    @Override
    public String getFamilyNames() {
        return super.getFamilyNames();
    }

    @Override
    public String getEmail() {
        return super.getEmail();
    }

    @Override
    public String getNumber() {
        return super.getNumber();
    }

    @Override
    public StateType getState() {
        return super.getState();
    }

    public void changeState(StateType state) {
        if (getState().canTransition(state)) {
            StateType oldState = getState();
            setState(state);
            triggerStateHandlers(oldState);
        } else {
            throw ApplicationTrackingDomainException.invalidStateTransition(this, state);
        }
    }

    private void triggerStateHandlers(StateType oldState) {
        getPurposeFulfilment().getPurposeConfiguration().handleStateTransition(this, oldState);

        for (RequirementFulfilment requirementFulfilment : getRequirementFulfilmentSet()) {
            requirementFulfilment.getRequirementConfiguration().handleStateTransition(this, oldState, requirementFulfilment);
        }

        if (getState().equals(StateType.PLACED)) {
            getPurposeFulfilment().setPlacerResult(getPeriod().getPurposeConfiguration().placeApplicant(this));
        }
    }

    public boolean isActive() {
        return !getState().equals(StateType.CANCELLED);
    }

    public boolean isSubmited() {
        return getState().equals(StateType.SUBMITTED);
    }


    public List<StateType> getPossibleStates() {
        return Stream.of(StateType.values()).filter(s -> getState().canTransition(s)).sorted().collect(Collectors.toList());
    }

    public List<StateType> getPossibleForwardStates() {
        return Stream.of(StateType.values()).filter(s -> getState().canTransition(s)).filter(s -> s.compareTo(getState()) > 0)
                .sorted().collect(Collectors.toList());
    }

    public Optional<StateType> getReversionState() {
        return Stream.of(StateType.values()).filter(s -> getState().canTransition(s)).filter(s -> s.compareTo(getState()) < 0)
                .filter(s -> !s.equals(StateType.CANCELLED)).findAny();
    }

    @Override
    public LocalDateTime getCreation() {
        return super.getCreation();
    }

    @Override
    public Actor getApplicant() {
        return super.getApplicant();
    }

    @Override
    public Period getPeriod() {
        return super.getPeriod();
    }

    @Override
    public PurposeFulfilment getPurposeFulfilment() {
        return super.getPurposeFulfilment();
    }

    public RequirementFulfilment getRequirementFulfilment(RequirementConfiguration configuration) {
        return getRequirementFulfilmentSet()
                .stream()
                .filter(f -> f.getRequirementConfiguration().equals(configuration))
                .findAny()
                .orElseThrow(
                        () -> new Error("Period configuration error: requirement " + configuration.getExternalId()
                                + " not present for period " + getPeriod().getSlug()));
    }

    public List<RequirementFulfilment> getRequirementFulfilments() {
        return getRequirementFulfilmentSet().stream().sorted().collect(Collectors.toList());
    }

    public List<RequirementFulfilment> getCompleteRequirementFulfilments() {
        return getRequirementFulfilmentSet().stream().filter(RequirementFulfilment::isComplete).sorted()
                .collect(Collectors.toList());
    }

    private Stream<ApplicationStep> getApplicationStepStream() {
        return Stream.concat(getPurposeFulfilment().getApplicationStepStream(), getRequirementFulfilmentSet().stream().sorted()
                .flatMap(RequirementFulfilment::getApplicationStepStream));
    }

    public List<ApplicationStep> getApplicationSteps() {
        return getApplicationStepStream().collect(Collectors.toList());
    }

    public Optional<ApplicationStep> getApplicationStep(int index) {
        return getApplicationStepStream().skip(index).findFirst();
    }

    public BigDecimal calculateRank() {
        return getRequirementFulfilmentSet()
                .stream()
                .map(r -> (r.getGrade() != null ? r.getGrade() : BigDecimal.ZERO).multiply(r.getRequirementConfiguration()
                        .getWeigth())).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Slot getPlacement() {
        return super.getPlacement();
    }

    public List<ApplicationActivityLog> getActivityLog() {
        return getLogSet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    public List<ApplicationActivityLog> getStateActivityLog() {
        return getLogSet().stream().filter(l -> l.getState() != null).sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    public Set<Comment> getComments() {
        return super.getCommentSet();
    }

    public Optional<Actor> findActor(String secret) {
        return getActorSet().stream().filter(actor -> actor.getSecret().equals(secret)).findAny();
    }

    public Optional<ApplicationFile> findFile(String id) {
        return getFileSet().stream().filter(file -> file.getId().equals(id)).findAny();
    }

    public int clearUnconfirmedFiles() {
        int count = 0;
        for (ApplicationFile file : getFileSet()) {
            if (!file.isConfirmed()) {
                file.delete();
                count++;
            }
        }
        return count;
    }

    public void delete() {
        setPeriod(null);
        getApplicant().delete();
        setUser(null);
        setPurposeFulfilment(null);
        for (Comment comment : getCommentSet()) {
            comment.delete();
        }
        for (ApplicationFile file : getFileSet()) {
            file.delete();
        }
        for (ApplicationActivityLog log : getLogSet()) {
            log.delete();
        }
        for (RequirementFulfilment requirementFulfilment : getRequirementFulfilmentSet()) {
            requirementFulfilment.delete();
        }

        deleteDomainObject();
    }
}
