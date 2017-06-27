package org.fenixedu.applicationtracking.domain.form.fields.predicate;

import java.util.Optional;

import org.fenixedu.applicationtracking.domain.FormAnswer;
import org.fenixedu.applicationtracking.domain.form.FormAnswerField;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class Predicate {

    public abstract boolean evaluate(Optional<FormAnswerField> formAnswerField, FormAnswer formAnswer);

    public abstract JsonElement json();

    public static Optional<Predicate> fromJson(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.has(ExistsPredicate.PREDICATE_KEY)) {
            return Optional.of(ExistsPredicate.fromJson(jsonObject.get(ExistsPredicate.PREDICATE_KEY).getAsJsonObject()));
        } else if (jsonObject.has(EqualsPredicate.PREDICATE_KEY)) {
            return Optional.of(EqualsPredicate.fromJson(jsonObject.get(EqualsPredicate.PREDICATE_KEY).getAsJsonObject()));
        } else {
            return Optional.empty();
        }
    }
}
