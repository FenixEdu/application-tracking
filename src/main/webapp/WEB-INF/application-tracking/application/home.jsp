<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="base" value="${pageContext.request.contextPath}/public-applications/${period.slug}" scope="request" />

<t:applications>
  <jsp:body>
    <h1 class="page-header">
        <fmt:message key="label.application.to" /> ${period.name.content} <code class="small">${application.number}</code>
    </h1>
    ${period} - ${application} - ${actor.actorName} (${actor.actorType})
  </jsp:body>
</t:applications>
