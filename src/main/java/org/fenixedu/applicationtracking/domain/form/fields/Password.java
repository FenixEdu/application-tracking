package org.fenixedu.applicationtracking.domain.form.fields;

import java.util.Optional;

import org.fenixedu.applicationtracking.domain.FieldValidator;
import org.fenixedu.applicationtracking.domain.form.FieldType;
import org.fenixedu.applicationtracking.domain.form.fields.predicate.Predicate;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Password extends FormField {

    public Password(String key, Optional<Predicate> requiredPredicate, Optional<Predicate> includeIfPredicate,
            LocalizedString label, Optional<FieldValidator> fieldValidator) {
        super(key, requiredPredicate, includeIfPredicate, label, fieldValidator);
    }

    public static Password fromJson(JsonElement jsonElement) {
        JsonObject json = jsonElement.getAsJsonObject();
        return new Password(keyFromJson(json), requiredPredicateFromJson(json), includeIfPredicateFromJson(json),
                labelFromJson(json), fieldValidatorFromJson(json));
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.PASSWORD;
    }

}
