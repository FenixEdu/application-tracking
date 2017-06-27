package org.fenixedu.applicationtracking.domain.form.fields;

import java.util.List;
import java.util.Optional;

import org.fenixedu.applicationtracking.domain.FieldValidator;
import org.fenixedu.applicationtracking.domain.form.fields.predicate.Predicate;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class OptionsField extends FormField {

    private static final String OPTIONS = "options";

    private final OptionsAdapter optionsAdapter;

    public OptionsField(String key, Optional<Predicate> requiredPredicate, Optional<Predicate> includeIfPredicate,
            LocalizedString label, Optional<FieldValidator> fieldValidator, OptionsAdapter optionsAdapter) {
        super(key, requiredPredicate, includeIfPredicate, label, fieldValidator);
        this.optionsAdapter = optionsAdapter;
    }

    public List<Option> getOptions() {
        return optionsAdapter.getOptions();
    }

    @Override
    public JsonElement json() {
        JsonObject result = super.json().getAsJsonObject();
        result.add(OPTIONS, optionsAdapter.json());
        return result;
    }

    public static OptionsAdapter optionsAdapterFromJson(JsonObject json) {
        return OptionsAdapter.fromJson(json.get(OPTIONS));
    }

}
