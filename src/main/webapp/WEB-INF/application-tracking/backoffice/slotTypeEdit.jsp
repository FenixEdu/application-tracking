<%--@elvariable id="period" type="org.fenixedu.applicationtracking.domain.Period"--%>
<%--@elvariable id="slotType" type="org.fenixedu.applicationtracking.domain.SlotType"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<h1><fmt:message key="label.slot.types.configure" /></h1>
<ol class="breadcrumb">
	<li><a href="${pageContext.request.contextPath}/application-tracking"><fmt:message key="title.application.tracking" /></a></li>
	<li><a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a></li>
	<li class="active"><fmt:message key="label.slot.types.configure" /></li>
</ol>
<h3>
    <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a> - <fmt:message key="label.slot.type" /> - ${slotType.name.content}
</h3>

<div class="row">
    <div class="col-sm-8">
<form class="form-horizontal" action="${pageContext.request.contextPath}/application-tracking/${period.slug}/slotTypes/${slotType.externalId}"
      method="POST">
    <div class="modal-body">
        <div class="form-group">
            <label class="control-label col-sm-3" name="name"><fmt:message key="label.name" /></label>
            <div class="col-sm-9">
                <input type="text" bennu-localized-string name="name" id="name" required-any value='${slotType.name.json()}'/>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-3" for="code"><fmt:message key="label.code" /></label>
            <div class="col-sm-4">
                <input type="text" name="code" id="code" class="form-control" required value="${slotType.code}"/>
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <button type="submit" class="btn btn-primary"><fmt:message key="label.edit" /></button>
    </div>
</form>
</div>
    <div class="col-sm-4">
        <fmt:message key="help.slot.type" />

        <hr/>
        <div class="well">
            <h5><fmt:message key="label.danger.zone" /></h5>
            <form method="POST"
                  action="${pageContext.request.contextPath}/application-tracking/${period.slug}/slotTypes/${slotType.externalId}/delete">
                <button type="submit" class="btn btn-danger"><fmt:message key="label.delete" /></button>
            </form>
        </div>
    </div>
</div>

${portal.toolkit()}