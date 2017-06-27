package org.fenixedu.applicationtracking.domain.form.fields;

import java.util.List;

import org.fenixedu.applicationtracking.domain.ApplicationTracking;

import com.google.gson.JsonElement;

public abstract class OptionsAdapter {

    abstract List<Option> getOptions();

    static OptionsAdapter fromJson(JsonElement json) {
        if (json.isJsonArray()) {
            return StaticOptionsAdapter.fromJson(json);
        } else {
            return ApplicationTracking.getInstance().getOptionAdapters(json.getAsString());
        }

    }

    abstract JsonElement json();
}
