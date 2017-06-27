package org.fenixedu.applicationtracking.domain;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.applicationtracking.domain.form.FormAnswerField;
import org.fenixedu.commons.stream.StreamUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

public class FormAnswer {

    private final FormStepAnswer[] answers;

    public FormAnswer(Form form) {
        this.answers = new FormStepAnswer[form.getLength()];
    }

    private FormAnswer(FormStepAnswer[] answers) {
        this.answers = answers;
    }

    public Stream<FormStepAnswer> getAnswers() {
        return Arrays.stream(answers);
    }

    public JsonElement json() {
        return getAnswers().map(answer -> answer == null ? JsonNull.INSTANCE : answer.json()).collect(StreamUtils.toJsonArray());
    }

    public static FormAnswer fromJson(JsonElement json) {
        return new FormAnswer(StreamUtils.of(json.getAsJsonArray()).map(FormStepAnswer::fromJson).toArray(FormStepAnswer[]::new));
    }

    public Optional<FormStepAnswer> getAnswerStep(int index) {
        return index < 0 || index >= answers.length ? Optional.empty() : Optional.ofNullable(answers[index]);
    }

    public FormAnswer with(int idx, FormStepAnswer answer) {
        FormStepAnswer[] newAnswers = Arrays.copyOf(answers, answers.length);
        newAnswers[idx] = answer;
        return new FormAnswer(newAnswers);
    }

    public Optional<FormAnswerField> getFormAnswerFieldFromKey(String key) {
        System.out.println("Getting form answer field from key: " + key);
        String[] tokens = key.split("\\.");
        String slug = tokens[0];
        String fieldKey = tokens[1];
        return Arrays.stream(answers).filter(formStepAnswer -> formStepAnswer.hasSlug(slug))
                .flatMap(formStepAnswer -> formStepAnswer.getFields().stream())
                .filter(formAnswerField -> formAnswerField.hasKey(fieldKey)).findAny();

    }
}