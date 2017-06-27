<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="base" value="${pageContext.request.contextPath}/public-applications/${period.slug}" scope="request" />

<t:applications>
  <jsp:body>
    <h2><fmt:message key="label.application.create" /></h2>
    <c:if test="${empty invalid}">
    <fmt:message key="message.basic.data" />
      <form class="form-horizontal" action="${base}/create-application" method="POST">
        <input type="hidden" name="nonce" value="${nonce}" />
        <input type="hidden" name="timestamp" value="${timestamp}" />
        <input type="hidden" name="hash" value="${hash}" />
        <div class="form-group">
          <label class="control-label col-sm-3" for="email"><fmt:message key="label.email" /></label>
          <div class="col-sm-9">
            <input type="email" required readonly name="email" id="email" value="<c:out value="${email}" />" class="form-control" />
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-3" for="givenNames"><fmt:message key="label.names.given" /></label>
          <div class="col-sm-9">
            <input type="text" required name="givenNames" id="givenNames" class="form-control" />
          </div>
        </div>
        <div class="form-group">
          <label class="control-label col-sm-3" for="familyNames"><fmt:message key="label.names.family" /></label>
          <div class="col-sm-9">
            <input type="text" required name="familyNames" id="familyNames" class="form-control" />
          </div>
        </div>
        <div class="form-group">
          <div class="col-sm-9 col-sm-offset-3">
            <button type="submit" class="btn btn-primary"><fmt:message key="label.application.create" /></button>
          </div>
        </div>
      </form>
    </c:if>
    <c:if test="${not empty invalid}">
      <div class="alert alert-danger">
          <fmt:message key="message.access.incorrect.or.expired" />
      </div>
    </c:if>
  </jsp:body>
</t:applications>

