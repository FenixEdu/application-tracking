<%--@elvariable id="period" type="org.fenixedu.applicationtracking.domain.Period"--%>
<%--@elvariable id="slot" type="org.fenixedu.applicationtracking.domain.Slot"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h1><fmt:message key="label.slot.configure" /></h1>
<ol class="breadcrumb">
	<li><a href="${pageContext.request.contextPath}/application-tracking"><fmt:message key="title.application.tracking" /></a></li>
	<li><a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a></li>
	<li class="active"><c:out value="${slot.name.content}"/></li>
</ol>
<h3>
    <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a>
    - <c:out value="${slot.name.content}"/>
</h3>

<div class="row">
    <div class="col-sm-8">
        <form class="form-horizontal" method="POST">
            <div class="form-group">
                <label class="control-label col-sm-3" for="name"><fmt:message key="label.name" /></label>
                <div class="col-sm-9">
                    <input type="text" name="name" class="form-control" id="name" bennu-localized-string value="<c:out
                    value="${slot.name.json()}" />"/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-3" for="code"><fmt:message key="label.code" /></label>
                <div class="col-sm-9">
                    <input type="text" name="code" class="form-control" id="code" value="<c:out value="${slot.code}" />"/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label col-sm-3"><fmt:message key="label.vacancies" /></label>
                <div class="col-sm-4">
                    <input type="number" min="1" name="vacancies"
                           class="form-control ${slot.hasUnlimitedVacancies() ? 'hide' : ''}"
                           value="${slot.hasUnlimitedVacancies() ? '100': slot.vacancies}"/>
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" name="unlimitedVacancies" id="unlimited-toggle"
                            ${slot.hasUnlimitedVacancies() ? 'checked' : ''}/> <fmt:message key="label.unlimited" />
                        </label>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-3 col-sm-9">
                    <button type="submit" class="btn btn-primary"><fmt:message key="label.save" /></button>
                </div>
            </div>
        </form>
    </div>
    <div class="col-sm-4">
        <fmt:message key="help.slot" />

        <hr/>
        <div class="well">
            <h5><fmt:message key="label.danger.zone" /></h5>
            <form method="POST"
                  action="${pageContext.request.contextPath}/application-tracking/${period.slug}/slots/${slot.externalId}/delete">
                <button type="submit" class="btn btn-danger"><fmt:message key="label.delete" /></button>
            </form>
        </div>
    </div>
</div>

<script>
    (function () {
        $("#unlimited-toggle").change(function () {
            var input = $("input[name=vacancies]");
            if ($(this).is(":checked")) {
                input.addClass("hide");
            } else {
                input.removeClass("hide");
            }
        });
    })();
</script>

${portal.toolkit()}