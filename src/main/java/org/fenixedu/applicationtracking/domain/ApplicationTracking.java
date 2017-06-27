
package org.fenixedu.applicationtracking.domain;

import java.util.Set;
import java.util.stream.Stream;

import org.fenixedu.applicationtracking.domain.form.fields.OptionsAdapter;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.groups.DynamicGroup;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class ApplicationTracking extends ApplicationTracking_Base {
    private ApplicationTracking() {
        super();
        setRoot(Bennu.getInstance());
        setPaymentInstructionsTemplate(new PaymentInstructionsTemplate());
        getPaymentInstructionsTemplate().setBody(new LocalizedString());
        setBackofficePersistentGroup(DynamicGroup.get("managers").toPersistentGroup());
    }

    public static ApplicationTracking getInstance() {
        if (Bennu.getInstance().getApplicationTracking() == null) {
            return initialize();
        }
        return Bennu.getInstance().getApplicationTracking();
    }

    @Atomic(mode = TxMode.WRITE)
    private static ApplicationTracking initialize() {
        if (Bennu.getInstance().getApplicationTracking() == null) {
            return new ApplicationTracking();
        }
        return Bennu.getInstance().getApplicationTracking();
    }

    public Set<Period> getAllPeriods() {
        return getPeriodSet();
    }

    public Stream<Period> getOpenPeriods() {
        return getPeriodSet().stream().filter(Period::isOpen);
    }

    public Stream<Period> getFuturePeriods() {
        return getPeriodSet().stream().filter(Period::isFuture);
    }

    public Stream<Period> getClosedPeriods() {
        return getPeriodSet().stream().filter(Period::isClosed);
    }

    public Set<Purpose> getPurposes() {
        return getPurposeSet();
    }

    public Set<Requirement> getRequirements() {
        return getRequirementSet();
    }

    public Group getBackofficeGroup() {
        return getBackofficePersistentGroup().toGroup();
    }

    public void setBackofficeGroup(Group backofficeGroup) {
        setBackofficePersistentGroup(backofficeGroup.toPersistentGroup());
    }

    public OptionsAdapter getOptionAdapters(String slug) {
        // TODO Auto-generated method stub
        return null;
    }

	public JsonElement getFieldValidatorsJson() {
		JsonObject result = new JsonObject();
		getFieldValidatorSet().stream().forEach(validator -> {
			result.add(validator.getSlug(), validator.json());
		});
		return result;
	}

}
