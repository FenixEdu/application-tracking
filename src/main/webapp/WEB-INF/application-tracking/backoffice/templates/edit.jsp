<%--@elvariable id="period" type="org.fenixedu.applicationtracking.domain.Period"--%>
<%--@elvariable id="template" type="org.fenixedu.applicationtracking.domain.MessageTemplate"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h1><fmt:message key="label.message.templates" /></h1>
<ol class="breadcrumb">
	<li><a href="${pageContext.request.contextPath}/application-tracking"><fmt:message key="title.application.tracking" /></a></li>
	<li><a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a></li>
	<li><a href="${pageContext.request.contextPath}/application-tracking/${period.slug}/templates"><fmt:message key="label.message.templates" /></a></li>
	<li class="active"><fmt:message key="label.edit" /> - <c:out value="${template.templateKey}"/></li>
</ol>

<h3>
    <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}/templates">
        <c:out value="${period.name.content}"/>
    </a>
</h3>

<div class="row">
    <div class="col-sm-9">

        <form class="form-horizontal" method="POST">
            <div class="form-group">
                <label class="control-label col-sm-3"><fmt:message key="label.template.key" /></label>
                <div class="col-sm-9">
                    <code><c:out value="${template.templateKey}"/></code>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-3" for="name"><fmt:message key="label.name" /></label>
                <div class="col-sm-9">
                    <input type="text" name="name" id="name" class="form-control" value="<c:out value="${template.name}" />"
                           required/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-3" for="subject"><fmt:message key="label.subject" /></label>
                <div class="col-sm-9">
                    <input bennu-localized-string type="text" name="subject" id="subject" class="form-control"
                           value="<c:out value="${template.subject.json()}" />"/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-3" for="plainBody"><fmt:message key="label.body.plain" /></label>
                <div class="col-sm-9">
                    <textarea bennu-localized-string type="text" name="plainBody" id="plainBody" class="form-control"><c:out
                            value="${template.plainBody.json()}"/></textarea>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-3" for="body"><fmt:message key="label.body.html" /></label>
                <div class="col-sm-9">
                    <input bennu-localized-string bennu-html-editor type="text" name="body" id="body" class="form-control"
                           value="<c:out value="${template.body.json()}" />"/>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-9 col-sm-offset-3">
                    <button type="submit" class="btn btn-primary"><fmt:message key="label.edit" /></button>
                </div>
            </div>
        </form>
    </div>
    <div class="col-sm-3">
        <h5><fmt:message key="label.help" /></h5>
            <fmt:message key="help.templates.edit" />

        <hr/>
        <div class="well">
            <h5><fmt:message key="label.danger.zone" /></h5>
            <form method="POST"
                  action="${pageContext.request.contextPath}/application-tracking/${period.slug}/templates/${template.externalId}/delete">
                <button type="submit" class="btn btn-danger"><fmt:message key="label.delete" /></button>
            </form>
        </div>
    </div>
</div>

${portal.toolkit()}