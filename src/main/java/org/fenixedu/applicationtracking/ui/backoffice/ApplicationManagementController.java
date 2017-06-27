package org.fenixedu.applicationtracking.ui.backoffice;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.fenixedu.applicationtracking.domain.Application;
import org.fenixedu.applicationtracking.domain.ApplicationActivityLog;
import org.fenixedu.applicationtracking.domain.ApplicationStep;
import org.fenixedu.applicationtracking.domain.ApplicationTrackingDomainException;
import org.fenixedu.applicationtracking.domain.Period;
import org.fenixedu.applicationtracking.domain.RequirementFulfilment;
import org.fenixedu.applicationtracking.domain.Slot;
import org.fenixedu.applicationtracking.domain.StateType;
import org.fenixedu.applicationtracking.service.ApplicationCertificateService;
import org.fenixedu.applicationtracking.service.ApplicationService;
import org.fenixedu.applicationtracking.service.EmailDispatchingService;
import org.fenixedu.applicationtracking.service.FormService;
import org.fenixedu.applicationtracking.service.PaymentInstructionsService;
import org.fenixedu.applicationtracking.service.SlotService;
import org.fenixedu.applicationtracking.ui.BaseApplicationController;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.commons.spreadsheet.SheetData;
import org.fenixedu.commons.spreadsheet.SpreadsheetBuilder;
import org.fenixedu.commons.spreadsheet.WorkbookExportFormat;
import org.fenixedu.commons.stream.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import pt.ist.fenixframework.FenixFramework;

@RequestMapping("/application-tracking/{period}/applications")
@BennuSpringController(ApplicationTrackingController.class)
public class ApplicationManagementController extends BaseApplicationController {

    private final ApplicationService applicationService;

    private final SlotService slotService;

    private final FormService formService;

    private final PaymentInstructionsService paymentInstructionsService;

    private final ApplicationCertificateService applicationCertificateService;

    private final EmailDispatchingService emailDispatchingService;

    @Autowired
    private ApplicationManagementController(ApplicationService applicationService, SlotService slotService,
            FormService formService,
            PaymentInstructionsService paymentInstructionsService, ApplicationCertificateService applicationCertificateService,
            EmailDispatchingService emailDispatchingService) {
        this.applicationService = applicationService;
        this.slotService = slotService;
        this.formService = formService;
        this.paymentInstructionsService = paymentInstructionsService;
        this.applicationCertificateService = applicationCertificateService;
        this.emailDispatchingService = emailDispatchingService;
    }

    @RequestMapping
    public String home(Model model, @PathVariable String period) {
        Period p = savePeriod(model, period);
        JsonArray applications = p.getApplicationSet().stream().map(application -> {
            JsonObject json = new JsonObject();
            json.addProperty("number", application.getNumber());
            json.addProperty("name", String.join(" ", application.getGivenNames(), application.getFamilyNames()));
            json.addProperty("creation", application.getCreation().toInstant(ZoneOffset.UTC).toEpochMilli());
            json.addProperty("state", application.getState().getLocalizedName().getContent());
            if (p.getPurposeConfiguration().getApplicationFee() != null) {
                json.addProperty("paid", application.getPurposeFulfilment().isPaid());
            }
            json.addProperty("rank", application.calculateRank());
            json.addProperty(
                    "placement",
                    application.getPlacement() != null ? application.getPlacement().getName().getContent()
                            + " ("
                            + (application.getPurposeFulfilment().getSlotSelection()
                            .indexOf(application.getPlacement()) + 1) + ")" : null);
            return json;
        }).collect(StreamUtils.toJsonArray());
        model.addAttribute("applications", applications);
        model.addAttribute("states",
                Arrays.stream(StateType.values()).map(type -> new JsonPrimitive(type.getLocalizedName().getContent()))
                        .collect(StreamUtils.toJsonArray()));
        return "application-tracking/backoffice/applications/home";
    }

    @ResponseBody
    @RequestMapping(value = "/searchable-fields", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String searchableFields(@PathVariable String period) {
        Period p = extractPeriod(period);
        JsonArray array = new JsonArray();

        {
            JsonObject json = new JsonObject();
            json.addProperty("name", "givenNames");
            json.addProperty("title", "Nomes Próprios");
            json.addProperty("searchable", true);
            array.add(json);
        }

        {
            JsonObject json = new JsonObject();
            json.addProperty("name", "familyNames");
            json.addProperty("title", "Apelidos");
            json.addProperty("searchable", false);
            array.add(json);
        }

        {
            JsonObject json = new JsonObject();
            json.addProperty("name", "email");
            json.addProperty("title", "Email");
            json.addProperty("searchable", true);
            array.add(json);
        }

        {
            JsonObject json = new JsonObject();
            json.addProperty("name", "number");
            json.addProperty("title", "Número");
            json.addProperty("searchable", true);
            array.add(json);
        }

        return array.toString();
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createApplicationPage(Model model, @PathVariable String period) {
        savePeriod(model, period);
        return "/application-tracking/backoffice/applications/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createApplication(Model model, @PathVariable String period, @RequestParam String givenNames,
            @RequestParam String familyNames, @RequestParam String email) {
        Period p = savePeriod(model, period);

        Application application = applicationService.create(p, givenNames, familyNames, email);
        return "redirect:/application-tracking/" + period + "/applications/" + application.getNumber();
    }

    @ResponseBody
    @RequestMapping(value = "/{application}/user", method = RequestMethod.POST)
    public String setUser(Model model, @PathVariable String period, @PathVariable String application, @RequestBody String user) {
        Application app = saveApplication(model, period, application);
        JsonObject jsonUser = new JsonParser().parse(user).getAsJsonObject();
        String givenNames = jsonUser.get("givenNames").getAsString();
        String familyNames = jsonUser.get("familyNames").getAsString();
        String email = jsonUser.get("email").getAsString();
        
        FenixFramework.atomic(() -> {
            app.setEmail(email);
            app.setFamilyNames(familyNames);
            app.setGivenNames(givenNames);
        });
        return "{ \"result\" : \"ok\" }";
    }

    @RequestMapping("/{application}")
    public String viewApplication(Model model, @PathVariable String period, @PathVariable String application) {
        Application app = saveApplication(model, period, application);

        JsonElement slotSelectionPossibilities = slotService.getSlotExternalRepresentation(app);

        JsonElement slotSelection = slotService.getSlotSelectionExternalRepresentation(app);

        JsonObject slots = new JsonObject();

        slots.add("selected", slotSelection);
        slots.add("available", slotSelectionPossibilities);
        slots.addProperty("maxSlots", app.getPeriod().getPurposeConfiguration().getMaxSlotsPerApplication());

        JsonObject forms = new JsonObject();
        forms.add("purpose", stepify(app.getPurposeFulfilment().getApplicationStepStream()));
        forms.add("requirements",
                app.getRequirementFulfilments().stream().sorted().map(r -> stepify(r.getApplicationStepStream()))
                        .collect(StreamUtils.toJsonArray()));

        JsonObject status = new JsonObject();

        status.add("slots", slots);
        status.add("steps", forms);

        model.addAttribute("status", status);
        try {
            model.addAttribute("paymentInstructions", paymentInstructionsService.getPaymentDetails(app, I18N.getLocale()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "application-tracking/backoffice/applications/application";
    }

    private JsonObject stepify(Stream<ApplicationStep> steps) {
        JsonArray forms = new JsonArray();
        JsonObject answer = new JsonObject();
        steps.forEach(s -> {
            forms.add(formService.getFormExternalRepresentation(s));
            answer.add(s.getFormStep().getSlug(), formService.getFormAnswerExternalRepresentation(s));
        });

        JsonObject json = new JsonObject();

        json.add("forms", forms);
        json.add("answers", answer);
        return json;
    }

    private void saveStep(Stream<ApplicationStep> steps, JsonObject stepsJson, Stream.Builder<Runnable> action) {
        JsonObject answers = stepsJson.get("answers").getAsJsonObject();
        steps.forEach(s -> {
            String slug = s.getFormStep().getSlug();
            action.accept(() -> s.setAnswer(formService.fromExternalRepresentation(answers.get(slug))));
        });
    }

    @RequestMapping(value = "/{application}/state", method = RequestMethod.POST)
    public String changeState(Model model, @PathVariable String period, @PathVariable String application,
            @RequestParam StateType state) {
        Application app = saveApplication(model, period, application);
        applicationService.changeStateByServices(app, state, "backoffice");
        return "redirect:/application-tracking/" + period + "/applications/" + application;
    }

    @RequestMapping(value = "/{application}/grade", method = RequestMethod.POST)
    public String grade(Model model, @PathVariable String period, @PathVariable String application,
            @RequestParam String fulfilment, @RequestParam BigDecimal grade) {
        Application app = saveApplication(model, period, application);
        RequirementFulfilment fulflmt = FenixFramework.getDomainObject(fulfilment);
        applicationService.grade(app, fulflmt, grade, "backoffice");
        return "redirect:/application-tracking/" + period + "/applications/" + application;
    }

    @ResponseBody
    @RequestMapping(value = "/{application}/answer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String answer(Model model, @PathVariable String period, @PathVariable String application,
            @RequestBody String answers) {
        Application app = saveApplication(model, period, application);
        JsonObject jsonAnswers = new JsonParser().parse(answers).getAsJsonObject();

        List<Slot> choices = slotService.fromSlotFromExternalRepresentation(jsonAnswers.get("slots"), app);

        Stream.Builder<Runnable> doLater = Stream.builder();

        JsonElement formsAnswers = jsonAnswers.get("formsAnswer");
        if (formsAnswers != null) {
            JsonObject purpose = formsAnswers.getAsJsonObject().get("purpose").getAsJsonObject();
            saveStep(app.getPurposeFulfilment().getApplicationStepStream(), purpose, doLater);

            JsonArray requirements = formsAnswers.getAsJsonObject().get("requirements").getAsJsonArray();
            List<RequirementFulfilment> requirementFulfilments = app.getRequirementFulfilments();
            for (int i = 0; i < requirementFulfilments.size(); i++) {
                saveStep(requirementFulfilments.get(i).getApplicationStepStream(), requirements.get(i).getAsJsonObject(),
                        doLater);
            }
        }

        FenixFramework.atomic(() -> {
            app.getPurposeFulfilment().setSlotSelection(choices);
            doLater.build().forEach(Runnable::run);
        });
        return "{ \"result\" : \"ok\" }";
    }

    @ResponseBody
    @RequestMapping(value = "/{application}/application-certificate")
    public void applicationCertificate(Model model, @PathVariable String period, @PathVariable String application,
            HttpServletResponse response) {
        Application app = saveApplication(model, period, application);
        applicationCertificateService.download(app, Locale.ENGLISH, response);
    }

    private Application saveApplication(Model model, String period, String application) {
        Period p = savePeriod(model, period);
        Application app = p.findApplication(application)
                .orElseThrow(() -> ApplicationTrackingDomainException.applicationNotFound(application));
        model.addAttribute("application", app);
        return app;
    }

    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public void exportToExcel(@PathVariable String period, @RequestParam(required = false) String numbers,
            HttpServletResponse response) throws IOException {
        Period p = extractPeriod(period);
        List<Application> applications = getApplications(p, numbers).collect(Collectors.toList());

        SheetData<Application> data = new SheetData<Application>(applications) {
            @Override
            protected void makeLine(Application application) {
                addCell("Number", application.getNumber());
                addCell("Name", String.join(" ", application.getGivenNames(), application.getFamilyNames()));
                addCell("State", application.getState().getLocalizedName().getContent());
            }
        };
        response.setHeader("Content-Disposition", "attachment;filename=candidates-" + p.getSlug() + ".xls");
        try (OutputStream out = response.getOutputStream()) {
            new SpreadsheetBuilder().addSheet("Candidates", data).build(WorkbookExportFormat.EXCEL, out);
        }
    }

    @RequestMapping(value = "/{application}/comment", method = RequestMethod.POST)
    public String comment(Model model, @PathVariable String period, @PathVariable String application,
            @RequestParam String text) {
        Application app = saveApplication(model, period, application);
        applicationService.comment(app, text, "services");
        return "redirect:/application-tracking/" + period + "/applications/" + application;
    }


    @RequestMapping(value= "/{application}/email", method = RequestMethod.POST)
    public String email(Model model, @PathVariable String period, @PathVariable String application, @RequestParam String subject, @RequestParam String body){

        Application app = saveApplication(model, period, application);
        emailDispatchingService.send(app.getEmail(), subject, body, null);
        FenixFramework.atomic(() -> {
            ApplicationActivityLog.log(app,new LocalizedString().with(Locale.ENGLISH, "sent an email with the subject '" + subject + "'")
                    .with(new Locale("PT", "pt"), "enviou um email com assunto " + subject + " "), Authenticate.getUser(), null);
        });
        return  "redirect:/application-tracking/" + period + "/applications/" + application;
    }

    private Stream<Application> getApplications(Period period, String numbers) {
        if (Strings.isNullOrEmpty(numbers)) {
            return period.getApplicationSet().stream().sorted(Comparator.comparing(Application::getNumber));
        } else {
            return Arrays.stream(numbers.split(",")).map(period::findApplication).filter(Optional::isPresent).map(Optional::get);
        }
    }

}
