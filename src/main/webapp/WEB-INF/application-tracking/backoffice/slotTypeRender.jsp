<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--@elvariable id="slotType" type="org.fenixedu.applicationtracking.domain.SlotType"--%>

<tr>
    <td>${slotType.name.content} - (<code>${slotType.code}</code>) <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}/slotTypes/${slotType.externalId}"><fmt:message key="label.edit" /></a></td>
</tr>
<c:if test="${slotType.child != null}">
    <c:set var="slotType" value="${slotType.child}" scope="request"/>
    <jsp:include page="slotTypeRender.jsp" />
</c:if>