<%--@elvariable id="period" type="org.fenixedu.applicationtracking.domain.Period"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h1><fmt:message key="label.message.templates" /></h1>
<ol class="breadcrumb">
	<li><a href="${pageContext.request.contextPath}/application-tracking"><fmt:message key="title.application.tracking" /></a></li>
	<li><a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a></li>
	<li class="active"><fmt:message key="label.message.templates" /></li>
</ol>

<h3>
    <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a>
</h3>

<c:choose>
    <c:when test="${empty period.templateSet}">
        <div class="panel panel-default">
            <div class="panel-body">
                <span class="glyphicon glyphicon-info-sign"></span>
                <fmt:message key="message.templates.none" />
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <button class="btn btn-default" data-toggle="modal" data-target="#newTemplateModal">
            <span class="glyphicon glyphicon-plus"></span> <fmt:message key="label.new" />
        </button>
        <table class="table">
            <thead>
                <tr>
                    <th><fmt:message key="label.template.key" /></th>
                    <th><fmt:message key="label.name" /></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="template" items="${period.templateSet}">
                    <tr>
                        <td><code><c:out value="${template.templateKey}"/></code></td>
                        <td><c:out value="${template.name}"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}/templates/${template.externalId}"
                               class="btn btn-default"><span class="glyphicon glyphicon-pencil"></span> <fmt:message key="label.edit" /></a></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<div class="modal fade" id="newTemplateModal" tabindex="-1" role="dialog" aria-labelledby="newTemplateModal">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form class="form-horizontal" method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"><fmt:message key="label.template.create" /></h4>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="key"><fmt:message key="label.template.key" /></label>
                        <div class="col-sm-9">
                            <input type="text" name="key" id="key" class="form-control" required/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="name"><fmt:message key="label.name" /></label>
                        <div class="col-sm-9">
                            <input type="text" name="name" id="name" class="form-control" required/>
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.close" /></button>
                    <button type="submit" class="btn btn-primary"><fmt:message key="label.create" /></button>
                </div>
            </form>
        </div>
    </div>
</div>