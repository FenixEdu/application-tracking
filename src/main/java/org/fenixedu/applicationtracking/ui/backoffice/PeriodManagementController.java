package org.fenixedu.applicationtracking.ui.backoffice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.money.Monetary;

import org.fenixedu.applicationtracking.ApplicationTrackingConfiguration;
import org.fenixedu.applicationtracking.domain.ApplicationFee;
import org.fenixedu.applicationtracking.domain.ApplicationTracking;
import org.fenixedu.applicationtracking.domain.HasSlots;
import org.fenixedu.applicationtracking.domain.Period;
import org.fenixedu.applicationtracking.domain.PurposeConfiguration;
import org.fenixedu.applicationtracking.domain.Requirement;
import org.fenixedu.applicationtracking.domain.RequirementConfiguration;
import org.fenixedu.applicationtracking.domain.Slot;
import org.fenixedu.applicationtracking.domain.SlotType;
import org.fenixedu.applicationtracking.ui.BaseApplicationController;
import org.fenixedu.bennu.spring.portal.BennuSpringController;
import org.fenixedu.commons.i18n.LocalizedString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import pt.ist.fenixframework.FenixFramework;

@RequestMapping("/application-tracking/{period}")
@BennuSpringController(ApplicationTrackingController.class)
public class PeriodManagementController extends BaseApplicationController {

    @RequestMapping
    public String home(Model model, @PathVariable String period) {
        savePeriod(model, period);
        model.addAttribute("applicationTracking", ApplicationTracking.getInstance());
        return "application-tracking/backoffice/period";
    }

    @RequestMapping(method = RequestMethod.POST)
    public RedirectView editPeriod(@PathVariable String period, @RequestParam LocalizedString name,
            @RequestParam String slug,
            @RequestParam LocalizedString description, @RequestParam int maxSlotsPerApplication,
            @RequestParam(required = false, defaultValue = "false") boolean chargeFee,
            @RequestParam(required = false, defaultValue = "0") double feeAmount,
            @RequestParam(required = false, defaultValue = "false") boolean feePerSlot,
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime end) {
        Period p = extractPeriod(period);
        FenixFramework.atomic(() -> {
            p.setName(name);
            p.setSlug(slug);
            p.setDescription(description);
            PurposeConfiguration configuration = p.getPurposeConfiguration();
            p.editDates(start, end);
            configuration.setMaxSlotsPerApplication(maxSlotsPerApplication);
            String currency = ApplicationTrackingConfiguration.getConfiguration().currency();
            if (chargeFee) {
                if (configuration.getApplicationFee() == null) {
                    new ApplicationFee(configuration, Monetary.getDefaultAmountFactory().setNumber(feeAmount).setCurrency(currency).create(), feePerSlot);
                } else {
                    configuration.getApplicationFee().setAmount(Monetary.getDefaultAmountFactory().setNumber(feeAmount).setCurrency(currency).create());
                    configuration.getApplicationFee().setPerSlot(feePerSlot);
                }
            } else {
                if (configuration.getApplicationFee() != null) {
                    configuration.getApplicationFee().delete();
                }
            }
        });
        return new RedirectView("/application-tracking/" + slug, true);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public RedirectView deletePeriod(@PathVariable String period) {
        Period p = extractPeriod(period);
        FenixFramework.atomic(() -> {
            p.delete();
        });
        return new RedirectView("/application-tracking", true);
    }

    @RequestMapping(value = "/slots", method = RequestMethod.POST)
    public RedirectView createSlot(@PathVariable String period, @RequestParam LocalizedString name, @RequestParam String code,
            @RequestParam(required = false, defaultValue = "false") boolean unlimitedVacancies,
            @RequestParam(required = false, defaultValue = "0") int vacancies, @RequestParam("parent") String parentCode) throws Exception {
        Period p = extractPeriod(period);
        Optional<Slot> parent = p.getPurposeConfiguration().getAllSlots().stream().filter(x -> x.getCode().equals(parentCode)).findFirst();
        SlotType type = parent.map(x -> x.getType()).map(x -> x.getChild()).orElse(p.getPurposeConfiguration().getSlotTypeRoot());
        Slot slot = FenixFramework.atomic(() -> {
            HasSlots paren = parent.isPresent() ? parent.get() : p.getPurposeConfiguration();
            if (unlimitedVacancies) {
                return new Slot(type, name, code, paren);
            } else {
                return new Slot(type, name, code, vacancies, paren);
            }
        });
        return new RedirectView("/application-tracking/" + period + "/slots/" + slot.getExternalId(), true);
    }

    @RequestMapping("/slots/{slot}")
    public String showSlot(Model model, @PathVariable String period, @PathVariable Slot slot) {
        Period p = savePeriod(model, period);
        if (p.getPurposeConfiguration().getSlotSet().contains(slot)) {
            model.addAttribute("slot", slot);
        }
        return "application-tracking/backoffice/slot";
    }

    @RequestMapping(value = "/slots/{slot}", method = RequestMethod.POST)
    public RedirectView editSlot(@PathVariable String period, @PathVariable Slot slot, @RequestParam LocalizedString name,
            @RequestParam String code, @RequestParam(required = false, defaultValue = "false") boolean unlimitedVacancies,
            @RequestParam(required = false, defaultValue = "0") int vacancies) {
        Period p = extractPeriod(period);
        FenixFramework.atomic(() -> {
            if (p.getPurposeConfiguration().getSlotSet().contains(slot)) {
                slot.setName(name);
                slot.setCode(code);
                if (unlimitedVacancies) {
                    slot.setUnlimitedVacancies();
                } else {
                    slot.setVacancies(vacancies);
                }
            }
        });
        return new RedirectView("/application-tracking/" + period, true);
    }

    @RequestMapping(value = "/slots/{slot}/delete", method = RequestMethod.POST)
    public RedirectView deleteSlot(@PathVariable String period, @PathVariable Slot slot) {
        Period p = extractPeriod(period);
        FenixFramework.atomic(() -> {
            if (p.getPurposeConfiguration().getSlotSet().contains(slot)) {
                slot.delete();
            }
        });
        return new RedirectView("/application-tracking/" + period, true);
    }

    @RequestMapping(value = "/requirements", method = RequestMethod.POST)
    public RedirectView createRequirementConfiguration(@PathVariable String period, @RequestParam Requirement requirement,
            @RequestParam BigDecimal weight) throws Exception {
        Period p = extractPeriod(period);
        RequirementConfiguration configuration =
                FenixFramework.atomic(() -> new RequirementConfiguration(p, requirement, weight));
        return new RedirectView("/application-tracking/" + period + "/requirements/" + configuration.getExternalId(), true);
    }

    @RequestMapping(value="/moveRequirement/{from}/{to}", method = RequestMethod.POST)
    @ResponseBody
    public String moveRequirement(Model model, @PathVariable String period, @PathVariable int from, @PathVariable int to) {
        Period p = extractPeriod(period);

        List<RequirementConfiguration> requirementConfigurations = p.getRequirementConfigurations();
        Collections.rotate(requirementConfigurations.subList(from, to+1), -1);
        FenixFramework.atomic(() -> p.reorderRequirements(requirementConfigurations));

        return "{ \"result\" : \"ok\" }";
    }

    @RequestMapping("/requirements/{requirementConfiguration}")
    public String showRequirement(Model model, @PathVariable String period,
            @PathVariable RequirementConfiguration requirementConfiguration) {
        Period p = savePeriod(model, period);
        if (p.getRequirementConfigurations().contains(requirementConfiguration)) {
            model.addAttribute("requirementConfiguration", requirementConfiguration);
        }
        return "application-tracking/backoffice/requirement";
    }

    @RequestMapping(value = "/requirements/{requirementConfiguration}", method = RequestMethod.POST)
    public RedirectView editRequirementConfiguration(@PathVariable String period,
            @PathVariable RequirementConfiguration requirementConfiguration, @RequestParam BigDecimal weight) {
        Period p = extractPeriod(period);
        if (p.getRequirementConfigurations().contains(requirementConfiguration)) {
            FenixFramework.atomic(() -> requirementConfiguration.setWeigth(weight));
        }
        return new RedirectView("/application-tracking/" + period, true);
    }

    @RequestMapping(value = "/requirements/{requirementConfiguration}/delete", method = RequestMethod.POST)
    public RedirectView deleteRequirementConfiguration(@PathVariable String period,
            @PathVariable RequirementConfiguration requirementConfiguration) {
        Period p = extractPeriod(period);
        if (p.getRequirementConfigurations().contains(requirementConfiguration)) {
            FenixFramework.atomic(requirementConfiguration::delete);
        }
        return new RedirectView("/application-tracking/" + period, true);
    }

    @RequestMapping(value = "/slotTypes", method = RequestMethod.GET)
    public String slotTypes(Model model, @PathVariable String period) throws Exception {
        Period p = extractPeriod(period);
        savePeriod(model, period);
        model.addAttribute("applicationTracking", ApplicationTracking.getInstance());
        model.addAttribute("slotType", p.getPurposeConfiguration().getSlotTypeRoot());
        return "application-tracking/backoffice/slotType";
    }

    @RequestMapping(value = "/slotTypes/new", method = RequestMethod.POST)
    public RedirectView slotTypesCreate(Model model, @PathVariable String period, @RequestParam LocalizedString name, @RequestParam String code) throws Exception {
        Period p = extractPeriod(period);
        FenixFramework.atomic(()->{
            if (p.getPurposeConfiguration().getSlotTypeRoot() == null){
                SlotType type = new SlotType(name,code);
                p.getPurposeConfiguration().setSlotTypeRoot(type);
            }else{
                SlotType t = p.getPurposeConfiguration().getSlotTypeRoot();
                while(t.getChild() != null){
                    t = t.getChild();
                }
                SlotType type = new SlotType(name,code,t);
            }
        });
        return new RedirectView("/application-tracking/" + period + "/slotTypes", true);
    }

    @RequestMapping(value = "/slotTypes/{oid}", method = RequestMethod.GET)
    public String slotTypesView(Model model, @PathVariable String period, @PathVariable("oid") SlotType type) throws Exception {
        Period p = extractPeriod(period);
        savePeriod(model, period);
        model.addAttribute("applicationTracking", ApplicationTracking.getInstance());
        model.addAttribute("slotType", type);
        return "application-tracking/backoffice/slotTypeEdit";
    }

    @RequestMapping(value = "/slotTypes/{oid}", method = RequestMethod.POST)
    public RedirectView slotTypesEdit(Model model, @PathVariable String period, @PathVariable("oid") SlotType type, @RequestParam LocalizedString name, @RequestParam String code) throws Exception {
        Period p = extractPeriod(period);

        FenixFramework.atomic(() -> {
            type.setName(name);
            type.setCode(code);
        });

        return new RedirectView("/application-tracking/" + period + "/slotTypes", true);
    }


    @RequestMapping(value = "/slotTypes/{oid}/delete", method = RequestMethod.POST)
    public RedirectView deleteSlotType(Model model, @PathVariable String period, @PathVariable("oid") SlotType type) throws Exception {
        Period p = extractPeriod(period);

        FenixFramework.atomic(() -> {
            type.delete();
        });

        return new RedirectView("/application-tracking/" + period + "/slotTypes", true);
    }

    @RequestMapping(value = "calculateSeriation", method = RequestMethod.POST)
    public String calculateSeriation(Model model, @PathVariable String period) {
        Period p = savePeriod(model, period);
        FenixFramework.atomic(() -> p.calculateSeriation());
        return "redirect:/application-tracking/" + period + "/applications";
    }

    @RequestMapping(value = "applySeriation", method = RequestMethod.POST)
    public String applySeriation(Model model, @PathVariable String period) {
        Period p = savePeriod(model, period);
        FenixFramework.atomic(() -> p.applySeriation());
        return "redirect:/application-tracking/" + period + "/applications";
    }
}
