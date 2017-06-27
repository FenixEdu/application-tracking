package org.fenixedu.applicationtracking.ui.backoffice;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.fenixedu.applicationtracking.domain.ApplicationTracking;
import org.fenixedu.applicationtracking.domain.MessageTemplate;
import org.fenixedu.applicationtracking.domain.Period;
import org.fenixedu.applicationtracking.domain.Purpose;
import org.fenixedu.applicationtracking.domain.PurposeConfiguration;
import org.fenixedu.applicationtracking.ui.BaseApplicationController;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.commons.stream.StreamUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import pt.ist.fenixframework.FenixFramework;

@RequestMapping("/application-tracking")
@SpringFunctionality(app = ApplicationTrackingController.class, title = "title.application.tracking")
@SpringApplication(group = "backoffice", path = "application-tracking", title = "title.application.tracking")
public class ApplicationTrackingController extends BaseApplicationController {

    @RequestMapping
    public String home(Model model) {
        model.addAttribute("applicationTracking", ApplicationTracking.getInstance());
        model.addAttribute("openPeriods", ApplicationTracking.getInstance().getOpenPeriods().collect(Collectors.toList()));
        model.addAttribute("upcomingPeriods", ApplicationTracking.getInstance().getFuturePeriods().collect(Collectors.toList()));
        model.addAttribute("closedPeriods", ApplicationTracking.getInstance().getClosedPeriods().collect(Collectors.toList()));
        return "application-tracking/backoffice/home";
    }

    @RequestMapping("/create")
    public String createPeriod(Model model) {
        model.addAttribute("applicationTracking", ApplicationTracking.getInstance());
        return "application-tracking/backoffice/create";
    }

    @ResponseBody
    @RequestMapping(value = "/find", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String findPeriodBySlug(@RequestParam String slug) {
        Optional<Period> period = Period.fromSlug(slug);
        JsonObject json = new JsonObject();
        json.addProperty("found", period.isPresent());
        return json.toString();
    }

    @ResponseBody
    @RequestMapping(value = "/find/{purpose}", produces = "application/json; charset=utf-8")
    public String findPeriodsByPurpose(@PathVariable Purpose purpose) {
        return purpose.getPurposeConfigurations().stream().map(PurposeConfiguration::getPeriod).map(period -> {
            JsonObject json = new JsonObject();
            json.addProperty("id", period.getExternalId());
            json.addProperty("name", period.getName().getContent());
            json.addProperty("slug", period.getSlug());
            json.addProperty("start", period.getStart().toInstant(ZoneOffset.UTC).toEpochMilli());
            json.addProperty("end", period.getEnd().toInstant(ZoneOffset.UTC).toEpochMilli());
            return json;
        }).collect(StreamUtils.toJsonArray()).toString();
    }

    @ResponseBody
    @RequestMapping(value = "/purposes", produces = "application/json; charset=utf-8")
    public String listPurposes() {
        return ApplicationTracking.getInstance().getPurposes().stream().map(purpose -> {
            JsonObject json = new JsonObject();
            json.addProperty("id", purpose.getExternalId());
            json.addProperty("name", purpose.getName().getContent());
            return json;
        }).collect(StreamUtils.toJsonArray()).toString();
    }

    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String createPeriod(@RequestBody String body) {
        Period period = createPeriod(new JsonParser().parse(body).getAsJsonObject());
        JsonObject json = new JsonObject();
        json.addProperty("id", period.getExternalId());
        return json.toString();
    }

    private Period createPeriod(JsonObject json) {
        final String slug = json.get("slug").getAsString();
        final LocalizedString name = LocalizedString.fromJson(json.get("name").getAsJsonObject());
        final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        final LocalDateTime startDateTime = LocalDateTime.parse(json.get("start").getAsString(), formatter);
        final LocalDateTime endDateTime = LocalDateTime.parse(json.get("end").getAsString(), formatter);
        if (json.has("template")) {
            final Period template = extractPeriod(json.get("template").getAsString());
            return Period.copyFrom(template, slug, name, startDateTime, endDateTime);
        } else {
            final Purpose purpose = ApplicationTracking.getInstance().getPurposes().stream()
                    .filter(p -> p.getExternalId().equals(json.get("purpose").getAsString())).findAny().get();

            Period period = Period.createNew(purpose, slug, name, startDateTime, endDateTime);

            FenixFramework.atomic(() -> {
                // Creating mock message templates necessary for the system to work.
                Locale EN = Locale.forLanguageTag("en-GB");
                new MessageTemplate(period, "email.verification", "Email Verification", new LocalizedString(EN,
                        "Application to {{period.name.content}} - Verify your email"), new LocalizedString(EN,
                        "To verify your email and continue your application, open the following link in your browser: {{link}}"),
                        new LocalizedString(EN, "To verify your email, click <a href=\"{{link}}\">here</a>."));

                new MessageTemplate(period, "retrieve.application", "Retrieve Application", new LocalizedString(EN,
                        "Retrieve your application to {{period.name.content}}"), new LocalizedString(EN,
                        "To access your application, open the following link in your browser: {{link}}"), new LocalizedString(EN,
                        "To access your application, click <a href=\"{{link}}\">here</a>."));

                new MessageTemplate(period, "application.created", "Application Created", new LocalizedString(EN,
                        "Your application to {{period.name.content}} has been created successfully"), new LocalizedString(EN,
                        "Dear {{actor.actorName}}, your application to {{period.name.content}} has been received. "
                                + "To access it, open the following URL in your browser {{link}}."
                                + "Your access code is {{actor.secret}}."), new LocalizedString(EN,
                        "<h1>Dear {{actor.actorName}},</h1><p>Your application to <b>{{period.name.content}} has been received.</p>"
                                + "Click <a href=\"{{link}}\">here</a> to access it. "
                                + "Your access code is <code>{{actor.secret}}</code>"));
            });
            return period;
        }
    }

}
