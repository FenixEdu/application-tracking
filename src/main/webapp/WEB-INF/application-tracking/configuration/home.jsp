<%--@elvariable id="applicationTracking" type="org.fenixedu.applicationtracking.domain.ApplicationTracking"--%>
<%--@elvariable id="paymentInstructionsTemplate" type="org.fenixedu.applicationtracking.domain.PaymentInstructionsTemplate"--%>
<%--@elvariable id="purposeTypes" type="java.util.List<String>"--%>
<%--@elvariable id="requirementTypes" type="java.util.List<String>"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h1><fmt:message key="title.application.tracking.configuration" /></h1>
<ol class="breadcrumb">
	<li><a href="${pageContext.request.contextPath}/application-tracking"><fmt:message key="title.application.tracking" /></a></li>
	<li class="active"><fmt:message key="title.application.tracking.configuration" /></li>
</ol>

<!-- Nav tabs -->
  <ul class="nav nav-tabs" role="tablist">
    <li role="presentation" class="active"><a href="#groups" aria-controls="messages" role="tab" data-toggle="tab"><fmt:message key="label.backoffice.group" /></a></li>
    <li role="presentation"><a href="#payment" aria-controls="profile" role="tab" data-toggle="tab"><fmt:message key="label.payment.instructions"/></a></li>
    <li role="presentation"><a href="#purpose" aria-controls="home" role="tab" data-toggle="tab"><fmt:message key="label.purposes" /></a></li>
    <li role="presentation"><a href="#requirements" aria-controls="profile" role="tab" data-toggle="tab"><fmt:message key="label.requirements" /></a></li>
    <li role="presentation"><a href="#validators" aria-controls="settings" role="tab" data-toggle="tab"><fmt:message key="label.validators" /></a></li>
  </ul>


<div class="tab-content">
    <div role="tabpanel" class="tab-pane" id="purpose">
        <h3><fmt:message key="label.purposes" /></h3>
        <p>
            <button class="btn btn-default" data-toggle="modal" data-target="#newPurposeModal">
                <span class="glyphicon glyphicon-plus"></span>
                <fmt:message key="label.new" />
            </button>
        </p>
        <c:choose>
            <c:when test="${empty applicationTracking.purposes}">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <fmt:message key="message.purposes.none" />
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <table class="table">
                    <thead>
                        <tr>
                            <th><fmt:message key="label.name" /></th>
                            <th><fmt:message key="label.description" /></th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="purpose" items="${applicationTracking.purposes}">
                            <tr>
                                <td><c:out value="${purpose.name.content}"/></td>
                                <td><c:out value="${purpose.description.content}"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/application-tracking/configuration/purpose/${purpose.externalId}"><fmt:message key="label.edit" /></a>

                                    <a href="${pageContext.request.contextPath}/application-tracking/forms/${purpose.externalId}"><fmt:message key="label.form.edit" /></a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
    <div role="tabpanel" class="tab-pane active" id="groups">
        <h3><fmt:message key="label.backoffice.group" /></h3>

        <form action="${pageContext.request.contextPath}/application-tracking/configuration/backoffice-group" method="POST">
            <div class="form-group <c:if test="${not empty errors}">has-error</c:if>">
                <input type="text" name="group" id="group" class="form-control"
                       value="<c:out value="${applicationTracking.backofficeGroup.expression}" />"/>
                <c:if test="${not empty errors}">
                    <p class="help-block"><c:out value="${errors}"/></p>
                </c:if>
            </div>
            <div class="form-group">
                <button type="submit" class="btn btn-primary"><fmt:message key="label.save" /></button>
            </div>
        </form>
    </div>

    <div role="tabpanel" class="tab-pane" id="payment">
        <h3><fmt:message key="label.payment.instructions" /></h3>

        <form action="${pageContext.request.contextPath}/application-tracking/configuration/paymentInstructionsTemplate" method="POST">
            <div class="form-group">
                <input bennu-localized-string bennu-html-editor type="text" name="body" id="body" class="form-control"
                       value="<c:out value="${paymentInstructionsTemplate.body.json()}" />"/>
            </div>
            <div class="form-group">
                <button type="submit" class="btn btn-primary"><fmt:message key="label.save" /></button>
            </div>
        </form>
    </div>

    <div role="tabpanel" class="tab-pane" id="requirements">
        <h3><fmt:message key="label.requirements" /></h3>
        <p>
            <button class="btn btn-default" data-toggle="modal" data-target="#newRequirementModal">
                <span class="glyphicon glyphicon-plus"></span>
                <fmt:message key="label.new" />
            </button>
        </p>
        <c:choose>
            <c:when test="${empty applicationTracking.requirements}">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <fmt:message key="message.requirement.none" />
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <table class="table">
                    <thead>
                        <tr>
                            <th><fmt:message key="label.name" /></th>
                            <th><fmt:message key="label.description" /></th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="requirement" items="${applicationTracking.requirements}">
                            <tr>
                                <td><c:out value="${requirement.name.content}"/></td>
                                <td><c:out value="${requirement.description.content}"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/application-tracking/configuration/requirement/${requirement.externalId}"><fmt:message key="label.edit" /></a>

                                    <a href="${pageContext.request.contextPath}/application-tracking/forms/${requirement.externalId}"><fmt:message key="label.form.edit" /></a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <div role="tabpanel" class="tab-pane" id="validators">
        <h3><fmt:message key="label.validators" /></h3>
        <p>
            <button class="btn btn-default" data-toggle="modal" data-target="#newValidatorModal">
                <span class="glyphicon glyphicon-plus"></span> 
                <fmt:message key="label.new" />
            </button>
        </p>
        <c:choose>
            <c:when test="${empty applicationTracking.requirements}">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <fmt:message key="message.validators.none" />
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <table class="table table">
                    <thead>
                        <tr>
                            <th><fmt:message key="label.name" /></th>
                            <th><fmt:message key="label.slug" /></th>
                            <th><fmt:message key="label.description" /></th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="validator" items="${applicationTracking.fieldValidatorSet}">
                        <tr>
                            <td>${validator.title.content}</td>
                            <td><code>${validator.slug}</code></td>
                            <td>${validator.description.content}</td>
                            <td></td>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
        </div>
    </div>
</div>

<%-- Modals --%>

<div class="modal fade" id="newValidatorModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form method="POST" action="${pageContext.request.contextPath}/application-tracking/configuration/validators">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"><fmt:message key="label.validator.new" /></h4>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label><fmt:message key="label.slug" /></label>
                        <input  class="form-control" type="text" name="slug">
                    </div>
                    <div class="form-group">
                        <label><fmt:message key="label.title" /></label>
                        <input  class="form-control" type="text" name="title" bennu-localized-string />
                    </div>
                    <div class="form-group">
                        <label><fmt:message key="label.description" /></label>
                        <input class="form-control" type="text" name="description" bennu-localized-string />
                    </div>
                    <div class="form-group">
                        <label><fmt:message key="label.error.message" /></label>
                        <input class="form-control" type="text" name="errorMessage" bennu-localized-string />
                    </div>
                    <div class="form-group">
                        <label><fmt:message key="label.code" /></label>
                        <textarea class="form-control" name="code" rows="5">function validate(value) {
        
    }
                        </textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.cancel" /></button>
                    <button class="btn btn-primary" type="submit"><fmt:message key="label.validator.save" /></button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="newPurposeModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form class="form-horizontal" action="${pageContext.request.contextPath}/application-tracking/configuration/purpose"
                  method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"><fmt:message key="label.purpose.new" /></h4>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="type"><fmt:message key="label.type" /></label>
                        <div class="col-sm-9">
                            <select name="type" id="type" class="form-control" required>
                                <option>org.fenixedu.applicationtracking.domain.Purpose</option>
                                <c:forEach var="type" items="${purposeTypes}">
                                    <option><c:out value="${type}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="name"><fmt:message key="label.name" /></label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control" id="name" name="name" bennu-localized-string required-any/>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.cancel" /></button>
                    <button type="submit" class="btn btn-primary"><fmt:message key="label.create" /></button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="newRequirementModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form class="form-horizontal"
                  action="${pageContext.request.contextPath}/application-tracking/configuration/requirement" method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"><fmt:message key="label.requirement.new" /></h4>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="requirementType"><fmt:message key="label.type" /></label>
                        <div class="col-sm-9">
                            <select name="type" id="requirementType" class="form-control" required>
                                <option>org.fenixedu.applicationtracking.domain.Requirement</option>
                                <c:forEach var="type" items="${requirementTypes}">
                                    <option><c:out value="${type}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="req-name"><fmt:message key="label.name" /></label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control" id="req-name" name="name" bennu-localized-string
                                   required-any/>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.cancel" /></button>
                    <button type="submit" class="btn btn-primary"><fmt:message key="label.create" /></button>
                </div>
            </form>
        </div>
    </div>
</div>

${portal.toolkit()}