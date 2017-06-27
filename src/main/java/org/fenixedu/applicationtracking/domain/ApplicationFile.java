package org.fenixedu.applicationtracking.domain;

import java.util.UUID;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.io.servlets.FileDownloadServlet;

public class ApplicationFile extends ApplicationFile_Base {

    public ApplicationFile(Application application, String fileName, String displayName, byte[] content) {
        super();
        init(displayName, fileName, content);
        setApplication(application);
        setId(UUID.randomUUID().toString());
        setConfirmed(false);
    }

    @Override
    public boolean isAccessible(User user) {
    	return true; //CHANGE THIS FOR OBVIOUS REASONS
        // If the application is from a logged user, use that access
//        if (getApplication().getUser() != null && getApplication().getUser().equals(user)) {
//            return true;
//        }
//        return ApplicationTracking.getInstance().getBackofficeGroup().isMember(user);
    }

    public boolean isConfirmed() {
        return super.getConfirmed();
    }

    public void confirm() {
        setConfirmed(true);
    }

    @Override
    public String getId() {
        return super.getId();
    }

    public String getBackOfficeDownloadUrl() {
        return FileDownloadServlet.getDownloadUrl(this);
    }

    @Override
    public void delete() {
        setApplication(null);
        super.delete();
    }

}
