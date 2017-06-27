<%--@elvariable id="period" type="org.fenixedu.applicationtracking.domain.Period"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h1><fmt:message key="label.slot.types.configure" /></h1>
<ol class="breadcrumb">
	<li><a href="${pageContext.request.contextPath}/application-tracking"><fmt:message key="title.application.tracking" /></a></li>
	<li><a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a></li>
	<li class="active"><fmt:message key="label.slot.types.configure" /></li>
</ol>
<h3>
    <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a> - <fmt:message key="label.slot.type" />
</h3>
<div class="panel-body">
    <button class="btn btn-default" data-toggle="modal" data-target="#newSlotModal">
        <span class="glyphicon glyphicon-plus"></span> <fmt:message key="label.new" />
    </button>
</div>

<c:choose>
    <c:when test="${period.purposeConfiguration.slotTypeRoot == null}">
        <div class="panel-body">
            <fmt:message key="message.slot.types.none" />
        </div>
    </c:when>
    <c:otherwise>
        <table class="table">
            <thead>
            <tr>
                <th><fmt:message key="label.slot.hierarchy" /></th>
            </tr>
            </thead>
            <tbody>
            <c:set var="slotType" value="${period.purposeConfiguration.slotTypeRoot}" scope="session"/>
            <jsp:include page="slotTypeRender.jsp" />
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<div class="modal fade" id="newSlotModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form class="form-horizontal" action="${pageContext.request.contextPath}/application-tracking/${period.slug}/slotTypes/new"
                  method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"><fmt:message key="label.slot.type.new" /></h4>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="control-label col-sm-3" name="name"><fmt:message key="label.name" /></label>
                        <div class="col-sm-9">
                            <input type="text" bennu-localized-string name="name" id="name" required-any/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="code"><fmt:message key="label.code" /></label>
                        <div class="col-sm-4">
                            <input type="text" name="code" id="code" class="form-control" required/>
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

${portal.toolkit()}