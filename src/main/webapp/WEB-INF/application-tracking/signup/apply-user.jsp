<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="base" value="${pageContext.request.contextPath}/public-applications/${period.slug}" scope="request" />

<t:applications>
  <jsp:body>
    <h2><fmt:message key="label.application.create" /></h2>
    <c:if test="${empty existing}">
      <fmt:message key="message.apply.are.you.sure">
          <fmt:param value="${period.name.content}" />
      </fmt:message>
      <form class="form-horizontal" method="POST">
        <div class="form-group">
          <label class="control-label col-sm-3"><fmt:message key="label.email" /></label>
          <div class="col-sm-9">
            <input type="text" readonly value="${portal.currentUser.profile.email}" class="form-control" />
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-3"><fmt:message key="label.names.given" /></label>
          <div class="col-sm-9">
            <input type="text" readonly value="${portal.currentUser.profile.givenNames}" class="form-control" />
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-3"><fmt:message key="label.names.family" /></label>
          <div class="col-sm-9">
            <input type="text" readonly value="${portal.currentUser.profile.familyNames}" class="form-control" />
          </div>
        </div>
        <div class="form-group">
          <div class="col-sm-9 col-sm-offset-3">
            <button type="submit" class="btn btn-primary"><fmt:message key="label.application.create" /></button>
            <a href="${base}" class="btn btn-default"><fmt:message key="label.cancel" /></a>
          </div>
        </div>
      </form>
    </c:if>
    <c:if test="${not empty existing}">
      <fmt:message key="message.application.proceed" />
      <form method="POST">
        <button type="submit" class="btn btn-primary"><fmt:message key="label.proceed" /></button>
      </form>
    </c:if>
  </jsp:body>
</t:applications>

