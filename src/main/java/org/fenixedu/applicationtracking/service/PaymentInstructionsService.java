package org.fenixedu.applicationtracking.service;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.extension.escaper.EscaperExtension;
import com.mitchellbosecke.pebble.loader.StringLoader;
import org.fenixedu.applicationtracking.domain.Application;
import org.fenixedu.applicationtracking.domain.ApplicationTrackingDomainException;
import org.fenixedu.applicationtracking.domain.PaymentInstructionsTemplate;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

@Service
public class PaymentInstructionsService {

    private final PebbleEngine engine;

    private final PaymentService paymentService;

    @Autowired
    public PaymentInstructionsService(PaymentService paymentService) {
        this.engine = new PebbleEngine(new StringLoader());

        // Disable auto-escaping
        this.engine.getExtension(EscaperExtension.class).setAutoEscaping(false);
        this.paymentService = paymentService;
    }

    public String getPaymentDetails(Application application, Locale locale) throws Exception {
        LocalizedString body = PaymentInstructionsTemplate.getInstance().getBody();
        if (body == null || body.isEmpty()) {
            throw ApplicationTrackingDomainException.paymentInstructionsTemplateNotDefined();
        }
        Map<String, Object> params = paymentService.getPaymentDetails(application);
        return render(body, locale, params);
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
}
