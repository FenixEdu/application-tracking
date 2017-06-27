package org.fenixedu.applicationtracking.domain.form.fields;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.fenixedu.applicationtracking.domain.FieldValidator;
import org.fenixedu.applicationtracking.domain.FormAnswer;
import org.fenixedu.applicationtracking.domain.form.FieldType;
import org.fenixedu.applicationtracking.domain.form.FormAnswerField;
import org.fenixedu.applicationtracking.domain.form.fields.predicate.Predicate;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class FormField {

    private static final String FIELD_TYPE = "type";
    private static final String KEY = "key";
    private static final String REQUIRED = "required";
    private static final String INCLUDE_IF = "include-if";
    private static final String LABEL = "label";
    private static final String VALIDATOR = "validator";

    private final String key;

    private final Optional<Predicate> requiredPredicate;
    private final Optional<Predicate> includeIfPredicate;

    private final LocalizedString label;

    private final Optional<FieldValidator> fieldValidator;

    public FormField(String key, Optional<Predicate> requiredPredicate, Optional<Predicate> includeIfPredicate,
            LocalizedString label, Optional<FieldValidator> fieldValidator) {
        this.key = key;
        this.requiredPredicate = requiredPredicate;
        this.includeIfPredicate = includeIfPredicate;
        this.label = label;
        this.fieldValidator = fieldValidator;
    }

    public String getKey() {
        return key;
    }

    /**
     * Returns an optional required predicate. The predicate is present if it was defined during the form specification.
     * 
     * @return an optional predicate.
     */
    public Optional<Predicate> getRequiredPredicate() {
        return requiredPredicate;
    }

    /**
     * Returns an optional include if predicate. The predicate is present if it was defined during the form specification.
     * 
     * @return an optional predicate.
     */
    public Optional<Predicate> getIncludeIfPredicate() {
        return includeIfPredicate;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public abstract FieldType getFieldType();

    public Optional<FieldValidator> getFieldValidator() {
        return fieldValidator;
    }

    public JsonElement json() {
        JsonObject result = new JsonObject();
        result.addProperty(FIELD_TYPE, getFieldType().toString());
        result.addProperty(KEY, key);
        requiredPredicate.ifPresent(requiredPredicate -> {
            result.add(REQUIRED, requiredPredicate.json());
        });
        includeIfPredicate.ifPresent(includeIfPredicate -> {
            result.add(INCLUDE_IF, includeIfPredicate.json());
        });
        result.add(LABEL, label.json());
        fieldValidator.ifPresent(fieldValidator -> {
            result.addProperty(VALIDATOR, fieldValidator.getSlug());
        });
        return result;
    }

    public static FormField fromJson(JsonElement jsonElement) {
        JsonObject json = jsonElement.getAsJsonObject();
        switch (fieldTypeFromJson(json)) {
        case SUBFORM:
            return Subform.fromJson(json);
        case TEXTAREA:
            return Textarea.fromJson(json);
        case FILE:
            return File.fromJson(json);
        case PASSWORD:
            return Password.fromJson(json);
        case CHECKBOX_GROUP:
            return CheckboxGroup.fromJson(json);
        case TEXT:
            return Text.fromJson(json);
        case SELECT:
            return Select.fromJson(json);
        case RADIO_GROUP:
            return RadioGroup.fromJson(json);
        case DATE:
            return Date.fromJson(json);
        case TIME:
            return Time.fromJson(json);
        case DATETIME:
            return DateTime.fromJson(json);
        case AVATAR:
        	return Avatar.fromJson(json);
        case COUNTRY:
            return Country.fromJson(json);
        default:
            throw new UnsupportedOperationException();
        }
    }

    public static FieldType fieldTypeFromJson(JsonObject json) {
        return FieldType.fromType(json.get(FIELD_TYPE).getAsString());
    }

    public static String keyFromJson(JsonObject json) {
        return json.get(KEY).getAsString();
    }

    public static LocalizedString labelFromJson(JsonObject json) {
        return LocalizedString.fromJson(json.get(LABEL));
    }

    public static Optional<Predicate> requiredPredicateFromJson(JsonObject json) {
        return json.has(REQUIRED) ? Predicate.fromJson(json.get(REQUIRED)) : Optional.empty();
    }

    public static Optional<Predicate> includeIfPredicateFromJson(JsonObject json) {
        return json.has(INCLUDE_IF) ? Predicate.fromJson(json.get(INCLUDE_IF)) : Optional.empty();
    }

    public static Optional<FieldValidator> fieldValidatorFromJson(JsonObject json) {
        return json.has(VALIDATOR) ? FieldValidator.get(json.get(VALIDATOR).getAsString()) : Optional.empty();
    }

    public boolean hasKey(String key) {
        return key != null && key.equals(this.key);
    }

    public static final LocalizedString REQUIRED_ERROR = new LocalizedString().with(Locale.ENGLISH, "This fiedl is required");

    public Stream<LocalizedString> validate(Optional<FormAnswerField> formAnswerField, FormAnswer ctx) {
        Builder<LocalizedString> builder = Stream.builder();
        getRequiredPredicate().ifPresent(predicate -> {
            if (!predicate.evaluate(formAnswerField, ctx)) {
                builder.add(REQUIRED_ERROR);
            }
        });
        return builder.build();
    }
}
