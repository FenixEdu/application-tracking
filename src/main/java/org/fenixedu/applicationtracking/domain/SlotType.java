package org.fenixedu.applicationtracking.domain;

import org.fenixedu.commons.i18n.LocalizedString;

public class SlotType extends SlotType_Base {

    public SlotType(LocalizedString name, String code) {
        this(name,code,null);
    }
    
    public SlotType(LocalizedString name, String code, SlotType parent) {
        super();
        setName(name);
        setCode(code);
        setParent(parent);
    }

    public void delete(){
        for(Slot slot : getSlotsSet()){
            slot.delete();
        }
        setParent(null);
        setPurposeConfiguration(null);
        if(getChild() != null){
            getChild().delete();
        }
        deleteDomainObject();
    }
    
    
    public SlotType getParent() {
        return super.getParent();
    }
    public SlotType getChild() {
        return super.getChild();
    }
    
}
