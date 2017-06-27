package org.fenixedu.applicationtracking.ui.configuration;

import java.util.List;
import java.util.stream.Collectors;

import org.fenixedu.applicationtracking.domain.ApplicationTracking;
import org.fenixedu.applicationtracking.domain.PaymentInstructionsTemplate;
import org.fenixedu.applicationtracking.domain.Purpose;
import org.fenixedu.applicationtracking.domain.Requirement;
import org.fenixedu.applicationtracking.service.ValidatorService;
import org.fenixedu.applicationtracking.ui.backoffice.ApplicationTrackingController;
import org.fenixedu.bennu.core.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DomainClass;

@RequestMapping("/application-tracking/configuration")
@SpringFunctionality(app = ApplicationTrackingController.class, title = "title.application.tracking.configuration")
public class ConfigurationController {

	@RequestMapping
	public String home(Model model) {
		model.addAttribute("applicationTracking", ApplicationTracking.getInstance());
		model.addAttribute("paymentInstructionsTemplate", PaymentInstructionsTemplate.getInstance());
		model.addAttribute("purposeTypes", subclassesOf(Purpose.class));
		model.addAttribute("requirementTypes", subclassesOf(Requirement.class));
		return "application-tracking/configuration/home";
	}

	private List<String> subclassesOf(Class<?> type) {
		return FenixFramework.getDomainModel().getDomainClasses().stream().filter(t -> isSubclass(t, type))
				.filter(t -> !t.getFullName().equals(type.getName())).map(DomainClass::getFullName).sorted()
				.collect(Collectors.toList());
	}

	private boolean isSubclass(DomainClass domainClass, Class<?> type) {
		while (domainClass != null) {
			if (domainClass.getFullName().equals(type.getName())) {
				return true;
			}
			domainClass = (DomainClass) domainClass.getSuperclass();
		}
		return false;
	}

	@RequestMapping(value = "/backoffice-group", method = RequestMethod.POST)
	public RedirectView changeBackofficeGroup(@RequestParam String group, RedirectAttributes attr) throws Exception {
		try {
			FenixFramework.atomic(() -> ApplicationTracking.getInstance().setBackofficeGroup(Group.parse(group)));
		} catch (DomainException e) {
			attr.addFlashAttribute("errors", "Could not parse group expression");
		}
		return new RedirectView("/application-tracking/configuration", true);
	}

	@RequestMapping(value = "/paymentInstructionsTemplate", method = RequestMethod.POST)
	public RedirectView paymentInstructionsTemplate(@RequestParam LocalizedString body) throws Exception {
		FenixFramework.atomic(() -> PaymentInstructionsTemplate.getInstance().setBody(body));
		return new RedirectView("/application-tracking/configuration", true);
	}

	@RequestMapping(value = "/purpose", method = RequestMethod.POST)
	public RedirectView createPurpose(@RequestParam String type, @RequestParam LocalizedString name) throws Exception {
		Purpose purpose = FenixFramework.atomic(() -> {
			Class<Purpose> t = (Class<Purpose>) Class.forName(type);

			if (!Purpose.class.isAssignableFrom(t)) {
				throw new IllegalArgumentException("Provided type is not a purpose!");
			}

			Purpose p = t.newInstance();

			p.setName(name);
			p.setDescription(name);

			return p;
		});
		return new RedirectView("/application-tracking/configuration/purpose/" + purpose.getExternalId(), true);
	}

	@RequestMapping(value = "/validators", method = RequestMethod.POST)
	public RedirectView createValidator(@RequestParam String slug, @RequestParam LocalizedString title,
			@RequestParam LocalizedString description, @RequestParam LocalizedString errorMessage,
			@RequestParam String code) throws Exception {
		ValidatorService.createFromJson(slug, title, description, errorMessage, code);
		return new RedirectView("/application-tracking/application-tracking-configuration", true);
	}

	@RequestMapping("/purpose/{purpose}")
	public String viewPurpose(Model model, @PathVariable Purpose purpose) {
		model.addAttribute("purpose", purpose);
		return "application-tracking/configuration/edit-purpose";
	}

	@RequestMapping(value = "/purpose/{purpose}", method = RequestMethod.POST)
	public RedirectView editPurpose(@PathVariable Purpose purpose, @RequestParam LocalizedString name,
			@RequestParam LocalizedString description) {
		FenixFramework.atomic(() -> {
			purpose.setName(name);
			purpose.setDescription(description);
		});
		return new RedirectView("/application-tracking/configuration", true);
	}

    @RequestMapping(value = "/purpose/{purpose}/delete", method = RequestMethod.POST)
    public RedirectView deletePurpose(@PathVariable Purpose purpose) {
        FenixFramework.atomic(() -> {
            purpose.delete();
        });
        return new RedirectView("/application-tracking/configuration", true);
    }

	@RequestMapping(value = "/requirement", method = RequestMethod.POST)
	public RedirectView createRequirement(@RequestParam String type, @RequestParam LocalizedString name)
			throws Exception {
		Requirement requirement = FenixFramework.atomic(() -> {
			Class<Requirement> t = (Class<Requirement>) Class.forName(type);

			if (!Requirement.class.isAssignableFrom(t)) {
				throw new IllegalArgumentException("Provided type is not a requirement!");
			}

			Requirement req = t.newInstance();

			req.setName(name);
			req.setDescription(name);

			return req;
		});
		return new RedirectView("/application-tracking/configuration/requirement/" + requirement.getExternalId(), true);
	}

	@RequestMapping("/requirement/{requirement}")
	public String viewRequirement(Model model, @PathVariable Requirement requirement) {
		model.addAttribute("requirement", requirement);
		return "application-tracking/configuration/edit-requirement";
	}

	@RequestMapping(value = "/requirement/{requirement}", method = RequestMethod.POST)
	public RedirectView editRequirement(@PathVariable Requirement requirement, @RequestParam LocalizedString name,
			@RequestParam LocalizedString description) {
		FenixFramework.atomic(() -> {
			requirement.setName(name);
			requirement.setDescription(description);
		});
		return new RedirectView("/application-tracking/configuration", true);
	}

    @RequestMapping(value = "/requirement/{requirement}/delete", method = RequestMethod.POST)
    public RedirectView deleteRequirement(@PathVariable Requirement requirement) {
        FenixFramework.atomic(() -> {
            requirement.delete();
        });
        return new RedirectView("/application-tracking/configuration", true);
    }

}
