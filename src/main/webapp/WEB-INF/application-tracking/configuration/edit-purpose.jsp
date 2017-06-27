<%--@elvariable id="purpose" type="org.fenixedu.applicationtracking.domain.Purpose"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h1><fmt:message key="label.purpose.edit" /></h1>
<ol class="breadcrumb">
<li><a href="${pageContext.request.contextPath}/application-tracking"><fmt:message key="title.application.tracking" /></a></li>
<li><a href="${pageContext.request.contextPath}/application-tracking/configuration"><fmt:message key="title.application.tracking.configuration" /></a></li>
<li class="active"><fmt:message key="label.purpose.edit" /> - <c:out value="${purpose.name.content}"/></li>
</ol>
<h3><c:out value="${purpose.name.content}"/></h3>

<div class="row">
    <div class="col-sm-8">
        <form class="form-horizontal" method="POST">
            <div class="form-group">
                <label class="control-label col-sm-3" for="type"><fmt:message key="label.type" /></label>
                <div class="col-sm-9">
                    <input class="form-control" id="type" value="<c:out value="${purpose.getClass().name}" /> "
                           readonly/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-3" for="name"><fmt:message key="label.name" /></label>
                <div class="col-sm-9">
                    <input bennu-localized-string class="form-control" id="name" name="name"
                           value="<c:out value="${purpose.name.json()}" /> " required-any/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-3" for="description"><fmt:message key="label.description" /></label>
                <div class="col-sm-9">
                    <input bennu-localized-string class="form-control" id="description" name="description"
                           value="<c:out value="${purpose.description.json()}" /> " required-any/>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-9 col-sm-offset-3">
                    <button type="submit" class="btn btn-primary"><fmt:message key="label.save" /></button>
                </div>
            </div>
        </form>
    </div>
    <div class="col-sm-4">
        <fmt:message key="help.purpose" />
        
        <hr/>
        <div class="well">
            <h5><fmt:message key="label.danger.zone" /></h5>
            <form method="POST"
                  action="${pageContext.request.contextPath}/application-tracking/configuration/purpose/${purpose.externalId}/delete">
                <button type="submit" class="btn btn-danger"><fmt:message key="label.delete" /></button>
            </form>
        </div>
    </div>
</div>

${portal.toolkit()}
