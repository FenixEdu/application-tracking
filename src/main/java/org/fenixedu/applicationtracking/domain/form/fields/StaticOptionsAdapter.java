package org.fenixedu.applicationtracking.domain.form.fields;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class StaticOptionsAdapter extends OptionsAdapter {

    private List<Option> options;

    public StaticOptionsAdapter(List<Option> options) {
        this.options = options;
    }

    @Override
    public List<Option> getOptions() {
        return this.options;
    }

    public static OptionsAdapter fromJson(JsonElement json) {
        List<Option> options = new ArrayList<Option>();
        json.getAsJsonArray().forEach(el -> {
            options.add(Option.fromJson(el));
        });
        return new StaticOptionsAdapter(options);
    }

    @Override
    JsonElement json() {
        JsonArray result = new JsonArray();
        options.forEach(option -> {
            result.add(option.json());
        });
        return result;
    }

}
