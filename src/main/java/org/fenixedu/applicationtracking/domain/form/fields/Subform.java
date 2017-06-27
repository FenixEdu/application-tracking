package org.fenixedu.applicationtracking.domain.form.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.fenixedu.applicationtracking.domain.FieldValidator;
import org.fenixedu.applicationtracking.domain.form.FieldType;
import org.fenixedu.applicationtracking.domain.form.fields.predicate.Predicate;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Subform extends FormField {

    private static final String FIELDS = "fields";
    private static final String MIN = "min";
    private static final String MAX = "max";

    private final List<FormField> formFields;

    private final Optional<Integer> minInstances;
    private final Optional<Integer> maxInstances;

    public Subform(String key, Optional<Predicate> requiredPredicate, Optional<Predicate> includeIfPredicate,
            LocalizedString label, Optional<FieldValidator> fieldValidator, List<FormField> formFields,
            Optional<Integer> minInstances, Optional<Integer> maxInstances) {
        super(key, requiredPredicate, includeIfPredicate, label, fieldValidator);
        this.formFields = formFields;
        this.minInstances = minInstances;
        this.maxInstances = maxInstances;
    }

    public List<FormField> getFormFields() {
        return formFields;
    }

    public static Subform fromJson(JsonElement jsonElement) {
        List<FormField> formFields = new ArrayList<FormField>();
        JsonObject json = jsonElement.getAsJsonObject();
        json.get(FIELDS).getAsJsonArray().forEach(jsonEl -> {
            formFields.add(FormField.fromJson(jsonEl));
        });
        Optional<Integer> minInstances = json.has(MIN) ? Optional.of(json.get(MIN).getAsInt()) : Optional.empty();
        Optional<Integer> maxInstances = json.has(MAX) ? Optional.of(json.get(MAX).getAsInt()) : Optional.empty();
        return new Subform(keyFromJson(json), requiredPredicateFromJson(json), includeIfPredicateFromJson(json),
                labelFromJson(json), fieldValidatorFromJson(json), formFields, minInstances, maxInstances);
    }

    @Override
    public JsonElement json() {
        JsonObject json = super.json().getAsJsonObject();
        formFields.forEach(formField -> {
            json.add(FIELDS, formField.json());
        });
        minInstances.ifPresent(minInstances -> {
            json.addProperty("min", minInstances);
        });
        maxInstances.ifPresent(maxInstances -> {
            json.addProperty("max", maxInstances);
        });
        return json;
    }

    public Optional<Integer> getMinInstances() {
        return minInstances;
    }

    public Optional<Integer> getMaxInstances() {
        return maxInstances;
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.SUBFORM;
    }

}
