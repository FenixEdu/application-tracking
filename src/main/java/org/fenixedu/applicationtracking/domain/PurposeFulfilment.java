package org.fenixedu.applicationtracking.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PurposeFulfilment extends PurposeFulfilment_Base {
    PurposeFulfilment(Application application) {
        super();
        setPurposeConfiguration(application.getPeriod().getPurposeConfiguration());
        setApplication(application);
    }

    @Override
    public PurposeConfiguration getPurposeConfiguration() {
        return super.getPurposeConfiguration();
    }

    public List<Slot> getSlotSelection() {
        return getSelectedSlotSet().stream().sorted().map(SlotSelection::getSlot).collect(Collectors.toList());
    }

    public void setSlotSelection(List<Slot> slots) {
        getSelectedSlotSet().stream().forEach(SlotSelection::delete);
        for (int i = 0; i < slots.size(); i++) {
            addSelectedSlot(new SlotSelection(this, slots.get(i), i));
        }
    }

    public Optional<Form> getForm() {
        return getPurposeConfiguration().getForm();
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

    public boolean isPaid() {
        // TODO Check missing logic
        return getPaidFee() != null;
    }

    void calculatePlacement() {
        getSelectedSlotSet().stream().sorted().map(SlotSelection::getSlot).filter(Slot::hasVacancy).findFirst()
                .ifPresent(s -> getApplication().setPlacement(s));
    }

    public void delete() {
        setApplication(null);
        setPurposeConfiguration(null);
        setSlotSelection(new ArrayList<Slot>());
        deleteDomainObject();
    }
}
