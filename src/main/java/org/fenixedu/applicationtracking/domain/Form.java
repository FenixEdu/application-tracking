package org.fenixedu.applicationtracking.domain;

import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.commons.stream.StreamUtils;

import com.google.gson.JsonElement;

public class Form {
    private final FormStep[] steps;

    public Form(FormStep[] steps) {
        super();
        this.steps = steps;
    }

    public Stream<FormStep> getSteps() {
        return Arrays.stream(steps);
    }

    FormStep getStep(int index) {
        return steps[index];
    }

    public int getLength() {
        return steps.length;
    }

    public JsonElement json() {
        return getSteps().map(FormStep::json).collect(StreamUtils.toJsonArray());
    }

    public static Form fromJson(JsonElement json) {
        return new Form(StreamUtils.of(json.getAsJsonArray()).map(FormStep::fromJson).toArray(FormStep[]::new));
    }

    public Stream<LocalizedString> validate(FormAnswer answer) {
        Builder<LocalizedString> report = Stream.builder();
        for (int i = 0; i < steps.length; i++) {
            FormStep step = steps[i];
            step.validate(answer.getAnswerStep(i), answer).forEach(error -> report.accept(error));
        }
        return report.build();
    }
}