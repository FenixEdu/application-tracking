<%--@elvariable id="period" type="org.fenixedu.applicationtracking.domain.Period"--%>
<%--@elvariable id="applicationTracking" type="org.fenixedu.applicationtracking.domain.ApplicationTracking"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h1><fmt:message key="label.period.application" /></h1>
<ol class="breadcrumb">
	<li><a href="${pageContext.request.contextPath}/application-tracking"><fmt:message key="title.application.tracking" /></a></li>
	<li class="active"><c:out value="${period.name.content}"/></li>
</ol>
<h3><c:out value="${period.name.content}"/>
    <a href="#" class="btn btn-default" data-toggle="modal" data-target="#configurationModal" title="<fmt:message key='label.period.configure' />">
        <span class="glyphicon glyphicon-cog"></span>
    </a>
    <a class="btn btn-default" href="${pageContext.request.contextPath}/application-tracking/${period.slug}/templates"
       title="<fmt:message key='label.message.templates.manage' />">
        <span class="glyphicon glyphicon-envelope"></span>
    </a>
    <a class="btn btn-default" target="_blank"
       href="${pageContext.request.contextPath}/public-applications/${period.slug}" title="<fmt:message key='label.link.public' />">
        <span class="glyphicon glyphicon-link"></span>
    </a>
    <a class="btn btn-default"
       href="${pageContext.request.contextPath}/application-tracking/forms/${period.purposeConfiguration.oid}" title="<fmt:message key='label.form.editing' />">
        <span class="glyphicon glyphicon-user"> <fmt:message key="label.form" /></span>
    </a>
</h3>

<div class="jumbotron">
    <div class="row">
        <div class="col-sm-4">
            <dl>
                <dt><fmt:message key="label.name" /></dt>
                <dd><c:out value="${period.name.content}"/></dd>
                <dt><fmt:message key="label.purpose" /></dt>
                <dd><c:out value="${period.purpose.name.content}"/></dd>
                <dt><fmt:message key="label.slug" /></dt>
                <dd><code>${period.slug}</code></dd>
            </dl>
        </div>
        <div class="col-sm-4">
            <dl>
                <dt><fmt:message key="label.period.start" /></dt>
                <dd>${period.start.toLocalDate()} ${period.start.toLocalTime()}</dd>
                <dt><fmt:message key="label.period.end" /></dt>
                <dd>${period.end.toLocalDate()} ${period.end.toLocalTime()}</dd>
                <dt><fmt:message key="label.status" /></dt>
                <dd>
                    <c:choose>
                        <c:when test="${period.future}">
                            <fmt:message key="label.status.not.started" />
                        </c:when>
                        <c:when test="${period.open}">
                            <fmt:message key="label.status.open" />
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="label.status.closed" />
                        </c:otherwise>
                    </c:choose>
                </dd>
            </dl>
        </div>
        <div class="col-sm-4">
            <dl>
                <dt><fmt:message key="label.applications.number" /></dt>
                <dd>${period.applicationSet.size()}</dd>
            </dl>
            <p>
                <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}/applications/create"
                   class="btn btn-default"><span class="glyphicon glyphicon-plus"></span> <fmt:message key="label.application.create" /></a>
                <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}/applications"
                   class="btn btn-default"><span class="glyphicon glyphicon-search"></span> <fmt:message key="label.applications.view" /></a>
            </p>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-sm-6">
        <div class="panel panel-default">
            <div class="panel-heading" data-toggle="collapse" data-target="#slotsPanel" style="cursor: pointer">
                <h4 class="panel-title">
                    <span class="glyphicon glyphicon-chevron-right"></span>
                    <fmt:message key="label.slots" />
                    <span class="badge">${period.purposeConfiguration.slotSet.size()}</span>
                </h4>
            </div>
            <div class="panel-collapse collapse" id="slotsPanel">
                <div class="panel-body">
                    <button class="btn btn-default" data-toggle="modal" data-target="#newSlotModal">
                                <span class="glyphicon glyphicon-plus"></span> <fmt:message key="label.new" />
                            </button>
                            <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}/slotTypes" class="btn btn-default">     <fmt:message key="label.slot.types" />
                            </a>
                </p>
                <c:choose>
                    <c:when test="${empty period.purposeConfiguration.slotSet}">
                        <fmt:message key="message.slots.none" />
                    </c:when>
                    <c:otherwise>
                        <div style="padding-left:20px; padding-right: 20px;">
                        <c:forEach var="slot" items="${period.purposeConfiguration.slotSet}">
                            <c:set var="slot" value="${slot}" scope="session"/>
                            <jsp:include page="slotRender.jsp" />
                        </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
                </div>
            </div>
        </div>
    </div>
    <div class="col-sm-6">
        <c:set var="totalRequirementWeight" value="${period.totalRequirementWeight}"/>
        <div class="panel ${period.totalRequirementWeightValid ? 'panel-default' : 'panel-danger'}">
            <div class="panel-heading" data-toggle="collapse" data-target="#requirementsPanel" style="cursor: pointer">
                <h4 class="panel-title">
                    <span class="glyphicon glyphicon-chevron-right"></span>
                    <fmt:message key="label.requirements" />
                    <span class="badge">${period.requirementConfigurations.size()}</span>
                </h4>
            </div>
            <div class="panel-collapse collapse" id="requirementsPanel">
                <c:choose>
                    <c:when test="${empty period.requirementConfigurations}">
                        <div class="panel-body">
                            <fmt:message key="message.requirements.none" />
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${not period.totalRequirementWeightValid}">
                            <div class="panel-body">
                                <fmt:message key="message.requirements.weight.invalid">
                                    <fmt:param value="${totalRequirementWeight}" />
                                </fmt:message>
                            </div>
                        </c:if>
                        <table class="table" style="margin-bottom: 0">
                            <thead>
                            <tr>
                                <th><fmt:message key="label.order" /></th>
                                <th><fmt:message key="label.name" /></th>
                                <th><fmt:message key="label.weight" /></th>
                                <th></th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:set var="count" scope="session" value="${period.requirementConfigurations.size()}"/>


                            <c:forEach var="requirementConfiguration" items="${period.requirementConfigurations}" varStatus="i">
                                <tr>
                                    <td class="order"></td>
                                    <td><c:out value="${requirementConfiguration.requirement.name.content}"/></td>
                                    <td>${requirementConfiguration.weigth}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}/requirements/${requirementConfiguration.externalId}"><fmt:message key="label.edit" /></a>

                                        
                                        <a href="#"><span class="move-up glyphicon glyphicon-arrow-up"></span></a>
                                        <a href="#"><span class="move-down glyphicon glyphicon-arrow-down"></span></a>

                                        <a href="${pageContext.request.contextPath}/application-tracking/forms/${requirementConfiguration.externalId}"><fmt:message key="label.form.edit" /></a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                        <div class="panel-body">
                            <button class="btn btn-default" data-toggle="modal" data-target="#newRequirementModal">
                                <span class="glyphicon glyphicon-plus"></span> <fmt:message key="label.new" />
                            </button>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<script>
    function reset(){
        $(".move-up,.move-down").show();
        $(".move-up").first().hide();
        $(".move-down").last().hide();
        $(".move-up").each(function(i){ $(this).closest("a").data("index", i) });
        $(".move-down").each(function(i){ $(this).closest("a").data("index", i) });
        $(".move-down").each(function(i){ $(this).closest("tr").find(".order").html(i + 1) });
    }

    $(".move-up").click(function(e){
        var index = parseInt($(e.target).closest("a").data("index"));
        var tr = $(e.target).closest("tr");
        $.ajax({
          type: "POST",
          url: "${pageContext.request.contextPath}/application-tracking/${period.slug}/moveRequirement/" + index + "/" + (index - 1),
          success: function(){
            tr.insertBefore(tr.prev());
            reset();    
          },
        });
    });

    $(".move-down").click(function(e){
        var index = parseInt($(e.target).closest("a").data("index"));
        var tr = $(e.target).closest("tr");
        $.ajax({
          type: "POST",
          url: "${pageContext.request.contextPath}/application-tracking/${period.slug}/moveRequirement/" + index + "/" + (index + 1),
          success: function(){
            tr.insertAfter(tr.next());
            reset();
          },
        });
    });

    reset();
</script>

<div class="modal fade" id="newRequirementModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form class="form-horizontal"
                  action="${pageContext.request.contextPath}/application-tracking/${period.slug}/requirements" method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"><fmt:message key="label.requirement.add" /></h4>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="requirement"><fmt:message key="label.requirement" /></label>
                        <div class="col-sm-9">
                            <select name="requirement" class="form-control" id="requirement">
                                <c:forEach var="requirement" items="${applicationTracking.requirements}">
                                    <option value="${requirement.externalId}"><c:out
                                            value="${requirement.name.content}"/></option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="weight"><fmt:message key="label.weight" /></label>
                        <div class="col-sm-4">
                            <input type="number" step="any" name="weight" id="weight" class="form-control" value="1.0" required/>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.close" /></button>
                    <button type="submit" class="btn btn-primary"><fmt:message key="label.create" /></button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="configurationModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <form class="form-horizontal" action="${pageContext.request.contextPath}/application-tracking/${period.slug}"
                  method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"><fmt:message key="label.period.configure" /></h4>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="control-label col-sm-2" for="period-name"><fmt:message key="label.name" /></label>
                        <div class="col-sm-10">
                            <input type="text" bennu-localized-string name="name" id="period-name"
                                   value="<c:out value="${period.name.json()}" />" required-any/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-2" for="period-slug"><fmt:message key="label.slug" /></label>
                        <div class="col-sm-10">
                            <input type="text" name="slug" id="period-slug" class="form-control"
                                   value="<c:out value="${period.slug}" />" required-any/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-2" for="description"><fmt:message key="label.description" /></label>
                        <div class="col-sm-10">
                            <input type="text" bennu-localized-string bennu-html-editor name="description" id="description"
                                   value="<c:out value="${period.description.json()}" />" required-any/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-2" for="maxSlotsPerApplication"><fmt:message key="label.application.slots.max" /></label>
                        <div class="col-sm-3">
                            <input type="number" min="0" max="${period.purposeConfiguration.slotSet.size()}"
                                   name="maxSlotsPerApplication" id="maxSlotsPerApplication" class="form-control"
                                   value="${period.purposeConfiguration.maxSlotsPerApplication}"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-9 col-sm-offset-2">
                            <label>
                                <input type="checkbox"
                                       name="chargeFee" ${empty period.purposeConfiguration.applicationFee ? '' : 'checked'}>
                                Charge Fee
                            </label>
                        </div>
                    </div>
                    <div class="form-group fee-configuration ${empty period.purposeConfiguration.applicationFee ? 'hide' : ''}">
                        <label class="control-label col-sm-2" for="fee-amount"><fmt:message key="label.fee.amount" /></label>
                        <div class="col-sm-3">
                            <input type="number" step="any" name="feeAmount" class="form-control" id="fee-amount"
                                   value="${period.purposeConfiguration.applicationFee.amount.number}"/>
                        </div>
                        <div class="col-sm-4">
                            <label>
                                <input type="checkbox"
                                       name="feePerSlot" ${period.purposeConfiguration.applicationFee.perSlot ? 'checked' : ''}
                                /> Per Slot
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-2" for="start"><fmt:message key="label.period.start" /></label>
                        <div class="col-sm-5">
                            <input type="text" bennu-datetime name="start" id="start" class="form-control" required value="${period.start}"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-2" for="end"><fmt:message key="label.period.end" /></label>
                        <div class="col-sm-5">
                            <input type="text" bennu-datetime name="end" id="end" class="form-control" required value="${period.end}"/>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" data-toggle="modal" data-target="#deleteModal" class="btn btn-danger pull-left" data-dismiss="modal"><fmt:message key="label.delete" /></button>
                    <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.close" /></button>
                    <button type="submit" class="btn btn-primary"><fmt:message key="label.save" /></button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
            <form class="form-horizontal" action="${pageContext.request.contextPath}/application-tracking/${period.slug}/delete"
                  method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"><fmt:message key="label.period.delete" /></h4>
                </div>
                <div class="modal-body">
                    <fmt:message key="message.delete.period.confirm">
                        <fmt:param value="${period.slug}"/>
                    </fmt:message>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.close" /></button>
                    <button type="submit" class="btn btn-danger"><fmt:message key="label.delete" /></button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="newSlotModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form class="form-horizontal" action="${pageContext.request.contextPath}/application-tracking/${period.slug}/slots"
                  method="POST">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"><fmt:message key="label.slot.new" /></h4>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="control-label col-sm-3" name="name"><fmt:message key="label.parent" /></label>
                        <div class="col-sm-9">
                            <select name="parent" class="form-control">
                                <option value="">-</option>
                                <c:forEach var="slot"  items="${period.purposeConfiguration.allSlots}">
                                    <option value="${slot.code}">${slot.name.content}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-3" name="name"><fmt:message key="label.name" /></label>
                        <div class="col-sm-9">
                            <input type="text" bennu-localized-string name="name" id="name" required-any/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="code"><fmt:message key="label.code" /></label>
                        <div class="col-sm-4">
                            <input type="text" name="code" id="code" class="form-control" required/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label col-sm-3"><fmt:message key="label.vacancies" /></label>
                        <div class="col-sm-4">
                            <input type="number" min="1" name="vacancies" class="form-control" value="100"/>
                            <div class="checkbox">
                                <label>
                                    <input type="checkbox" name="unlimitedVacancies" id="unlimited-toggle"/> <fmt:message key="label.unlimited" />
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.close" /></button>
                    <button type="submit" class="btn btn-primary"><fmt:message key="label.create" /></button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    (function () {
        $("input[name=chargeFee]").change(function () {
            if ($(this).is(":checked")) {
                $(".fee-configuration").removeClass("hide");
            } else {
                $(".fee-configuration").addClass("hide");
            }
        });
        $("#unlimited-toggle").change(function () {
            var input = $("input[name=vacancies]");
            if ($(this).is(":checked")) {
                input.addClass("hide");
            } else {
                input.removeClass("hide");
            }
        });
        $("a[title]").tooltip({'placement': 'bottom'});
    })();
</script>

${portal.toolkit()}
