package org.fenixedu.applicationtracking.ui.applicant;

import com.google.common.net.UrlEscapers;
import org.fenixedu.applicationtracking.domain.Actor;
import org.fenixedu.applicationtracking.domain.Application;
import org.fenixedu.applicationtracking.domain.Period;
import org.fenixedu.applicationtracking.service.CaptchaService;
import org.fenixedu.applicationtracking.service.NotificationService;
import org.fenixedu.applicationtracking.service.SignupService;
import org.fenixedu.applicationtracking.ui.BaseApplicationController;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/public-applications/{period}")
public class SignupController extends BaseApplicationController {

    @Autowired(required = false)
    private CaptchaService captcha;

    private final NotificationService notificationService;
    private final SignupService signupService;

    @Autowired
    public SignupController(NotificationService notificationService, SignupService signupService) {
        this.notificationService = notificationService;
        this.signupService = signupService;
    }

    @RequestMapping
    public String home(Model model, @PathVariable String period) {
        savePeriod(model, period);
        return "application-tracking/home";
    }

    @RequestMapping("/retrieve")
    public String retrieve(Model model, @PathVariable String period) {
        savePeriod(model, period);
        return "application-tracking/signup/retrieve";
    }

    @RequestMapping("/retrieve-user")
    public RedirectView retrieveUser(@PathVariable String period) {
        Period p = extractPeriod(period);
        if (!Authenticate.isLogged()) {
            // Redirect to login, calling back to this page
            return new RedirectView("/login?callback=" + CoreConfiguration.getConfiguration().applicationUrl()
                    + "/public-applications/" + period + "/retrieve-user", true);
        } else {
            return new RedirectView(
                    signupService.findApplicationForUser(p, Authenticate.getUser()).map(app -> publicUrlForApplicant(app))
                            .orElse("/public-applications/" + period + "/apply-user"), true);
        }
    }

    @RequestMapping(value = "/retrieve", method = RequestMethod.POST)
    public String retrieveApplication(Model model, @PathVariable String period, @RequestParam String email,
            @RequestParam(required = false) String accessCode) throws Exception {
        Period p = extractPeriod(period);
        Optional<Application> application = p.findApplicationByEmail(email);
        if (!application.isPresent()) {
            model.addAttribute("badCode", true);
            return retrieve(model, period);
        }
        if (accessCode == null) {
            regenerateLink(application.get());
            return "redirect:/public-applications/" + period + "/retrieve?success";
        } else {
            Optional<Actor> actor = application.get().findActor(accessCode);
            if (actor.isPresent()) {
                return "redirect:" + publicUrlFor(actor.get());
            } else {
                model.addAttribute("badCode", true);
                return retrieve(model, period);
            }
        }
    }

    private void regenerateLink(Application application) throws Exception {
        // Regenerate the secret, and send email with the proper link
        signupService.regenerateSecret(application.getApplicant());

        notificationService.builder(application.getApplicant(), "retrieve.application")
                .param("link", publicUrlForApplicant(application)).send(application.getEmail());
    }

    @RequestMapping("/apply")
    public String apply(Model model, @PathVariable String period) {
        Period p = savePeriod(model, period);
        if (!p.isOpen()) {
            return "redirect:/public-applications/" + period;
        }
        if (captcha != null) {
            model.addAttribute("captcha", captcha);
        }
        return "application-tracking/signup/apply";
    }

    @RequestMapping("/apply-success")
    public String applySuccess(Model model, @PathVariable String period) {
        savePeriod(model, period);
        return "application-tracking/signup/apply-success";
    }

    @RequestMapping("/apply-user")
    public String applyWithLogin(Model model, @PathVariable String period) {
        Period p = savePeriod(model, period);
        if (!p.isOpen()) {
            return "redirect:/public-applications/" + period;
        }
        if (!Authenticate.isLogged()) {
            return "redirect:/login?callback=" + CoreConfiguration.getConfiguration().applicationUrl() + "/public-applications/"
                    + period + "/apply-user";
        } else {
            signupService.findApplicationForUser(p, Authenticate.getUser()).ifPresent(app -> model.addAttribute("existing", app));
            return "application-tracking/signup/apply-user";
        }
    }

    @RequestMapping(value = "/apply-user", method = RequestMethod.POST)
    public RedirectView createApplicationForUser(@PathVariable String period) throws Exception {
        Period p = extractPeriod(period);
        if (!p.isOpen()) {
            return new RedirectView("/public-applications/" + period, true);
        }
        if (!Authenticate.isLogged()) {
            return new RedirectView("/login?callback=" + CoreConfiguration.getConfiguration().applicationUrl()
                    + "/public-applications/" + period + "/apply-user", true);
        } else {
            Application application = signupService.createApplication(p, Authenticate.getUser());
            String link = publicUrlForApplicant(application);
            notificationService.builder(application.getApplicant(), "application.created").param("link", link)
                    .send(application.getEmail());
            return new RedirectView(link);
        }
    }

    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public RedirectView createApplicationRequest(@PathVariable String period, @RequestParam String email,
            HttpServletRequest request) throws Exception {
        Period p = extractPeriod(period);
        if (!p.isOpen()) {
            return new RedirectView("/public-applications/" + period, true);
        }
        boolean valid = (captcha == null || captcha.isValid(request)) && validateEmail(email);
        if (!valid) {
            return new RedirectView("/public-applications/" + period + "/apply?error", true);
        }
        Optional<Application> application = p.findApplicationByEmail(email);
        if (application.isPresent()) {
            regenerateLink(application.get());
        } else {
            notificationService.builder(p, "email.verification").param("link", generateCallbackUrl(p, email)).send(email);
        }
        return new RedirectView("/public-applications/" + period + "/apply-success", true);
    }

    private boolean validateEmail(String email) {
        try {
            InternetAddress address = new InternetAddress(email);
            address.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }

    private String generateCallbackUrl(Period period, String email) {
        String nonce = UUID.randomUUID().toString();
        long now = Instant.now().toEpochMilli();

        StringBuilder builder = new StringBuilder();
        builder.append(CoreConfiguration.getConfiguration().applicationUrl());
        builder.append("/public-applications/");
        builder.append(period.getSlug());
        builder.append("/create-application?email=");
        builder.append(UrlEscapers.urlFormParameterEscaper().escape(email));
        builder.append("&nonce=");
        builder.append(nonce);
        builder.append("&timestamp=");
        builder.append(now);
        builder.append("&hash=");
        builder.append(signupService.generateHashForApplicant(now, period, email, nonce));
        return builder.toString();
    }

    @RequestMapping("/create-application")
    public String prepareCreateApplication(Model model, @PathVariable String period, @RequestParam String email,
            @RequestParam String nonce, @RequestParam long timestamp, @RequestParam String hash) {
        Period p = savePeriod(model, period);
        if (!p.isOpen()) {
            return "redirect:/public-applications/" + period;
        }
        if (signupService.validateHash(timestamp, p, nonce, email, hash) && !p.findApplicationByEmail(email).isPresent()) {
            model.addAttribute("email", email);
            model.addAttribute("nonce", nonce);
            model.addAttribute("timestamp", timestamp);
            model.addAttribute("hash", hash);
        } else {
            model.addAttribute("invalid", Boolean.TRUE);
        }
        return "application-tracking/signup/create-application";
    }

    @RequestMapping(value = "/create-application", method = RequestMethod.POST)
    public RedirectView createApplication(@PathVariable String period, @RequestParam String email, @RequestParam String nonce,
            @RequestParam long timestamp, @RequestParam String hash, @RequestParam String givenNames,
            @RequestParam String familyNames) throws Exception {
        Period p = extractPeriod(period);
        if (p.isOpen() && signupService.validateHash(timestamp, p, nonce, email, hash) && !p.findApplicationByEmail(email)
                .isPresent()) {
            Application application = signupService.createApplication(p, givenNames, familyNames, email);
            String link = publicUrlForApplicant(application);
            notificationService.builder(application.getApplicant(), "application.created").param("link", link).send(email);
            return new RedirectView(link, true);
        } else {
            return new RedirectView("/public-applications/" + period, true);
        }
    }

}
