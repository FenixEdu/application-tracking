package org.fenixedu.applicationtracking.domain;

import humanize.Humanize;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;

public class ApplicationActivityLog extends ApplicationActivityLog_Base implements Comparable<ApplicationActivityLog> {

    protected ApplicationActivityLog(Application application, User userWho, Actor actorWho, String role,
            LocalizedString customAction) {
        super();
        setApplication(application);
        setWhen(Instant.now());
        setUserWho(userWho);
        setActorWho(actorWho);
        setRole(role);
        setCustomAction(customAction);
    }

    @Override
    public Instant getWhen() {
        return super.getWhen();
    }

    public String getExtendedWhen() {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withZone(ZoneId.systemDefault()).format(getWhen());
    }

    public String getHumanReabadleWhen() {
        return Humanize.naturalTime(Date.from(getWhen()), I18N.getLocale());
    }

    @Override
    public User getUserWho() {
        return super.getUserWho();
    }

    @Override
    public Actor getActorWho() {
        return super.getActorWho();
    }

    public String getWho() {
        if (getUserWho() != null) {
            return getUserWho().getProfile().getDisplayName();
        } else {
            return getActorWho().getActorName();
        }
    }

    @Override
    public String getRole() {
        return super.getRole();
    }

    @Override
    public StateType getState() {
        return super.getState();
    }

    @Override
    public Comment getComment() {
        return super.getComment();
    }

    @Override
    public LocalizedString getCustomAction() {
        return super.getCustomAction();
    }

    public String getType() {
        if (getState() != null) {
            return "state";
        } else if (getComment() != null) {
            return "comment";
        } else {
            return "custom";
        }
    }

    public String getAction() {
        if (getState() != null) {
            return "set state to";
        } else if (getComment() != null) {
            return "made a comment";
        } else {
            return getCustomAction().getContent();
        }
    }

    public String getTarget() {
        if (getState() != null) {
            return getState().getLocalizedName().getContent();
        } else if (getComment() != null) {
            return getComment().getExternalId();
        } else {
            return "";
        }
    }

    public static ApplicationActivityLog logStateChange(Application application, StateType state, User who, String role) {
        ApplicationActivityLog log = new ApplicationActivityLog(application, who, null, role, null);
        log.setState(state);
        return log;
    }

    public static ApplicationActivityLog logStateChange(Application application, StateType state, Actor who, String role) {
        ApplicationActivityLog log = new ApplicationActivityLog(application, null, who, role, null);
        log.setState(state);
        return log;
    }

    public static ApplicationActivityLog logComment(Comment comment, User who, String role) {
        ApplicationActivityLog log = new ApplicationActivityLog(comment.getApplication(), who, null, role, null);
        log.setComment(comment);
        return log;
    }

    public static ApplicationActivityLog log(Application application, LocalizedString customAction, User who, String role) {
        return new ApplicationActivityLog(application, who, null, role, customAction);
    }

    public static ApplicationActivityLog log(Application application, LocalizedString customAction, Actor who, String role) {
        return new ApplicationActivityLog(application, null, who, role, customAction);
    }

    @Override
    public int compareTo(ApplicationActivityLog o) {
        int when = getWhen().compareTo(o.getWhen());
        return when != 0 ? when : getExternalId().compareTo(o.getExternalId());
    }

    public void delete() {
        setApplication(null);
        setUserWho(null);
        setActorWho(null);
        setComment(null);
        deleteDomainObject();
    }
}
