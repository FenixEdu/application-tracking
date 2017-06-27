package org.fenixedu.applicationtracking.ui;

import java.io.InputStreamReader;

import org.fenixedu.applicationtracking.domain.ApplicationTracking;
import org.fenixedu.applicationtracking.domain.Form;
import org.fenixedu.applicationtracking.service.SignupService;
import org.fenixedu.applicationtracking.servlet.ApplicationTrackingBootstrapper;
import org.fenixedu.bennu.core.security.Authenticate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@Controller
@RequestMapping("/form")
public class FormDummyController extends BaseApplicationController {

    private static final JsonParser PARSER = new JsonParser();

    @Autowired
    SignupService signupService;

    @RequestMapping
    public String showDynamicForm(Model model) {
        JsonElement json =
                PARSER.parse(new InputStreamReader(ApplicationTrackingBootstrapper.class.getResourceAsStream("/form.json")));

        Form form = Form.fromJson(json);

        model.addAttribute("application", form.json());
        if (Authenticate.isLogged()) {
            signupService.findApplicationForUser(ApplicationTracking.getInstance().getAllPeriods().iterator().next(),
                    Authenticate.getUser()).ifPresent(app -> model.addAttribute("url", publicUrlForApplicant(app)));
        }
        return "application-tracking/form";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String submitDynamicForm(Model model) {
        return "application-tracking/gracias";
    }

}
