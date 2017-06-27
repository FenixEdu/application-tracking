<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="base" value="${pageContext.request.contextPath}/public-applications/${period.slug}" scope="request" />

<t:applications>
  <jsp:body>
    <h2><fmt:message key="label.apply.to" /> ${period.name.content}</h2>
    <div class="row">
      <div class="col-sm-6">
        <h4><fmt:message key="label.new.user" /></h4>
        <p>
            <fmt:message key="message.new.user" />
        </p>
        <form class="form-horizontal" method="POST">
          <div class="form-group">
            <label class="control-label col-sm-3" for="email"><fmt:message key="label.email" /></label>
            <div class="col-sm-9">
              <input type="email" name="email" id="email" class="form-control" required />
            </div>
          </div>
          <c:if test="${not empty captcha}">
            <div class="form-group">
              <label class="control-label col-sm-3"><fmt:message key="label.captcha" /></label>
              <div class="col-sm-9">
                ${captcha.generateCaptcha(pageContext.request)}
              </div>
            </div>
          </c:if>
          <div class="form-group">
            <div class="col-sm-9 col-sm-offset-3">
              <button type="submit" class="btn btn-default"><fmt:message key="label.application.create" /></button>
            </div>
          </div>
        </form>
      </div>
      <div class="col-sm-6">
        <h4><fmt:message key="label.has.account" /></h4>
        <p>
            <fmt:message key="message.has.account.institution" />
        </p>
        <a href="${base}/apply-user" role="button" class="btn btn-default"><fmt:message key="label.select" /> »</a>
      </div>
    </div>
  </jsp:body>
</t:applications>

