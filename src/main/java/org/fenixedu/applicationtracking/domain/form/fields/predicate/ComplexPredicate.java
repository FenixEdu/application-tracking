package org.fenixedu.applicationtracking.domain.form.fields.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class ComplexPredicate extends Predicate {

    static final String KEY_KEY = "key";

    private final String key;

    public ComplexPredicate(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    abstract String getPredicateKey();

    protected static String getKeyValue(JsonObject json) {
        return json.get(KEY_KEY).getAsString();
    }

    abstract JsonObject getJsonPayload();

    @Override
    public JsonElement json() {
        JsonObject result = new JsonObject();
        JsonObject payload = getJsonPayload();
        payload.addProperty(KEY_KEY, getKey());
        result.add(getPredicateKey(), payload);
        return result;
    }
}
