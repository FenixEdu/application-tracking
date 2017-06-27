package org.fenixedu.applicationtracking.domain;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;

public class Period extends Period_Base {
    public Period(Purpose purpose, String slug, LocalizedString name, LocalizedString description, LocalDateTime start,
            LocalDateTime end) {
        super();
        setRoot(ApplicationTracking.getInstance());
        setSlug(slug);
        setName(name);
        setDescription(description);
        editDates(start, end);
        setSignatureKey(generateSignatureKey());
        new PurposeConfiguration(this, purpose);
    }

    private byte[] generateSignatureKey() {
        byte[] bytes = new byte[128];
        new SecureRandom().nextBytes(bytes);
        return bytes;
    }

    @Override
    public byte[] getSignatureKey() {
        return super.getSignatureKey();
    }

    public boolean isOpen() {
        return isOpen(LocalDateTime.now());
    }

    public boolean isOpen(LocalDateTime when) {
        return !getStart().isAfter(when) && !getEnd().isBefore(when);
    }

    public boolean isClosed() {
        return isClosed(LocalDateTime.now());
    }

    public boolean isClosed(LocalDateTime when) {
        return getEnd().isBefore(when);
    }

    public boolean isFuture() {
        return isFuture(LocalDateTime.now());
    }

    public boolean isFuture(LocalDateTime when) {
        return getStart().isAfter(when);
    }

    @Override
    public LocalDateTime getStart() {
        return super.getStart();
    }

    @Override
    public LocalDateTime getEnd() {
        return super.getEnd();
    }

    @Override
    public void setSlug(String slug) {
        super.setSlug(slug);
        ensureUniqueSlug();
    }

    private void ensureUniqueSlug() {
        for (Period period : ApplicationTracking.getInstance().getAllPeriods()) {
            if (period != this && getSlug().equals(period.getSlug())) {
                throw ApplicationTrackingDomainException.duplicatedSlug(getSlug());
            }
        }
    }

    public static Optional<Period> fromSlug(String slug) {
        return ApplicationTracking.getInstance().getAllPeriods().stream().filter(period -> slug.equals(period.getSlug()))
                .findAny();
    }

    @Override
    public Set<Application> getApplicationSet() {
        return Collections.unmodifiableSet(super.getApplicationSet());
    }

    public Optional<Application> findApplication(String number) {
        return getApplicationSet().stream().filter(app -> app.getNumber().equals(number)).findAny();
    }

    public Optional<Application> findApplicationByEmail(String email) {
        return getApplicationSet().stream().filter(app -> app.getEmail().equals(email)).findAny();
    }

    public MessageTemplate templateForKey(String key) {
        return getTemplateSet().stream().filter(template -> template.getTemplateKey().equals(key)).findAny()
                .orElseThrow(() -> ApplicationTrackingDomainException.missingTemplate(key));
    }

    @Override
    public PurposeConfiguration getPurposeConfiguration() {
        return super.getPurposeConfiguration();
    }

    @Override
    public void setPurposeConfiguration(PurposeConfiguration purposeConfiguration) {
        if (super.getPurposeConfiguration() != purposeConfiguration) {
            super.getPurposeConfiguration().delete();
            super.setPurposeConfiguration(purposeConfiguration);
        }
    }

    public Purpose getPurpose() {
        return getPurposeConfiguration().getPurpose();
    }

    public List<RequirementConfiguration> getRequirementConfigurations() {
        return getRequirementConfigurationSet().stream().sorted().collect(Collectors.toList());
    }

    public void reorderRequirements(List<RequirementConfiguration> requirementConfigurations) {
        for (RequirementConfiguration current : getRequirementConfigurationSet()) {
            if (!requirementConfigurations.contains(current)) {
                current.delete();
            }
        }
        getRequirementConfigurationSet().clear();
        for (int i = 0; i < requirementConfigurations.size(); i++) {
            RequirementConfiguration requirementConfiguration = requirementConfigurations.get(i);
            requirementConfiguration.setOrder(i);
            addRequirementConfiguration(requirementConfiguration);
        }
    }

    public BigDecimal getTotalRequirementWeight() {
        return getRequirementConfigurationSet().stream().map(RequirementConfiguration::getWeigth)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isTotalRequirementWeightValid() {
        BigDecimal w = getTotalRequirementWeight();
        return w.compareTo(BigDecimal.ONE) == 0 || w.compareTo(BigDecimal.ZERO) == 0;
    }

    @Atomic
    public static Period copyFrom(Period template, String slug, LocalizedString name, LocalDateTime start, LocalDateTime end) {
        Period period = new Period(template.getPurpose(), slug, name, name, start, end);

        period.getPurposeConfiguration()
                .setMaxSlotsPerApplication(template.getPurposeConfiguration().getMaxSlotsPerApplication());

        if (template.getPurposeConfiguration().getApplicationFee() != null) {
            new ApplicationFee(period.getPurposeConfiguration(),
                    template.getPurposeConfiguration().getApplicationFee().getAmount(),
                    template.getPurposeConfiguration().getApplicationFee().getPerSlot());
        }

        period.getPurposeConfiguration().setForm(template.getPurposeConfiguration().getForm().orElse(null));

        for (Slot slot : template.getPurposeConfiguration().getSlotSet()) {
            period.getPurposeConfiguration().addSlot(slot.deepClone());
        }

        for (MessageTemplate messageTemplate : template.getTemplateSet()) {
            new MessageTemplate(period, messageTemplate.getTemplateKey(), messageTemplate.getName(), messageTemplate.getSubject(),
                    messageTemplate.getPlainBody(), messageTemplate.getBody());
        }

        for (RequirementConfiguration configuration : template.getRequirementConfigurations()) {
            RequirementConfiguration config = new RequirementConfiguration(period, configuration.getRequirement(),
                    configuration.getWeigth());
            config.setForm(configuration.getForm().orElse(null));

            // TODO Copy recomendation configuration
        }

        return period;
    }

    @Atomic
    public static Period createNew(Purpose purpose, String slug, LocalizedString name, LocalDateTime start, LocalDateTime end) {
        Period period = new Period(purpose, slug, name, name, start, end);
        return period;
    }

    public void editDates(LocalDateTime start, LocalDateTime end) {
        // TODO Implement validation logic
        setStart(start);
        setEnd(end);
    }

    public void calculateSeriation() {
        // clear previous calculation
        getApplicationSet().stream().filter(a -> a.getState().equals(StateType.ACCEPTED)).forEach(a -> a.setPlacement(null));
        // recalculate
        getApplicationSet().stream().filter(a -> a.getState().equals(StateType.ACCEPTED))
                .sorted(Comparator.comparing(Application::calculateRank).reversed()).map(Application::getPurposeFulfilment)
                .forEachOrdered(PurposeFulfilment::calculatePlacement);
    }

    public void applySeriation() {
        getApplicationSet().stream().filter(a -> a.getState().equals(StateType.ACCEPTED)).forEach(a -> {
            if (a.getPlacement() != null) {
                a.changeState(StateType.PLACED);
            } else {
                a.changeState(StateType.EXCLUDED);
            }
        });
    }

    public void delete() {
        ApplicationTrackingDomainException.throwWhenDeleteBlocked(getDeletionBlockers());
        for (RequirementConfiguration requirementConfig : getRequirementConfigurationSet()) {
            for (RequirementFulfilment requirementFulfilment : requirementConfig.getRequirementFulfilmentSet()) {
                requirementFulfilment.delete();
            }
            requirementConfig.delete();
        }
        for (Application application : getApplicationSet()) {
            application.delete();
        }
        for (MessageTemplate template : getTemplateSet()) {
            template.delete();
        }
        for (PurposeFulfilment purposeFulfilment : getPurposeConfiguration().getPurposeFulfilmentSet()) {
            purposeFulfilment.delete();
        }
        setPurposeConfiguration(null);
        setRoot(null);
        deleteDomainObject();
    }
}
