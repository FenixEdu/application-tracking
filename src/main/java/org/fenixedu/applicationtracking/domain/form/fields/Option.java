package org.fenixedu.applicationtracking.domain.form.fields;

import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Option {

    private static final String KEY = "key";
    private static final String LABEL = "label";

    private String key;
    private LocalizedString label;

    public Option(String key, LocalizedString label) {
        this.key = key;
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public static Option fromJson(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();
        return new Option(jsonObject.get(KEY).getAsString(), LocalizedString.fromJson(jsonObject.get(LABEL)));
    }

    public JsonElement json() {
        JsonObject result = new JsonObject();
        result.addProperty(KEY, getKey());
        result.add(LABEL, label.json());
        return result;
    }
}
