<%--@elvariable id="slot" type="org.fenixedu.applicationtracking.domain.Slot"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="row">
    <div class="col-sm-10">
         - <c:out value="${slot.name.content}"/> (<code><c:out value="${slot.code}"/></code>)
    </div>
    <div class="col-sm-2">
        <a class="pull-right" href="${pageContext.request.contextPath}/application-tracking/${period.slug}/slots/${slot.externalId}">
            <fmt:message key="label.edit" />
        </a>
    </div>
    <div class="col-sm-12" style="padding-left:40px;">
    <c:if test="${slot.slotSet.size() > 0}">
        <c:forEach var="slot" items="${slot.slotSet}">
            <c:set var="slot" value="${slot}" scope="session"/>
            <jsp:include page="slotRender.jsp" />
        </c:forEach>
    </c:if>
    </div>
</div>