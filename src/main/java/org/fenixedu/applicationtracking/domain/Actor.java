package org.fenixedu.applicationtracking.domain;

import java.util.UUID;

import org.fenixedu.commons.i18n.I18N;

public class Actor extends Actor_Base {

    public Actor(Application application, String actorName, String actorType) {
        super();
        setApplication(application);
        setActorName(actorName);
        setActorType(actorType);
        setLocale(I18N.getLocale());
        generateSecret();
    }

    @Override
    public Application getApplication() {
        return super.getApplication();
    }

    @Override
    public String getActorName() {
        return super.getActorName();
    }

    @Override
    public String getActorType() {
        return super.getActorType();
    }

    @Override
    public String getSecret() {
        return super.getSecret();
    }

    public void generateSecret() {
        setSecret(UUID.randomUUID().toString());
    }

    public void delete() {
        setApplication(null);
        setApplicationAsApplicant(null);
        for (ApplicationActivityLog log : getLogSet()) {
            log.delete();
        }
        deleteDomainObject();
    }

}
