package org.fenixedu.applicationtracking.domain.form.fields;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.fenixedu.applicationtracking.domain.FieldValidator;
import org.fenixedu.applicationtracking.domain.form.FieldType;
import org.fenixedu.applicationtracking.domain.form.fields.predicate.Predicate;
import org.fenixedu.commons.i18n.LocalizedString;

import java.util.Optional;

import static org.fenixedu.applicationtracking.domain.form.fields.OptionsField.optionsAdapterFromJson;

/**
 * Created by nurv on 07/07/16.
 */
public class Country extends FormField {

    public Country(String key, Optional<Predicate> requiredPredicate, Optional<Predicate> includeIfPredicate,
            LocalizedString label, Optional<FieldValidator> fieldValidator) {
        super(key, requiredPredicate, includeIfPredicate, label, fieldValidator);
    }

    public static Country fromJson(JsonElement jsonElement) {
        JsonObject json = jsonElement.getAsJsonObject();
        return new Country(keyFromJson(json), requiredPredicateFromJson(json), includeIfPredicateFromJson(json),
                labelFromJson(json), fieldValidatorFromJson(json));
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.COUNTRY;
    }
}
