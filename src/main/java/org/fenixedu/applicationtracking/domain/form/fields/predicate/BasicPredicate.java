package org.fenixedu.applicationtracking.domain.form.fields.predicate;

import java.util.Optional;

import org.fenixedu.applicationtracking.domain.FormAnswer;
import org.fenixedu.applicationtracking.domain.form.FormAnswerField;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BasicPredicate extends Predicate {

    private final boolean predicateValue;

    public BasicPredicate(boolean value) {
        this.predicateValue = value;
    }

    @Override
    public JsonElement json() {
        return new JsonPrimitive(this.predicateValue);
    }

    @Override
    public boolean evaluate(Optional<FormAnswerField> formAnswerField, FormAnswer formAnswer) {
        return !this.predicateValue || (formAnswerField.isPresent() && !formAnswerField.get().getValue().isJsonNull());
    }
}
