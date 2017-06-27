package org.fenixedu.applicationtracking.domain;

class SlotSelection extends SlotSelection_Base implements Comparable<SlotSelection> {
    SlotSelection(PurposeFulfilment purposeFulfilment, Slot slot, int order) {
        super();
        setPurposeFulfilment(purposeFulfilment);
        setSlot(slot);
        setOrder(order);
    }

    public void delete() {
        setPurposeFulfilment(null);
        setSlot(null);
        deleteDomainObject();
    }

    @Override
    public int compareTo(SlotSelection o) {
        return Integer.compare(getOrder(), o.getOrder());
    }

}
