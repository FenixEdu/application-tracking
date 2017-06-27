<%--@elvariable id="period" type="org.fenixedu.applicationtracking.domain.Period"--%>
<%--@elvariable id="requirementConfiguration" type="org.fenixedu.applicationtracking.domain.RequirementConfiguration"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h1><fmt:message key="label.requirement.configure" /></h1>
<ol class="breadcrumb">
	<li><a href="${pageContext.request.contextPath}/application-tracking"><fmt:message key="title.application.tracking" /></a></li>
	<li><a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a></li>
	<li class="active"><c:out value="${requirementConfiguration.requirement.name.content}"/></li>
</ol>
<h3>
    <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a>
    - <c:out value="${requirementConfiguration.requirement.name.content}"/>
</h3>

<div class="row">
    <div class="col-sm-8">
        <form class="form-horizontal" method="POST">
            <div class="form-group">
                <label class="control-label col-sm-3" for="weight"><fmt:message key="label.weight" /></label>
                <div class="col-sm-2">
                    <input type="number" step="any" name="weight" id="weight" class="form-control"
                           value="${requirementConfiguration.weigth}" required/>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-3 col-sm-9">
                    <button type="submit" class="btn btn-primary"><fmt:message key="label.save" /></button>
                </div>
            </div>
        </form>
    </div>
    <div class="col-sm-4">
        <fmt:message key="help.requirement" />

        <hr/>
        <div class="well">
            <h5><fmt:message key="label.danger.zone" /></h5>
            <form method="POST"
                  action="${pageContext.request.contextPath}/application-tracking/${period.slug}/requirements/${requirementConfiguration.externalId}/delete">
                <button type="submit" class="btn btn-danger"><fmt:message key="label.delete" /></button>
            </form>
        </div>
    </div>
</div>
