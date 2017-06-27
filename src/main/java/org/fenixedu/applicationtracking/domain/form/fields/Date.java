package org.fenixedu.applicationtracking.domain.form.fields;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.applicationtracking.domain.FieldValidator;
import org.fenixedu.applicationtracking.domain.form.FieldType;
import org.fenixedu.applicationtracking.domain.form.fields.predicate.Predicate;
import org.fenixedu.commons.i18n.LocalizedString;

import java.util.Optional;

/**
 * Created by nurv on 31/05/16.
 */
public class Date extends FormField {
    public Date(String key,
            Optional<Predicate> requiredPredicate,
            Optional<Predicate> includeIfPredicate,
            LocalizedString label,
            Optional<FieldValidator> fieldValidator) {
        super(key, requiredPredicate, includeIfPredicate, label, fieldValidator);
    }

    @Override
    public JsonElement json() {
        return super.json();
    }

    public static Date fromJson(JsonElement jsonElement) {
        JsonObject json = jsonElement.getAsJsonObject();
        return new Date(keyFromJson(json), requiredPredicateFromJson(json), includeIfPredicateFromJson(json),
                labelFromJson(json), fieldValidatorFromJson(json));
    }

    @Override public FieldType getFieldType() {
        return FieldType.DATE;
    }
}
