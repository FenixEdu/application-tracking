package org.fenixedu.applicationtracking.service;

import org.fenixedu.applicationtracking.domain.*;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

@Service
public class FormService {

    public JsonElement getFormSpec(PurposeConfiguration purpose) {
        JsonElement form = purpose.getForm().map(y -> y.json()).orElse(JsonNull.INSTANCE);
        return form;
    }

    public JsonElement getFormSpec(Purpose purpose) {
        JsonElement form = purpose.getForm().map(y -> y.json()).orElse(JsonNull.INSTANCE);
        return form;
    }

    public JsonElement getFormSpec(RequirementConfiguration purpose) {
        JsonElement form = purpose.getForm().map(y -> y.json()).orElse(JsonNull.INSTANCE);
        return form;
    }

    public JsonElement getFormSpec(Requirement purpose) {
        JsonElement form = purpose.getForm().map(y -> y.json()).orElse(JsonNull.INSTANCE);
        return form;
    }

    public Form fromFormSpec(JsonElement element){
        return Form.fromJson(element);
    }

    public JsonElement getFormExternalRepresentation(ApplicationStep step) {
        JsonElement form = step.getFormStep().json();

        return form;
    }

    public JsonElement getFormAnswerExternalRepresentation(ApplicationStep step) {
        return step.getAnswer().map(y -> y.json()).orElse(JsonNull.INSTANCE);
    }

    public FormStepAnswer fromExternalRepresentation(JsonElement json) {
        return FormStepAnswer.fromJson(json);
    }
}
