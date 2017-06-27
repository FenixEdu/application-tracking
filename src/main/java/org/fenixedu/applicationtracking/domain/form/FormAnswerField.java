package org.fenixedu.applicationtracking.domain.form;

import com.google.gson.JsonElement;

public class FormAnswerField {

    private final String key;
    private final JsonElement value;

    public FormAnswerField(String key, JsonElement value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public JsonElement getValue() {
        return value;
    }

    public boolean hasKey(String fieldKey) {
        return this.getKey().equals(fieldKey);
    }

}
