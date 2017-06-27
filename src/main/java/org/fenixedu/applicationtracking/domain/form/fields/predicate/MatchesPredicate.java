package org.fenixedu.applicationtracking.domain.form.fields.predicate;

import java.util.Optional;
import java.util.regex.Pattern;

import org.fenixedu.applicationtracking.domain.FormAnswer;
import org.fenixedu.applicationtracking.domain.form.FormAnswerField;

import com.google.gson.JsonObject;

public class MatchesPredicate extends ComplexPredicate {

    protected static final String PREDICATE_KEY = "matches";
    protected static final String PATTERN_KEY = "pattern";

    private final String pattern;

    public MatchesPredicate(String key, String pattern) {
        super(key);
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public boolean evaluate(Optional<FormAnswerField> formAnswerField, FormAnswer formAnswer) {
        return formAnswer.getFormAnswerFieldFromKey(getKey())
                .map(referencedFormAnswerField -> Pattern.matches(pattern, referencedFormAnswerField.getValue().getAsString()))
                .orElse(false);
    }

    @Override
    JsonObject getJsonPayload() {
        JsonObject payload = new JsonObject();
        payload.addProperty(PATTERN_KEY, pattern);
        return payload;
    }

    public static Predicate fromJson(JsonObject json) {
        return new MatchesPredicate(getKeyValue(json), json.get(PATTERN_KEY).getAsString());
    }

    @Override
    String getPredicateKey() {
        return PREDICATE_KEY;
    }
}
