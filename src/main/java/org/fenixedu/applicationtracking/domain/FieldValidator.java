package org.fenixedu.applicationtracking.domain;

import java.util.Optional;

import org.fenixedu.applicationtracking.service.ValidatorService;
import org.fenixedu.bennu.core.domain.NashornStrategy;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class FieldValidator extends FieldValidator_Base {

    @Autowired
    private ValidatorService validatorService;

    private FieldValidator(String slug, LocalizedString title, LocalizedString description, LocalizedString errorMessage,
            String javascriptFunction) {
        super();
        setRoot(ApplicationTracking.getInstance());
        setSlug(slug);
        setTitle(title);
        setDescription(description);
        setErrorMessage(errorMessage);
        setValidationCode(new NashornStrategy<FieldValidator.Validator>(FieldValidator.Validator.class, javascriptFunction));
    }

    public static FieldValidator create(String slug, LocalizedString title, LocalizedString description,
            LocalizedString errorMessage, String javascriptFunction) {
        return get(slug).orElseGet(() -> {
            return new FieldValidator(slug, title, description, errorMessage, javascriptFunction);
        });
    }

    public static Optional<FieldValidator> get(String slug) {
        return ApplicationTracking.getInstance().getFieldValidatorSet().stream().filter(validator -> validator.hasSlug(slug))
                .findFirst();
    }

    public boolean hasSlug(String slug) {
        return getSlug().equals(slug);
    }

    public static FieldValidator fromJson(JsonElement json) {
        return get(json.getAsString()).get();
    }

    public JsonElement json() {
    	JsonObject result = new JsonObject();
    	result.addProperty("slug", getSlug());
    	result.add("title", getTitle().json());
    	result.add("description", getDescription().json());
    	result.add("errorMessage", getErrorMessage().json());
    	result.addProperty("code", getValidationCode().getCode());
    	return result;
    }

    public interface Validator {
        boolean validate(Object value);
    }

    public Optional<LocalizedString> validate(JsonElement json) {
        JsonIfier JsonIfier =
                new NashornStrategy<JsonIfier>(JsonIfier.class, "function jsonify(data) { return JSON.parse(data); }")
                        .getStrategy();
        Object parsed = JsonIfier.jsonify(json.toString());
        return getValidationCode().getStrategy().validate(parsed) ? Optional.empty() : Optional.of(getErrorMessage());
    }

    public static interface JsonIfier {

        public Object jsonify(String source);

    }

}
