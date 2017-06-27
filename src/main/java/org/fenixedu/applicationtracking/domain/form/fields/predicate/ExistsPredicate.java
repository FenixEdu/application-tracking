package org.fenixedu.applicationtracking.domain.form.fields.predicate;

import java.util.Optional;

import org.fenixedu.applicationtracking.domain.FormAnswer;
import org.fenixedu.applicationtracking.domain.form.FormAnswerField;

import com.google.gson.JsonObject;

public class ExistsPredicate extends ComplexPredicate {

    protected static final String PREDICATE_KEY = "exists";

    public ExistsPredicate(String key) {
        super(key);
    }

    @Override
    public boolean evaluate(Optional<FormAnswerField> formAnswerField, FormAnswer formAnswer) {
        return formAnswer.getFormAnswerFieldFromKey(getKey())
                .map(referencedFormAnswerField -> !referencedFormAnswerField.getValue().isJsonNull()).orElse(false);
    }

    public static Predicate fromJson(JsonObject json) {
        return new ExistsPredicate(getKeyValue(json));
    }

    @Override
    JsonObject getJsonPayload() {
        return new JsonObject();
    }

    @Override
    String getPredicateKey() {
        return PREDICATE_KEY;
    }

}
