package org.fenixedu.applicationtracking.domain;

import com.google.common.collect.Lists;
import org.fenixedu.commons.i18n.LocalizedString;

import java.util.*;

public class Slot extends Slot_Base implements HasSlots{
 
    public Slot(SlotType type, LocalizedString name, String code, HasSlots parent) {
        this(type, name, code, -1, parent);
    }

    public Slot(SlotType type, LocalizedString name, String code, int vacancies, HasSlots parent) {
        super();
        parent.addSlot(this);
        setName(name);
        setCode(code);
        setVacancies(vacancies);
        
        if (type != null){
            setType(type);
        }else{
            throw new RuntimeException("Slot Type is required");
        }
        
        verifyTypeHiearchy();
    }

    private void verifyTypeHiearchy() {
        if(getParent() == null){
            if (getType().getParent() != null){
                throw new RuntimeException(String.format("Slot Type Hiearchy Violated: expected %s, got %s.", getType().getParent().getCode(), "None"));
            }
        }else{
            if(getType().getParent() == null){
                throw new RuntimeException(String.format("Slot Type Hiearchy Violated: expected %s, got %s.", "None", getParent().getType().getCode()));
            }else if (!(getType().getParent().getCode().equals(getParent().getType().getCode()))){
                throw new RuntimeException(String.format("Slot Type Hiearchy Violated: expected %s, got %s.", getType().getParent().getCode(), getParent().getType().getCode()));
            }
            
        }
    }

    public void setUnlimitedVacancies() {
        setVacancies(-1);
    }

    public boolean hasUnlimitedVacancies() {
        return getVacancies() < 0;
    }

    public boolean hasVacancy() {
        return hasUnlimitedVacancies() || getVacancies() >= getPlacedApplicantSet().size();
    }

    public void delete() {
        setPurposeConfiguration(null);
        setType(null);
        setParent(null);
        if(getSlotSet().size() > 0){
            for(Slot slot : getSlotSet()){
                slot.delete();
            }
        }
        deleteDomainObject();
    }
    
    public SlotType getType(){
        return super.getType();
    }

    public List<Slot> getPath(){
        ArrayList<Slot> result = new ArrayList<>();
        Slot current = this;
        do{
            result.add(current);
            current = current.getParent();
        }while(current != null);
        return Lists.reverse(result);
    }

    public Slot deepClone() {
        Slot clone = new Slot(getType(), getName(), getCode(), getVacancies(),
                getParent() != null ? getParent() : getPurposeConfiguration());
        for (Slot child : getSlotSet()) {
            clone.addSlot(child.deepClone());
        }
        return clone;
    }

}
