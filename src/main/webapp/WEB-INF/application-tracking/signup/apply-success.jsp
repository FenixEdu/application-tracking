<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="base" value="${pageContext.request.contextPath}/public-applications/${period.slug}" scope="request" />

<t:applications>
  <jsp:body>
    <h2><fmt:message key="label.apply.to" /> ${period.name.content}</h2>
    <br />
    <p class="lead">
        <fmt:message key="message.application.success" />
    </p>
  </jsp:body>
</t:applications>

