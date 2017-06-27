package org.fenixedu.applicationtracking.domain;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.applicationtracking.domain.form.FormAnswerField;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FormStepAnswer {

    private static final String SLUG_KEY = "slug";
    private static final String FIELDS_KEY = "fields";

    private final String slug;
    private final List<FormAnswerField> fields;

    public FormStepAnswer(String slug) {
        this(slug, new ArrayList<FormAnswerField>());
    }

    public FormStepAnswer(String slug, List<FormAnswerField> fields) {
        this.slug = slug;
        this.fields = fields;
    }

    public String getSlug() {
        return slug;
    }

    public List<FormAnswerField> getFields() {
        return fields;
    }

    public JsonElement json() {
        JsonObject json = new JsonObject();
        json.addProperty(SLUG_KEY, getSlug());
        JsonObject answerFields = new JsonObject();
        fields.forEach(field -> answerFields.add(field.getKey(), field.getValue()));
        json.add(FIELDS_KEY, answerFields);
        return json;
    }

    public static FormStepAnswer fromJson(JsonElement json) {
        if (json.isJsonNull()) {
            return null;
        }
        JsonObject jsonObject = json.getAsJsonObject();
        String slug = jsonObject.get(SLUG_KEY).getAsString();
        List<FormAnswerField> answerFields = new ArrayList<FormAnswerField>();

        jsonObject.get(FIELDS_KEY).getAsJsonObject().entrySet().forEach(entry -> {
            answerFields.add(new FormAnswerField(entry.getKey(), entry.getValue()));
        });

        return new FormStepAnswer(slug, answerFields);
    }

    public boolean hasSlug(String slug) {
        System.out.println("Has slug? " + slug);
        System.out.println("Slug: " + getSlug());
        return getSlug().equals(slug);
    }

}
