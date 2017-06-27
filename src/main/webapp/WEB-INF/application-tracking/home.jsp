<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="base" value="${pageContext.request.contextPath}/public-applications/${period.slug}" scope="request" />

<t:applications>
  <jsp:body>
    <div class="jumbotron text-center">
      <h1>${period.name.content}</h1>
      <p class="lead">${period.description.content}</p>
      <p>
        <c:if test="${period.open}">
          <a href="${base}/apply" class="btn btn-primary"><fmt:message key="label.period.apply" /></a> or
        </c:if>
        <c:if test="${period.open or not period.future}">
          <a href="${base}/retrieve" class="btn btn-default"><fmt:message key="label.period.application.retrieve" /></a>
        </c:if>
      </p>
    </div>

    <c:if test="${period.open or period.future}">
      <p>
        <b><fmt:message key="label.period.open" />: </b> ${period.start}
      </p>
      <p>
        <b><fmt:message key="label.period.close" />: </b> ${period.end}
      </p>
    </c:if>
    <c:if test="${not period.open}">
      <p>
          <fmt:message key="message.period.closed" />
      </p>
    </c:if>
  </jsp:body>
</t:applications>
