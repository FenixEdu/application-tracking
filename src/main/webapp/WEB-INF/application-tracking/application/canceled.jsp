<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://fenixedu.org/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="base" value="${pageContext.request.contextPath}/public-applications/${period.slug}/slots" scope="request" />

<t:applications>
  <jsp:body>
    <h2><fmt:message key="label.application.canceled" /></h2>

    <p><fmt:message key="message.application.canceled" /></p>
  </jsp:body>
</t:applications>