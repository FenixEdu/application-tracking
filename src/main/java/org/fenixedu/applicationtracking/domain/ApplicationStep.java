package org.fenixedu.applicationtracking.domain;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.fenixedu.commons.i18n.LocalizedString;

public class ApplicationStep {
    private final Form form;

    private final int index;

    private final Supplier<Optional<FormAnswer>> answerGetter;

    private final Consumer<FormAnswer> answerChanger;

    private ApplicationStep(Form form, int index, Supplier<Optional<FormAnswer>> answerGetter, Consumer<FormAnswer> answerChanger) {
        super();
        this.form = form;
        this.index = index;
        this.answerGetter = answerGetter;
        this.answerChanger = answerChanger;
    }

    public Form getForm() {
        return form;
    }

    public FormStep getFormStep() {
        return form.getStep(index);
    }

    public Optional<FormStepAnswer> getAnswer() {
        return getOrCreateAnswer().getAnswerStep(index);
    }

    public void setAnswer(FormStepAnswer answer) {
        answerChanger.accept(getOrCreateAnswer().with(index, answer));
    }

    public Stream<LocalizedString> validate(Optional<FormStepAnswer> formStepAnswer, FormAnswer formAnswer) {
        return getFormStep().validate(formStepAnswer, formAnswer);
    }

    private FormAnswer getOrCreateAnswer() {
        return answerGetter.get().orElseGet(() -> new FormAnswer(form));
    }

    static Stream<ApplicationStep> steps(Optional<Form> optionalForm, Supplier<Optional<FormAnswer>> answerGetter,
            Consumer<FormAnswer> answerChanger) {
        return optionalForm.map(
                form -> IntStream.range(0, form.getLength()).<ApplicationStep> mapToObj(
                        index -> new ApplicationStep(form, index, answerGetter, answerChanger))).orElseGet(Stream::empty);
    }
}
