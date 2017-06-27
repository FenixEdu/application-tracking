package org.fenixedu.applicationtracking.servlet;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.fenixedu.applicationtracking.ApplicationTrackingConfiguration;
import org.fenixedu.applicationtracking.domain.*;
import org.fenixedu.commons.i18n.LocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import javax.money.Monetary;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * For demo purposes only.
 *
 * Delete before final release.
 *
 * @author jpc
 *
 */
@WebListener
public class ApplicationTrackingBootstrapper implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationTrackingBootstrapper.class);

    private static final JsonParser PARSER = new JsonParser();
    private static final Locale PT = Locale.forLanguageTag("pt-PT");
    private static final Locale EN = Locale.forLanguageTag("en-GB");

    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }

    @Override
    @Atomic(mode = TxMode.READ)
    public void contextInitialized(ServletContextEvent event) {
        if (ApplicationTracking.getInstance().getAllPeriods().isEmpty()) {
        //    initialize();
        }
    }

    @Atomic(mode = TxMode.WRITE)
    private void initialize() {
        if (ApplicationTracking.getInstance().getAllPeriods().isEmpty()) {
            logger.info("Creating test data for the Application Tracking module");

            // Create Purpose
            Purpose purpose =
                    new Purpose(new LocalizedString(EN, "Program Application").with(PT, "Candidatura a Cursos"),
                            new LocalizedString(EN, "Program Application <insert description here>").with(PT,
                                    "Candidatura a Cursos <inserir descrição aqui>"));

            // Create basic period data
            Period period =
                    new Period(purpose, "2nd-cycle-1617", new LocalizedString(EN, "2nd Cycle 2016/2017").with(PT,
                            "2º Ciclo 2016/2017"),
                            new LocalizedString(EN, "Applications to 2nd Cycle Programs for 2016/2017").with(PT,
                                    "Candidaturas a Cursos de 2º Ciclo para 2016/2017"), LocalDateTime.of(2015, 1, 1, 0, 0),
                            LocalDateTime.of(2017, 1, 1, 0, 0));

            // Create PurposeConfiguration
            period.getPurposeConfiguration().setMaxSlotsPerApplication(5);
            period.getPurposeConfiguration().setForm(createForm("/bio.json"));

            
            // Create a Slot Type Tree
            SlotType programType = new SlotType(new LocalizedString(EN, "Program").with(PT, "Programa"), "program");
            
            SlotType scienceAreaType = new SlotType(new LocalizedString(EN, "Science Area").with(PT, "Area Scientifica"), "scienceArea", programType);
            
            SlotType courseType = new SlotType(new LocalizedString(EN, "Courses").with(PT, "Cursos"), "courses", scienceAreaType);

            period.getPurposeConfiguration().setSlotTypeRoot(programType);
            
            
            // Create Slots Slots 
            Slot erasmus = new Slot(programType,new LocalizedString(EN,
                    "Erasmus Program").with(PT,
                    "Programa Erasmus"), "erasmus", 20, period.getPurposeConfiguration());
            
            
            Slot economy  = new Slot(scienceAreaType, new LocalizedString(EN,
                    "Economy").with(PT,
                    "Economomia"), "economy", 20, erasmus);
            
            Slot humanities = new Slot(scienceAreaType, new LocalizedString(EN,
                    "Humanidades").with(PT,
                    "Humanities"), "humanities", 20, erasmus);
            
            
            Slot finance = new Slot(courseType,new LocalizedString(EN, 
                    "Bologna Master Degree in Finance").with(PT,
                    "Mestrado Bolonha em Finança"), "meic", 20, economy);
            
            Slot philosophy = new Slot(courseType,new LocalizedString(EN, 
                    "Bologna Master Degree in Philosophy").with(PT,
                    "Mestrado Bolonha em Filosofia"), "merc", 5,humanities);
            
            
            Slot epfl = new Slot(programType, new LocalizedString(EN,
                    "EPFL").with(PT,
                    "EPFL"), "EPFL", 20, period.getPurposeConfiguration());
            
            Slot software  = new Slot(scienceAreaType, new LocalizedString(EN,
                    "Software").with(PT,
                    "Software"), "software", 20, epfl);
            
            Slot hardware = new Slot(scienceAreaType, new LocalizedString(EN,
                    "Hardware").with(PT,
                    "Hardware"), "hardware", 20, epfl);
            
            
            
            Slot meic = new Slot(courseType,new LocalizedString(EN, 
                    "Bologna Master Degree in Information Systems and Computer Engineering").with(PT,
                    "Mestrado Bolonha em Engenharia Informática e de Computadores"), "meic", 20, software);
            
            Slot merc = new Slot(courseType,new LocalizedString(EN, 
                    "Bologna Master Degree in Telecommunications and Informatics Engineering").with(PT,
                    "Mestrado Bolonha em Engenharia de Telecomunicações e Informática"), "merc", 5,hardware);
            
            Slot meec = new Slot(courseType,new LocalizedString(PT, 
                    "Mestrado Integrado em Engenharia Electrotécnica e de Computadores"), "meec", hardware);
            
            
            // Create ApplicationFee

            String currency = ApplicationTrackingConfiguration.getConfiguration().currency();
            new ApplicationFee(period.getPurposeConfiguration(), Monetary.getDefaultAmountFactory().setNumber(100).setCurrency(currency).create(), true);

            // Create Requirements
            Requirement requirement2 =
                    new Requirement(new LocalizedString(EN, "Skills").with(PT, "Habilitações"),
                            new LocalizedString(EN, "Skills <insert description here>").with(PT,
                                    "Habilitações <inserir descrição aqui>"));
            RequirementConfiguration reqConf2 = new RequirementConfiguration(period, requirement2, BigDecimal.valueOf(.5));
            reqConf2.setForm(createForm("/skills.json"));

            Requirement requirement1 =
                    new Requirement(new LocalizedString(EN, "CV").with(PT, "CV"),
                            new LocalizedString(EN, "Curriculum Vitae").with(PT, "Curriculum Vitae"));
            RequirementConfiguration reqConf1 = new RequirementConfiguration(period, requirement1, BigDecimal.valueOf(.5));
            reqConf1.setForm(createForm("/cv.json"));


            // Create MessageTemplates
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

            logger.info("Created Period '2nd-cycle-1617'");

            populateValidators();
        }
    }

    private Form createForm(String file) {
        try {
            return Form.fromJson(
                    PARSER.parse(new InputStreamReader(ApplicationTrackingBootstrapper.class.getResourceAsStream(file))));
        } catch (JsonIOException | JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private FormAnswer createFormAnswer(Form form) {
        try {
            JsonElement json =
                    PARSER.parse(new InputStreamReader(ApplicationTrackingBootstrapper.class.getResourceAsStream("/answer.json")));
            return FormAnswer.fromJson(json);
        } catch (JsonIOException | JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void populateValidators() {
        LocalizedString nifTitle = new LocalizedString(Locale.ENGLISH, "NIF Validator");
        LocalizedString nifDescription = new LocalizedString(Locale.ENGLISH, "This validator verifies if the NIF is valid");
        LocalizedString nifErrorMessage = new LocalizedString(Locale.ENGLISH, "Your NIF is invalid");

        String nifJS = "function validate(value) { print('Validating '+value); return value === 1231231313 ; }";
        FieldValidator.create("nif-validator", nifTitle, nifDescription, nifErrorMessage, nifJS);

        LocalizedString emailTitle = new LocalizedString(Locale.ENGLISH, "Email Validator");
        LocalizedString emailDescription =
                new LocalizedString(Locale.ENGLISH, "This validator verifies if the input is a valid email");
        LocalizedString emailErrorMessage = new LocalizedString(Locale.ENGLISH, "Your email is invalid");

        String emailJS = "function validate(value) { print('Validating '+value); return value.indexOf('@') > -1; }";
        FieldValidator.create("email-validator", emailTitle, emailDescription, emailErrorMessage, emailJS);
    }

}
