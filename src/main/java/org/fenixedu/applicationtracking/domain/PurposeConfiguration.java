package org.fenixedu.applicationtracking.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.bennu.core.i18n.BundleUtil;

public final class PurposeConfiguration extends PurposeConfiguration_Base implements HasSlots {

    PurposeConfiguration(Period period, Purpose purpose) {
        super();
        setPeriod(period);
        setPurpose(purpose);
        setForm(purpose.getForm().orElse(null));
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
        if (!getPurposeFulfilmentSet().isEmpty()) {
            blockers.add(BundleUtil.getString("resources.ApplicationTrackingResources",
                    "error.cannotDeletePurposeConfigurationWithFulfilments"));
        }
    }

    public void delete() {
        ApplicationTrackingDomainException.throwWhenDeleteBlocked(getDeletionBlockers());
        setPeriod(null);
        setPurpose(null);
        setApplicationFee(null);
        setSlotTypeRoot(null);
        for (Slot slot : getSlotSet()) {
            slot.delete();
        }
        deleteDomainObject();
    }

    @Override
    public void setApplicationFee(ApplicationFee applicationFee) {
        if (super.getApplicationFee() != applicationFee) {
            super.getApplicationFee().delete();
            super.setApplicationFee(applicationFee);
        }
    }

    @Override
    public Period getPeriod() {
        return super.getPeriod();
    }

    @Override
    public Purpose getPurpose() {
        return super.getPurpose();
    }

    public Optional<Form> getForm() {
        return Optional.ofNullable(getFormSpec());
    }

    public void setForm(Form form) {
        setFormSpec(form);
    }

    public void handleStateTransition(Application application, StateType previousState) {
        getPurpose().handleStateTransition(application, previousState);
    }

    public Slot getSlotFromPath(List<String> codes){
        if (codes.size() == 0){
            return null;
        }

        Stream<Slot> slots = getSlotSet().stream();
        String code;
        do{
            code = codes.remove(0);
            final String finalCode = code;
            Optional<Slot> curr = slots.filter(x -> x.getCode().equals(finalCode)).findAny();
            if (curr.isPresent()){
                if(codes.size() == 0){
                    return curr.get();
                }else{
                    slots = curr.get().getSlotSet().stream();
                }
            }else{
                return null;
            }
        }while (codes.size() > 0);

        return null;
    }

    public String placeApplicant(Application application) {
        return getPurpose().placeApplicant(application);
    }

    private static Set<Slot> flat(Slot slot){
        HashSet<Slot> set = new HashSet<>();
        set.add(slot);
        set.addAll(slot.getSlotSet().stream().map(PurposeConfiguration::flat).reduce((x,y) -> {x.addAll(y); return x;}).orElse(new HashSet<>()));
        return set;
    }

    public Set<Slot> getAllSlots(){
        return getSlotSet().stream().map(PurposeConfiguration::flat).reduce((x,y)-> {x.addAll(y); return x;}).orElse(new HashSet<>());
    }

    @Override
    public SlotType getSlotTypeRoot(){
        return super.getSlotTypeRoot();
    }

}
