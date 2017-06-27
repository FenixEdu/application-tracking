package org.fenixedu.applicationtracking.ui.backoffice;

import pt.ist.fenixframework.FenixFramework;

import org.fenixedu.applicationtracking.domain.MessageTemplate;
import org.fenixedu.applicationtracking.domain.Period;
import org.fenixedu.applicationtracking.ui.BaseApplicationController;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@RequestMapping("/application-tracking/{period}/templates")
@BennuSpringController(ApplicationTrackingController.class)
public class MessageTemplateController extends BaseApplicationController {

    @RequestMapping
    public String home(Model model, @PathVariable String period) {
        savePeriod(model, period);
        return "application-tracking/backoffice/templates/home";
    }

    @RequestMapping(method = RequestMethod.POST)
    public RedirectView createTemplate(@PathVariable String period, @RequestParam String key, @RequestParam String name)
            throws Exception {
        Period p = extractPeriod(period);
        MessageTemplate template = FenixFramework
                .atomic(() -> new MessageTemplate(p, key, name, new LocalizedString(), new LocalizedString(),
                        new LocalizedString()));
        return new RedirectView("/application-tracking/" + period + "/templates/" + template.getExternalId(), true);
    }

    @RequestMapping("/{template}")
    public String editTemplate(Model model, @PathVariable String period, @PathVariable MessageTemplate template) {
        savePeriod(model, period);
        model.addAttribute("template", template);
        return "application-tracking/backoffice/templates/edit";
    }

    @RequestMapping(value = "/{template}", method = RequestMethod.POST)
    public RedirectView updateTemplate(@PathVariable String period, @PathVariable MessageTemplate template,
            @RequestParam String name, @RequestParam LocalizedString subject, @RequestParam LocalizedString plainBody,
            @RequestParam LocalizedString body) {
        FenixFramework.atomic(() -> {
            template.setName(name);
            template.setSubject(subject);
            template.setPlainBody(plainBody);
            template.setBody(body);
        });
        return new RedirectView("/application-tracking/" + period + "/templates/" + template.getExternalId(), true);
    }

    @RequestMapping(value = "/{template}/delete", method = RequestMethod.POST)
    public RedirectView deleteTemplate(@PathVariable String period, @PathVariable MessageTemplate template) {
        FenixFramework.atomic(template::delete);
        return new RedirectView("/application-tracking/" + period + "/templates", true);
    }
}
