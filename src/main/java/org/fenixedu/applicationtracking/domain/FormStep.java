package org.fenixedu.applicationtracking.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.applicationtracking.domain.form.fields.FormField;
import org.fenixedu.applicationtracking.domain.form.fields.predicate.Predicate;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FormStep {

    private static final String SLUG_KEY = "slug";
    private static final String TITLE_KEY = "title";
    private static final String DESCRIPTION_KEY = "description";
    private static final String FIELDS_KEY = "fields";

    private final String slug;
    private final LocalizedString title;
    private final LocalizedString description;
    private final List<FormField> fields;

    public FormStep(String slug, LocalizedString title, LocalizedString description, List<FormField> fields) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.fields = fields;
    }

    public String getSlug() {
        return slug;
    }

    public JsonElement json() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(SLUG_KEY, slug);
        jsonObject.add(TITLE_KEY, title.json());
        jsonObject.add(DESCRIPTION_KEY, description.json());
        JsonArray fieldsArray = new JsonArray();
        fields.stream().forEach(field -> fieldsArray.add(field.json()));
        jsonObject.add("fields", fieldsArray);
        return jsonObject;
    }

    public static FormStep fromJson(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();
        String slug = jsonObject.get(SLUG_KEY).getAsString();
        LocalizedString title = LocalizedString.fromJson(jsonObject.get(TITLE_KEY));
        LocalizedString description = LocalizedString.fromJson(jsonObject.get(DESCRIPTION_KEY));
        List<FormField> fields = new ArrayList<FormField>();
        jsonObject.get(FIELDS_KEY).getAsJsonArray().forEach(jsonEl -> {
            fields.add(FormField.fromJson(jsonEl.getAsJsonObject()));
        });
        return new FormStep(slug, title, description, fields);
    }

    public Stream<LocalizedString> validate(Optional<FormStepAnswer> formStepAnswer, FormAnswer ctx) {
        return fields.stream().flatMap(
                field -> field.validate(ctx.getFormAnswerFieldFromKey(getSlug() + "." + field.getKey()), ctx));
    }

    public Optional<FieldValidator> getValidatorFor(String key) {
        return getFieldFromKey(key).flatMap(field -> field.getFieldValidator());
    }

    public Optional<Predicate> getRequiredPredicate(String key) {
        return getFieldFromKey(key).flatMap(field -> field.getRequiredPredicate());
    }

    public Optional<Predicate> getIncludeIfPredicate(String key) {
        return getFieldFromKey(key).flatMap(field -> field.getIncludeIfPredicate());
    }

    private Optional<FormField> getFieldFromKey(String key) {
        return fields.stream().filter(field -> field.hasKey(key)).findAny();
    }
}
