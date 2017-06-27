package org.fenixedu.applicationtracking.domain;

import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;

public enum StateType {
    CANCELLED, DRAFT, SUBMITTED, ACCEPTED, REJECTED, PLACED, EXCLUDED;

    public LocalizedString getLocalizedName() {
        return BundleUtil.getLocalizedString("resources.ApplicationTrackingResources", StateType.class.getName() + "." + name());
    }

    public LocalizedString getLocalizedActionName() {
        return BundleUtil.getLocalizedString("resources.ApplicationTrackingResources", StateType.class.getName() + "." + name()
                + ".action");
    }

    public boolean canTransition(StateType state) {
        if (equals(CANCELLED)) {
            return true;
        }
        switch (state) {
        case DRAFT:
            return equals(SUBMITTED);
        case SUBMITTED:
            return equals(DRAFT) || equals(REJECTED) || equals(ACCEPTED);
        case REJECTED:
            return equals(SUBMITTED);
        case CANCELLED:
            return true;
        case ACCEPTED:
            return equals(SUBMITTED) || equals(PLACED) || equals(EXCLUDED);
        case PLACED:
            return equals(ACCEPTED);
        case EXCLUDED:
            return equals(ACCEPTED);
        default:
            return false;
        }
    }
}
