package org.fenixedu.applicationtracking.domain.form.fields.predicate;

import java.util.Optional;

import org.fenixedu.applicationtracking.domain.FormAnswer;
import org.fenixedu.applicationtracking.domain.form.FormAnswerField;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class EqualsPredicate extends ComplexPredicate {

    protected static final String PREDICATE_KEY = "equals";
    protected static final String VALUE_KEY = "value";

    private final JsonElement value;

    public EqualsPredicate(String key, JsonElement value) {
        super(key);
        this.value = value;
    }

    public JsonElement getValue() {
        return value;
    }

    @Override
    public boolean evaluate(Optional<FormAnswerField> formAnswerField, FormAnswer formAnswer) {
        Optional<FormAnswerField> referencedFormAnswerField = formAnswer.getFormAnswerFieldFromKey(getKey());
        if (formAnswerField.isPresent() && referencedFormAnswerField.isPresent()) {
            return formAnswerField.get().getValue().equals(referencedFormAnswerField.get().getValue());
        } else {
            return false;
        }
    }

    @Override
    JsonObject getJsonPayload() {
        JsonObject payload = new JsonObject();
        payload.add(VALUE_KEY, value);
        return payload;
    }

    public static Predicate fromJson(JsonObject json) {
        return new EqualsPredicate(getKeyValue(json), json.get(VALUE_KEY));
    }

    @Override
    String getPredicateKey() {
        return PREDICATE_KEY;
    }
}
