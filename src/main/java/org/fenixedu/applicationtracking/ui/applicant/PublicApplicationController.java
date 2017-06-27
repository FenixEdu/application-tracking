package org.fenixedu.applicationtracking.ui.applicant;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fenixedu.applicationtracking.domain.*;
import org.fenixedu.applicationtracking.service.*;
import org.fenixedu.applicationtracking.ui.BaseApplicationController;
import org.fenixedu.commons.stream.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import pt.ist.fenixframework.FenixFramework;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/public-applications/{period}/application/{application}")
public class PublicApplicationController extends BaseApplicationController {

    @Autowired
    ApplicationService applicationService;

    @Autowired
    PaymentInstructionsService paymentInstructionsService;

    @Autowired
    ApplicationCertificateService applicationCertificateService;

    @Autowired
    FormService formService;
    
    @Autowired
    SlotService slotService;
    
    @RequestMapping
    public String home(Model model, @PathVariable String period, @PathVariable String application) {
        Actor actor = saveActor(model, period, application);

        JsonElement slotSelectionPossibilities = slotService.getSlotExternalRepresentation(actor.getApplication());

        JsonElement slotSelection = slotService.getSlotSelectionExternalRepresentation(actor.getApplication());

        JsonObject slots = new JsonObject();

        slots.add("selected", slotSelection);
        slots.add("available", slotSelectionPossibilities);
        slots.addProperty("maxSlots", actor.getApplication().getPeriod().getPurposeConfiguration().getMaxSlotsPerApplication());

        JsonArray forms = actor.getApplication().getApplicationSteps().stream().map(x -> formService.getFormExternalRepresentation(x))
                .collect(StreamUtils.toJsonArray());

        JsonObject fAnswer = new JsonObject();
        
        actor.getApplication().getApplicationSteps().forEach(x -> fAnswer.add(x.getFormStep().getSlug(), formService.getFormAnswerExternalRepresentation(x)));
        
        JsonObject steps = new JsonObject();

        steps.add("forms", forms);
        steps.add("answers", fAnswer);

        JsonObject status = new JsonObject();

        status.add("slots", slots);
        status.add("steps", steps);

        model.addAttribute("applicationUrl", publicUrlFor(actor));
        model.addAttribute("status", status);
        model.addAttribute("validators", ApplicationTracking.getInstance().getFieldValidatorsJson());
        try {
            model.addAttribute("paymentInstructions", paymentInstructionsService.getPaymentDetails(actor.getApplication(), actor.getLocale()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (actor.getApplication().getState()) {
        case DRAFT:
            return "application-tracking/application/interface";
        case SUBMITTED:
        case ACCEPTED:
        case EXCLUDED:
        case PLACED:
        case REJECTED:
            return "application-tracking/application/submited";
        case CANCELLED:
            return "application-tracking/application/canceled";
        default:
            throw new RuntimeException("¯\\_(ツ)_/¯");
        }

    }

    @ResponseBody
    @RequestMapping(value = "/files", method = RequestMethod.POST)
    public ResponseEntity<String> uploadFile(@PathVariable String period, @PathVariable String application,
            @RequestParam MultipartFile file) throws Exception {
        Actor actor = extractActor(extractPeriod(period), application);

        if (file.getSize() > maxFileSizeFor(actor)) {
            return new ResponseEntity<>(HttpStatus.REQUEST_ENTITY_TOO_LARGE);
        }

        byte[] content = file.getBytes();
        String name = file.getOriginalFilename();

        ApplicationFile applicationFile =
                FenixFramework.atomic(() -> new ApplicationFile(actor.getApplication(), name, name, content));
        return new ResponseEntity<>(describeFile(applicationFile).toString(), HttpStatus.OK);
    }

    private long maxFileSizeFor(Actor actor) {
        return 10 * 1024 * 1024;
    }

    private JsonObject describeFile(ApplicationFile file) {
        JsonObject json = new JsonObject();
        json.addProperty("id", file.getId());
        json.addProperty("filename", file.getDisplayName());
        json.addProperty("downloadLink", file.getBackOfficeDownloadUrl());
        json.addProperty("confirmed", file.isConfirmed());
        return json;
    }

    @ResponseBody
    @RequestMapping("/files/{file}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String period, @PathVariable String application,
            @PathVariable String file) {
        Actor actor = extractActor(extractPeriod(period), application);
        Optional<ApplicationFile> foundFile = actor.getApplication().findFile(file);
        if (!foundFile.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ApplicationFile f = foundFile.get();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", f.getContentType());
        headers.setContentLength(f.getSize());
        headers.set("Content-Disposition", "attachment; filename=\"" + f.getFilename() + "\"");
        return new ResponseEntity<>(f.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/files/{file}")
    public ResponseEntity<Void> deleteUnconfirmedFile(@PathVariable String period, @PathVariable String application,
            @PathVariable String file) {
        Actor actor = extractActor(extractPeriod(period), application);
        Optional<ApplicationFile> foundFile = actor.getApplication().findFile(file);
        foundFile.ifPresent(f -> {
            if (!f.isConfirmed()) {
                FenixFramework.atomic(f::delete);
            }
        });
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ResponseBody
    @RequestMapping(value = "/answer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String answer(Model model, @PathVariable String period, @PathVariable String application, @RequestBody String string) {
        Actor actor = saveActor(model, period, application);

        if (actor.getApplication().getState() == StateType.DRAFT) {
            saveAnswers(actor, string);

            return "{ \"result\" : \"ok\" }";
        } else {
            throw ApplicationTrackingDomainException.editionNotAllowed("Not in draft");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/submit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String submit(Model model, @PathVariable String period, @PathVariable String application, @RequestBody String string) {
        Actor actor = saveActor(model, period, application);

        if (actor.getApplication().getState() == StateType.DRAFT) {
            saveAnswers(actor, string);

            applicationService.submitByApplicant(actor.getApplication());

            return "{ \"result\" : \"ok\" }";
        } else {
            throw ApplicationTrackingDomainException.editionNotAllowed("Not in draft");
        }
    }

    private void saveAnswers(Actor actor, String answers) {
        JsonObject jsonAnswers = new JsonParser().parse(answers).getAsJsonObject();

        List<Slot> choices = slotService.fromSlotFromExternalRepresentation(jsonAnswers.get("slots"), actor.getApplication());

                List<Runnable> doLater = new ArrayList<>();
        for (int i = 0; i < actor.getApplication().getApplicationSteps().size(); i++) {
            ApplicationStep applicationStep = actor.getApplication().getApplicationSteps().get(i);
            JsonElement formAnswer = jsonAnswers.get("formsAnswer").getAsJsonArray().get(i);
            
            if (!formAnswer.isJsonNull()) {
                FormStepAnswer answer = formService.fromExternalRepresentation(formAnswer.getAsJsonObject());
                doLater.add(() -> applicationStep.setAnswer(answer));
            } else {
                doLater.add(() -> applicationStep.setAnswer(null));
            }
        }

        FenixFramework.atomic(() -> {
            actor.getApplication().getPurposeFulfilment().setSlotSelection(choices);
            doLater.stream().forEach(Runnable::run);
        });
    }

    @ResponseBody
    @RequestMapping(value = "application-certificate")
    public void applicationCertificate(Model model, @PathVariable String period, @PathVariable String application,
            HttpServletResponse response)
            throws Exception {
        Actor actor = saveActor(model, period, application);
        applicationCertificateService.download(actor.getApplication(), actor.getLocale(), response);
    }

    @ResponseBody
    @RequestMapping(value = "/cancel", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public RedirectView cancel(Model model, @PathVariable String period, @PathVariable String application) {
        Actor actor = saveActor(model, period, application);

        if (actor.getApplication().getState() == StateType.SUBMITTED) {
            
            applicationService.cancelByApplicant(actor.getApplication());

            return new RedirectView(publicUrlFor(actor));
        } else {
            throw ApplicationTrackingDomainException.editionNotAllowed("You need to be in state submited for ");
        }
    }

}
