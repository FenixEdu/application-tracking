package org.fenixedu.applicationtracking.ui.backoffice;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.fenixedu.applicationtracking.domain.*;
import org.fenixedu.applicationtracking.service.FormService;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.ist.fenixframework.FenixFramework;

@RequestMapping("/application-tracking/forms")
@BennuSpringController(ApplicationTrackingController.class)
public class FormEditingController {

    @Autowired
    FormService formService;

    @RequestMapping(value="/{oid}", method= RequestMethod.GET)
    public String view(Model model, @PathVariable String oid){
        Object obj = FenixFramework.getDomainObject(oid);

        if (obj instanceof PurposeConfiguration){
            model.addAttribute("obj", obj);
            model.addAttribute("form", formService.getFormSpec((PurposeConfiguration) obj));
        }

        if (obj instanceof Purpose){
            model.addAttribute("obj", obj);
            model.addAttribute("form", formService.getFormSpec((Purpose) obj));
        }

        if (obj instanceof RequirementConfiguration){
            model.addAttribute("obj", obj);
            model.addAttribute("form", formService.getFormSpec((RequirementConfiguration) obj));
        }

        if (obj instanceof Requirement){
            model.addAttribute("obj", obj);
            model.addAttribute("form", formService.getFormSpec((Requirement) obj));
        }
        
        model.addAttribute("validators", ApplicationTracking.getInstance().getFieldValidatorsJson());

        return "application-tracking/formEditing/home";
    }

    @RequestMapping(value="/{oid}", method= RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String save(Model model, @PathVariable  String oid, @RequestBody String formJson){
        Object obj = FenixFramework.getDomainObject(oid);
        Form form = formService.fromFormSpec(new JsonParser().parse(formJson));

        if (obj instanceof PurposeConfiguration){
            FenixFramework.atomic(() -> ((PurposeConfiguration) obj).setForm(form));
        }

        if (obj instanceof RequirementConfiguration){
            FenixFramework.atomic(() -> ((RequirementConfiguration) obj).setForm(form));
        }

        if (obj instanceof Purpose){
            FenixFramework.atomic(() -> ((Purpose) obj).setForm(form));
        }

        if (obj instanceof Requirement){
            FenixFramework.atomic(() -> ((Requirement) obj).setForm(form));
        }

        return "{ \"result\" : \"ok\" }";
    }
}

