<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="base" value="${pageContext.request.contextPath}/public-applications/${period.slug}" scope="request" />

<t:applications>
  <jsp:body>
    <h2><fmt:message key="label.period.application.retrieve" /></h2>
    <c:if test="${param.success != null}">
      <div class="alert alert-success">
          <fmt:message key="message.code.emailed" />
      </div>
    </c:if>
    <c:if test="${not empty badCode}">
      <div class="alert alert-danger">
          <fmt:message key="message.application.not.found" />
      </div>
    </c:if>
    <div class="row">
      <div class="col-sm-4">
        <h4><fmt:message key="label.has.access" /></h4>
        <p><fmt:message key="message.has.access" /></p>
        <form method="POST">
          <div class="form-group">
            <label for="email"><fmt:message key="label.email" /></label>
            <input type="email" name="email" id="email" required class="form-control" />
          </div>
          <div class="form-group">
            <label for="accessCode"><fmt:message key="label.access.code" /></label>
            <input type="text" name="accessCode" id="accessCode" required class="form-control" />
          </div>
          <button type="submit" class="btn btn-default"><fmt:message key="label.retrieve" /></button>
        </form>
      </div>
      <div class="col-sm-4">
        <h4><fmt:message key="label.lost.access" /></h4>
        <p><fmt:message key="message.lost.access" /></p>
        <form method="POST">
          <div class="form-group">
            <label for="email"><fmt:message key="label.email" /></label>
            <input type="email" name="email" id="email" required class="form-control" />
          </div>
          <button type="submit" class="btn btn-default"><fmt:message key="label.recover" /></button>
        </form>
      </div>
      <div class="col-sm-4">
        <h4><fmt:message key="label.has.account" /></h4>
        <p><fmt:message key="message.has.account" /></p>
        <a href="${base}/retrieve-user" role="button" class="btn btn-default"><fmt:message key="label.login" /></a>
      </div>
    </div>
  </jsp:body>
</t:applications>
