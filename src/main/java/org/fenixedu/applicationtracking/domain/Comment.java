package org.fenixedu.applicationtracking.domain;

public class Comment extends Comment_Base {
    public Comment(Application application, String text) {
        super();
        setApplication(application);
        setText(text);
    }

    @Override
    public ApplicationActivityLog getLog() {
        return super.getLog();
    }

    public void delete() {
        setApplication(null);
        deleteDomainObject();
    }
}
