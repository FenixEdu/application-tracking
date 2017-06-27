<%--@elvariable id="upcomingPeriods" type="java.util.List<org.fenixedu.applicationtracking.domain.Period>"--%>
<%--@elvariable id="openPeriods" type="java.util.List<org.fenixedu.applicationtracking.domain.Period>"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h1><fmt:message key="title.application.tracking" /></h1>
<ol class="breadcrumb">
	<li class="active"><fmt:message key="title.application.tracking" /></li>
</ol>

<a href="${pageContext.request.contextPath}/application-tracking/create" class="btn btn-default"><span
        class="glyphicon glyphicon-plus"></span> <fmt:message key="label.period.create"/></a>

<ul class="nav nav-tabs" role="tablist">
    <li role="presentation" class="active"><a href="#open-periods" aria-controls="home" role="tab" data-toggle="tab"><fmt:message key="label.periods.open" /></a></li>
    <li role="presentation"><a href="#upcoming-periods" aria-controls="profile" role="tab" data-toggle="tab"><fmt:message key="label.periods.upcoming" /></a></li>
    <li role="presentation"><a href="#closed-periods" aria-controls="messages" role="tab" data-toggle="tab"><fmt:message key="label.periods.closed" /></a></li>
</ul>

<div class="tab-content">    
    <div role="tabpanel" class="tab-pane col-sm-6 active" id="open-periods">
        <h3><fmt:message key="label.periods.open" /> <span class="badge">${openPeriods.size()}</span></h3>

        <c:choose>
            <c:when test="${empty openPeriods}">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <fmt:message key="message.no.open.periods" />
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="period" items="${openPeriods}">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <h4><a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out
                                    value="${period.name.content}"/></a></h4>
                            <div>
                                <span class="glyphicon glyphicon-calendar"></span>
                                <fmt:message key="label.period.start" />: ${period.start.toLocalDate()} ${period.start.toLocalTime()}
                                <fmt:message key="label.period.end" />: ${period.end.toLocalDate()} ${period.end.toLocalTime()}
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    <div role="tabpanel" class="tab-pane col-sm-6" id="upcoming-periods">
        <h3><fmt:message key="label.periods.upcoming" /> <span class="badge">${upcomingPeriods.size()}</span></h3>

        <c:choose>
            <c:when test="${empty upcomingPeriods}">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <fmt:message key="message.no.upcoming.periods" />
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="period" items="${upcomingPeriods}">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <h4><a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out
                                    value="${period.name.content}"/></a></h4>
                            <div>
                                <span class="glyphicon glyphicon-calendar"></span>
                                <fmt:message key="label.period.start" />: ${period.start.toLocalDate()} ${period.start.toLocalTime()}
                                <fmt:message key="label.period.end" />: ${period.end.toLocalDate()} ${period.end.toLocalTime()}
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
    <div role="tabpanel" class="tab-pane col-sm-6" id="closed-periods">
        <h3><fmt:message key="label.periods.closed" /> <span class="badge">${closedPeriods.size()}</span></h3>
    
        <c:choose>
            <c:when test="${empty closedPeriods}">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <fmt:message key="message.no.closed.periods" />
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="period" items="${closedPeriods}">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <h4><a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out
                                    value="${period.name.content}"/></a></h4>
                            <div>
                                <span class="glyphicon glyphicon-calendar"></span>
                                <fmt:message key="label.period.start" />: ${period.start.toLocalDate()} ${period.start.toLocalTime()}
                                <fmt:message key="label.period.end" />: ${period.end.toLocalDate()} ${period.end.toLocalTime()}
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>
