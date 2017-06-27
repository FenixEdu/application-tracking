package org.fenixedu.applicationtracking.domain;

import org.fenixedu.commons.i18n.LocalizedString;

public class MessageTemplate extends MessageTemplate_Base {

    public MessageTemplate(Period period, String templateKey, String name, LocalizedString subject, LocalizedString plainBody,
            LocalizedString body) {
        super();
        setPeriod(period);
        setTemplateKey(templateKey);
        setName(name);
        setSubject(subject);
        setPlainBody(plainBody);
        setBody(body);
    }

    @Override
    public String getTemplateKey() {
        return super.getTemplateKey();
    }

    public void delete() {
        setPeriod(null);
        deleteDomainObject();
    }

}
