package org.fenixedu.applicationtracking.service;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.fenixedu.applicationtracking.domain.Actor;
import org.fenixedu.applicationtracking.domain.MessageTemplate;
import org.fenixedu.applicationtracking.domain.Period;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.escaper.EscaperExtension;
import com.mitchellbosecke.pebble.loader.StringLoader;

@Service
public class NotificationService {

    private final PebbleEngine engine;

    private final EmailDispatchingService emailDispatchingService;

    @Autowired
    public NotificationService(EmailDispatchingService emailDispatchingService) {
        this.engine = new PebbleEngine(new StringLoader());

        // Disable auto-escaping
        this.engine.getExtension(EscaperExtension.class).setAutoEscaping(false);
        this.emailDispatchingService = emailDispatchingService;
    }

    public Builder builder(Period period, String key) {
        return new Builder(period, key);
    }

    public Builder builder(Actor actor, String key) {
        return new Builder(actor, key);
    }

    private RenderedTemplate renderTemplate(MessageTemplate template, Locale locale, Map<String, Object> params)
            throws Exception {
        String subject = render(template.getSubject(), locale, params);
        String plainBody = render(template.getPlainBody(), locale, params);
        String body = render(template.getBody(), locale, params);
        return new RenderedTemplate(subject, plainBody, body);
    }

    private void send(String email, RenderedTemplate template) {
        emailDispatchingService.send(email, template.getSubject(), template.getPlainBody(), template.getBody());
    }

    private String render(LocalizedString body, Locale locale, Map<String, Object> params) throws Exception {
        String template = body.isEmpty() ? "" : body.getContent(locale);
        if (template == null) {
            template = body.getContent();
        }
        StringWriter writer = new StringWriter();
        engine.getTemplate(template).evaluate(writer, params, locale);
        return writer.toString();
    }

    private static class RenderedTemplate {

        private final String subject;
        private final String plainBody;
        private final String body;

        private RenderedTemplate(String subject, String plainBody, String body) {
            this.subject = subject;
            this.plainBody = plainBody;
            this.body = body;
        }

        public String getSubject() {
            return subject;
        }

        public String getPlainBody() {
            return plainBody;
        }

        public String getBody() {
            return body;
        }

    }

    public class Builder {

        private final MessageTemplate template;
        private final Map<String, Object> params = new HashMap<>();
        private Locale locale = I18N.getLocale();

        public Builder(Period period, String key) {
            this.template = period.templateForKey(key);
            param("period", period);
        }

        public Builder(Actor actor, String key) {
            this.template = actor.getApplication().getPeriod().templateForKey(key);
            this.locale = actor.getLocale();
            param("actor", actor);
            param("application", actor.getApplication());
            param("period", actor.getApplication().getPeriod());
        }

        public Builder param(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public void send(String to) throws Exception {
            param("email", to);
            NotificationService.this.send(to, renderTemplate(template, locale, params));
        }
    }

}
