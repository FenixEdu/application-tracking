package org.fenixedu.applicationtracking.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.*;
import org.fenixedu.applicationtracking.domain.Application;
import org.fenixedu.applicationtracking.domain.PurposeConfiguration;
import org.fenixedu.applicationtracking.domain.Slot;
import org.fenixedu.commons.stream.StreamUtils;
import org.springframework.stereotype.Service;

@Service
public class SlotService {
    private JsonElement getSlot(Slot slot) {
        JsonObject slotElement = new JsonObject();

        slotElement.addProperty("code", slot.getCode());
        slotElement.add("name", slot.getName().json());
        slotElement.add("typeName", slot.getType().getName().json());

        Set<Slot> childrenSet = slot.getSlotSet();

        if (childrenSet.size() != 0) {
            JsonObject meta = new JsonObject();

            JsonObject type = new JsonObject();
            type.add("name", childrenSet.stream().map(x -> x.getType().getName().json()).findFirst().orElseGet(() -> JsonNull.INSTANCE));
            type.addProperty("code", childrenSet.stream().map(x -> x.getType().getCode()).findFirst().orElseGet(() -> null));
            meta.add("type", type);

            JsonObject children = new JsonObject();
            childrenSet.stream().forEach(x -> children.add(x.getCode(), getSlot(x)));

            meta.add("slots", children);

            slotElement.add("children", meta);
        }

        return slotElement;
    }

    public JsonElement getSlotExternalRepresentation(Application application) {
        JsonObject meta = new JsonObject();

        JsonObject type = new JsonObject();
        type.add("name", application.getPeriod().getPurposeConfiguration().getSlotSet().stream().map(x -> x.getType().getName().json()).findFirst().orElseGet(() -> JsonNull.INSTANCE));
        type.addProperty("code", application.getPeriod().getPurposeConfiguration().getSlotSet().stream().map(x -> x.getType().getCode()).findFirst().orElseGet(() -> null));
        meta.add("type", type);

        JsonObject slots = new JsonObject();

        application.getPeriod().getPurposeConfiguration().getSlotSet().stream().forEach(x -> slots.add(x.getCode(), getSlot(x)));

        meta.add("slots", slots);

        return meta;
    }

    public JsonElement getSlotSelectionExternalRepresentation(Application application) {
        JsonArray selected =
                application.getPurposeFulfilment().getSlotSelection().stream().map(Slot::getPath).map(x -> x.stream().map(y -> new JsonPrimitive(y.getCode())).collect(StreamUtils.toJsonArray())).collect(StreamUtils.toJsonArray());
        int total = application.getPeriod().getPurposeConfiguration().getMaxSlotsPerApplication() - selected.size();

        for(int i =0; i<total; i++){
            selected.add(new JsonArray());
        }

        return selected;
    }

    public List<Slot> fromSlotFromExternalRepresentation(JsonElement fromUser, Application application) {
        List<Slot> choices = new ArrayList<>();

        PurposeConfiguration purposeConfiguration = application.getPurposeFulfilment().getPurposeConfiguration();

        JsonArray slots = fromUser.getAsJsonArray();
        int maxSlots = purposeConfiguration.getMaxSlotsPerApplication();
        if (maxSlots == 0) {
            maxSlots = purposeConfiguration.getSlotSet().size();
        }
        for (int i = 0; i < maxSlots; i++) {
            if (slots.size() <= i) {
                continue;
            }

            JsonElement element = slots.get(i);

            if (element == null || element.equals(JsonNull.INSTANCE)) {
                continue;
            }

            JsonArray value = element.getAsJsonArray();
            List<String> path = new ArrayList<>();
            for (int i1 = 0; i1 < value.size(); i1++) {
                path.add(value.get(i1).getAsString());
            }

            Slot s = purposeConfiguration.getSlotFromPath(path);

            if (s == null || choices.contains(s)) {
                continue;
            }

            choices.add(s);
        }

        return choices;
    }
}
