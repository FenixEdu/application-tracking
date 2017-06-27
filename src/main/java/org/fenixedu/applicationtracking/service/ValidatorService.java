package org.fenixedu.applicationtracking.service;

import org.fenixedu.applicationtracking.domain.FieldValidator;
import org.fenixedu.applicationtracking.domain.FormStepAnswer;
import org.fenixedu.applicationtracking.domain.form.FormAnswerField;
import org.fenixedu.bennu.core.domain.NashornStrategy;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.stereotype.Service;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

@Service
public class ValidatorService {

    public boolean validate(FormStepAnswer formAnswer) {
        NashornStrategy<FieldValidator.Validator> validator = null;
        return formAnswer.getFields().stream().allMatch(answerField -> validate(answerField, validator));
    }

    public boolean validate(FormAnswerField answerField, NashornStrategy<FieldValidator.Validator> validator) {
        return validator.getStrategy().validate(answerField.getValue());
    }
	
    @Atomic(mode=TxMode.WRITE)
	public static void createFromJson(String slug, LocalizedString title, LocalizedString description,
			LocalizedString errorMessage, String code) {
    	FieldValidator.create(slug, title, description, errorMessage, code);
	}
}
