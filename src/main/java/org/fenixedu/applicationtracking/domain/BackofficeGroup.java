package org.fenixedu.applicationtracking.domain;

import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.GroupStrategy;
import org.joda.time.DateTime;

import java.util.Set;

@GroupOperator("backoffice")
public class BackofficeGroup extends GroupStrategy {
    @Override public String getPresentationName() {
        return "Backoffice Group";
    }

    @Override public Set<User> getMembers() {
        return ApplicationTracking.getInstance().getBackofficeGroup().getMembers();
    }

    @Override public Set<User> getMembers(DateTime when) {
        return ApplicationTracking.getInstance().getBackofficeGroup().getMembers(when);
    }

    @Override public boolean isMember(User user) {
        return ApplicationTracking.getInstance().getBackofficeGroup().isMember(user);
    }

    @Override public boolean isMember(User user, DateTime when) {
        return ApplicationTracking.getInstance().getBackofficeGroup().isMember(user, when);
    }
}
