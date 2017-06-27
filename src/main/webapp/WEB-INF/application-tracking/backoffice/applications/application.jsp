<%--@elvariable id="period" type="org.fenixedu.applicationtracking.domain.Period"--%>
<%--@elvariable id="application" type="org.fenixedu.applicationtracking.domain.Application"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

${portal.toolkit()}
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
<%-- ${portal.angularToolkit()} --%>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/angular.js/1.5.0/angular.js"></script>
${portal.angularToolkit().replaceAll('angular.min.js', 'xpto')}
<script type="text/javascript"
        src="https://cdnjs.cloudflare.com/ajax/libs/danialfarid-angular-file-upload/11.0.1/ng-file-upload-shim.min.js"></script>
<script type="text/javascript"
        src="https://cdnjs.cloudflare.com/ajax/libs/danialfarid-angular-file-upload/11.0.1/ng-file-upload.min.js"></script>

<script>
    var applicationStatus = ${status};
    var user = {
        givenNames: 
            <c:if test="${empty application.givenNames}">""</c:if>
            <c:if test="${not empty application.givenNames}">"${application.givenNames}"</c:if>,
        familyNames:
            <c:if test="${empty application.familyNames}">""</c:if>
            <c:if test="${not empty application.familyNames}">"${application.familyNames}"</c:if>,
        email:
            <c:if test="${empty application.email}">""</c:if>
            <c:if test="${not empty application.email}">"${application.email}"</c:if>
    }
</script>

<script src="${pageContext.request.contextPath}/scripts/formEngine.js"></script>

<div ng-app="FormEngineApp" ng-controller="MainCtrl">
<h1><fmt:message key="label.applications.manage" /></h1>
<ol class="breadcrumb">
	<li><a href="${pageContext.request.contextPath}/application-tracking"><fmt:message key="title.application.tracking" /></a></li>
	<li><a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a></li>
	<li><a href="${pageContext.request.contextPath}/application-tracking/${period.slug}/applications"><fmt:message key="label.applications.view" /></a></li>
	<li class="active"><c:out value="${application.number}"/></li>
</ol>
<div class="row">
    <div class="col-sm-3">
        <ul class="nav nav-pills nav-stacked" role="tablist" id="mainNav">
            <li role="presentation" class="active"><a href="#summary" aria-controls="summary" role="tab" data-toggle="tab"><fmt:message key="label.summary" /></a></li>
            <li role="presentation"><a href="#data" aria-controls="data" role="tab" data-toggle="tab"><fmt:message key="label.application.data" /></a></li>
            <c:forEach var="req" items="${application.requirementFulfilments}">
                <li role="presentation">
                    <a href="#r-${req.requirementConfiguration.order}" aria-controls="r-${req.requirementConfiguration.order}"
                       role="tab" data-toggle="tab"><c:out value="${req.requirementConfiguration.requirement.name.content}"/></a>
                </li>
            </c:forEach>
            <li role="presentation"><a href="#payment" aria-controls="payment" role="tab" data-toggle="tab"><fmt:message key="label.payment.instructions" /></a></li>
            <li role="presentation"><a href="#comment" aria-controls="comment" role="tab" data-toggle="tab"><fmt:message key="label.comments" /></a></li>
            <li role="presentation"><a href="#log" aria-controls="log" role="tab" data-toggle="tab"><fmt:message key="label.log.activity" /></a></li>
            <li role="presentation"><a href="#email" aria-controls="log" role="tab" data-toggle="tab"><fmt:message key="label.send.email" /></a></li>
            <c:if test="${application.active}">
                <li role="presentation"><a href="#danger" aria-controls="danger" role="tab" data-toggle="tab"><fmt:message key="label.danger.zone" /></a></li>
            </c:if>
        </ul>
    </div>
    <div class="col-sm-9" id="tab-content" style="display: none;">
        <div class="tab-content">
            <div role="tabpanel" class="tab-pane active" id="summary">
                <h3><fmt:message key="label.summary" /></h3>
                <div>

                        <c:choose>
                            <c:when test="${application.active}">
                                <c:forEach var="transition" items="${application.possibleForwardStates}">
                                    <form method="post" id="transition" class="softform"
                                          action="${pageContext.request.contextPath}/application-tracking/${period.slug}/applications/${application.number}/state"
                                          style="display:inline;">
                                    <input type="hidden" name="state" value="${transition.name()}" />
                                    <button type="submit" class="btn btn-default"><c:out value="${transition.localizedActionName.content}"/></button>
                                    </form>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <form method="post" id="transition" class="softform"
                                      action="${pageContext.request.contextPath}/application-tracking/${period.slug}/applications/${application.number}/state"
                                      style="display:inline;">
                                <input type="hidden" name="state" value="DRAFT" />
                                <button type="submit" class="btn btn-default"><fmt:message key="label.reopen" /></button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </form>
                </div>
                <p></p>
                <div class="row">
                    <div class="col-sm-10">
                        <a href="#" data-toggle="modal" data-target="#edit-names"><fmt:message key="label.edit" /></a>
                        <div class="modal fade" id="edit-names" tabindex="-1" role="dialog">
                            <div class="modal-dialog" role="document">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                                aria-hidden="true">&times;</span></button>
                                        <h4 class="modal-title"><fmt:message key="label.edit.user" /></h4>
                                    </div>
                                    <div class="modal-body">
                                        <div class="form-group">
                                            <label for="exampleInputFile"><fmt:message key="label.names.given" /></label>
                                            <input type="text" class="form-control" ng-model="user.givenNames" aria-label="">
                                        </div>
                                        <div class="form-group">
                                            <label for="exampleInputFile"><fmt:message key="label.names.family" /></label>
                                            <input type="text" class="form-control" ng-model="user.familyNames" aria-label="">
                                        </div>
                                        <div class="form-group">
                                            <label for="exampleInputFile"><fmt:message key="label.email" /></label>
                                            <input type="email" class="form-control" ng-model="user.email" aria-label="">
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.cancel" /></button>
                                        <button type="submit" ng-click="saveUser()" class="pull-right btn btn-default" data-dismiss="modal"><fmt:message key="label.save" /></button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <table class="table">
                            <tbody>
                            <tr>
                                <th><fmt:message key="label.number" /></th>
                                <td><code><c:out value="${application.number}"/></code></td>
                            </tr>
                            <tr>
                                <th><fmt:message key="label.date.creation" /></th>
                                <td><c:out value="${application.creation}"/></td>
                            </tr>
                            <tr>
                                <th><fmt:message key="label.names.given" /></th>
                                <td>{{ user.givenNames }}</td>
                            </tr>
                            <tr>
                                <th><fmt:message key="label.names.family" /></th>
                                <td>{{ user.familyNames }}</td>
                            </tr>
                            <tr>
                                <th><fmt:message key="label.email" /></th>
                                <td>{{ user.email }}</td>
                            </tr>
                            <tr>
                                <th><fmt:message key="label.state" /></th>
                                <td><c:out value="${application.state.localizedName.content}"/></td>
                            </tr>
                            <tr>
                                <th><fmt:message key="label.selections" /></th>
                                <td>
                                    <ol>
                                        <c:forEach var="selection" items="${application.purposeFulfilment.slotSelection}">
                                            <li>
                                                <c:forEach var="slot" items="${selection.path}" varStatus="loop">
                                                    <c:out value="${slot.name.content}"/><c:if test="${!loop.last}">,</c:if>
                                                </c:forEach>
                                            </li>
                                        </c:forEach>
                                    </ol>
                                    <a href="#" data-toggle="modal" data-target="#edit-selection"><fmt:message key="label.edit" /></a>
                                    <div class="modal fade" id="edit-selection" tabindex="-1" role="dialog">
                                        <div class="modal-dialog" role="document">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                                            aria-hidden="true">&times;</span></button>
                                                    <h4 class="modal-title"><fmt:message key="label.edit.selection" /></h4>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="form-group" ng-repeat="slot in getSlots() track by $index">
                                                        <label for="exampleInputPassword1">{{getOrdinal($index + 1)}} <fmt:message key="label.choice" /></label>
                                                        <slot-selection data="$parent.status.slots.available" pos="0" writeback="status.slots.selected[$index]"></slot-selection>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.cancel" /></button>
                                                    <button type="submit" ng-click="addSlot()" ng-show="canAddSlots()" class="btn btn-default"><fmt:message key="label.choice.add" /></button>
                                                    <button type="submit" ng-click="saveSlots()" class="pull-right btn btn-default" data-dismiss="modal"><fmt:message key="label.save" /></button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <th><fmt:message key="label.paid" /></th>
                                <td>
                                    <span ng-show="${application.purposeFulfilment.paid}"><fmt:message key="label.yes" /></span>
                                    <span ng-show="!${application.purposeFulfilment.paid}"><fmt:message key="label.no" /></span>
                                </td>
                            </tr>
                            <tr>
                                <th><fmt:message key="label.requirement.fulfilment" /></th>
                                <td>
                                    <div class="progress">
                                        <div class="progress-bar" role="progressbar" aria-valuenow="${application.completeRequirementFulfilments.size()}" aria-valuemin="0" aria-valuemax="${application.requirementFulfilments.size()}" style="width: ${application.completeRequirementFulfilments.size() * 100 / application.requirementFulfilments.size()}%;">
                                            <span class="sr-only"></span>
                                        </div>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <th><fmt:message key="label.rank.calculated" /></th>
                                <td><c:out value="${application.calculateRank()}" /></td>
                            </tr>
                            <tr>
                                <th><fmt:message key="label.certificate" /></th>
                                <td>
                                    <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}/applications/${application.number}/application-certificate"><fmt:message key="label.download" /></a>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div role="tabpanel" class="tab-pane" id="data">
                <h3><fmt:message key="label.application.data" /></h3>
                <div ng-repeat="form in status.steps.purpose.forms track by $index" role="tabpanel" class="tab-pane"
                     id="purpose-step-{{$index}}">
                    <h4 ng-init="formIndex = $index">{{form.title | i18n}}</h4>

                    <a href="#" data-toggle="modal" data-target="#edit-purpose-step-{{$index}}"><fmt:message key="label.edit" /></a>

                    <div class="form-group" ng-repeat="field in form.fields | orderBy: 'index'">
                        <field read-only="true" value="field"
                               ng-model="status.steps.purpose.answers[$parent.form.slug].fields[field.key]"/>
                    </div>

                    <div class="modal fade" id="edit-purpose-step-{{$index}}" tabindex="-1" role="dialog">
                        <div class="modal-dialog" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                            aria-hidden="true">&times;</span></button>
                                    <h4 class="modal-title"><fmt:message key="label.edit.answers" />: {{form.title | i18n}}</h4>
                                </div>
                                <div class="modal-body">
                                    <div class="form-group" ng-repeat="field in form.fields | orderBy: 'index'">
                                        <field value="field" ng-model="status.steps.purpose.answers[$parent.form.slug].fields[field.key]"/>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.cancel" /></button>
                                    <button type="submit" ng-click="saveFormByBackoffice()" class="pull-right btn btn-default" data-dismiss="modal"><fmt:message key="label.save" /></button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <c:forEach var="fulfilment" items="${application.requirementFulfilments}" varStatus="loop">
                <div role="tabpanel" class="tab-pane" id="r-${fulfilment.requirementConfiguration.order}">
                    <h3><c:out value="${fulfilment.requirementConfiguration.requirement.name.content}"/></h3>
                    <em><c:out value="${fulfilment.requirementConfiguration.requirement.description.content}"/></em>

                    <p>
                    <button type="button" class="btn btn-default" data-toggle="modal" data-target="#evaluation${loop.index}">
                        <fmt:message key="label.evaluation" />
                    </button>
                    </p>

                    <!-- Modal -->
                    <div class="modal fade" id="evaluation${loop.index}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
                        <form method="post" id="fulfilment" class="softform"
                              action="${pageContext.request.contextPath}/application-tracking/${period.slug}/applications/${application.number}/grade">
                        <div class="modal-dialog" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                    <h4 class="modal-title"><fmt:message key="label.evaluation" /></h4>
                                </div>
                                <div class="modal-body">
                                    <div class="form-group">
                                        <label><fmt:message key="label.grade" /></label>
                                        <input type="text" class="form-control"  name="grade" value="${fulfilment.grade}" placeholder="<fmt:message key='label.grade' />"/>
                                    </div>

                                    <input type="hidden" name="fulfilment" value="${fulfilment.externalId}" />

                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.cancel" /></button>
                                    <button type="submit" class="btn btn-primary"><fmt:message key="label.set" /></button>
                                </div>
                            </div>
                        </div>
                        </form>
                    </div>

                    <div ng-repeat="form in status.steps.requirements[${loop.index}].forms track by $index" role="tabpanel" class="tab-pane"
                         id="r-${loop.index}-step-{{$index}}">
                        <h2 ng-init="formIndex = $index">{{form.title | i18n}}</h2>
                        <a href="#" data-toggle="modal" data-target="#edit-r-${loop.index}-step-{{$index}}"><fmt:message key="label.edit" /></a>
                        <div class="form-group" ng-repeat="field in form.fields | orderBy: 'index'">
                            <field read-only="true" value="field"
                                   ng-model="status.steps.requirements[${loop.index}].answers[$parent.form.slug].fields[field.key]"/>
                        </div>
                        <div class="modal fade" id="edit-r-${loop.index}-step-{{$index}}" tabindex="-1" role="dialog">
                            <div class="modal-dialog" role="document">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                                aria-hidden="true">&times;</span></button>
                                        <h4 class="modal-title"><fmt:message key="label.edit.answers" />: {{form.title | i18n}}</h4>
                                    </div>
                                    <div class="modal-body">
                                        <div class="form-group" ng-repeat="field in form.fields | orderBy: 'index'">
                                            <field value="field" ng-model="status.steps.requirements[${loop.index}].answers[$parent.form.slug].fields[field.key]"/>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.cancel" /></button>
                                        <button type="submit" ng-click="saveFormByBackoffice()" class="pull-right btn btn-default" data-dismiss="modal"><fmt:message key="label.save" /></button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
            <div role="tabpanel" class="tab-pane" id="payment">
                <h3><fmt:message key="label.payment.instructions" /></h3>
                <p>${paymentInstructions}</p>
            </div>
            <div role="tabpanel" class="tab-pane" id="comment">
                <h3><fmt:message key="label.comments" /></h3>
                <c:if test="${empty application.comments}">
                    <p><fmt:message key="message.comments.none" /></p>
                </c:if>
                <c:forEach var="comment" items="${application.comments}">
                    <div class="media">
                        <div class="pull-left">
                            <a href="#">
                                <img class="media-object" src="https://fenix.tecnico.ulisboa.pt/user/photo/<c:out value="${comment.log.userWho.username}"/>?s=35"/>
                            </a>
                        </div>
                        <div class="media-body">
                            <p><strong><c:out value="${comment.log.who}"/></strong> <c:out value="${comment.text}"/></p>
                            <p class="help-block"><abbr title="<c:out value="${comment.log.extendedWhen}"/>"><c:out value="${comment.log.humanReabadleWhen}" /></abbr></p>
                        </div>
                    </div>
                </c:forEach>
                <form method="post" class="softform" action="${pageContext.request.contextPath}/application-tracking/${period.slug}/applications/${application.number}/comment">
                    <div class="form-group">
                        <textarea id="commentText" name="text" class="form-control" placeholder="<fmt:message key='message.comments.none' />" onclick="this.submit();" required></textarea>
                    </div>
                    <div class="form-group">
                        <button id="commentButton" type="submit" class="btn btn-primary"><fmt:message key="label.comment" /></button>
                    </div>
                </form>
            </div>
            <div role="tabpanel" class="tab-pane" id="log">
                <h3><fmt:message key="label.log.activity" /></h3>
                <c:forEach var="log" items="${application.activityLog}">
                    <div class="row">
                        <div class="col-sm-1">
                            <c:if test="${not empty log.userWho}">
                                <img class="media-object" src="https://fenix.tecnico.ulisboa.pt/user/photo/<c:out value="${log.userWho.username}"/>?s=22"/>
                            </c:if>
                        </div>
                        <div class="col-sm-11">
                            <time class="pull-right"><abbr title="<c:out value="${log.extendedWhen}"/>"><c:out value="${log.humanReabadleWhen}" /></abbr></time>
                            <c:choose>
                                <c:when test="${log.type == 'state'}">
                                    <p>
                                        <fmt:message key="log.set.state">
                                            <fmt:param value="${log.who}" />
                                            <fmt:param value="${log.state.localizedName.content}" />
                                        </fmt:message>
                                    </p>
                                </c:when>
                                <c:when test="${log.type == 'comment'}">
                                    <p>
                                        <fmt:message key="log.comment">
                                            <fmt:param value="${log.who}" />
                                        </fmt:message>
                                    </p>
                                </c:when>
                                <c:otherwise>
                                    <p><c:out value="${log.who} ${log.customAction.content}"/></p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <div role="tabpanel" class="tab-pane" id="email">
                <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="summary">
                        <h3><fmt:message key="label.send.email" /></h3>

                        <form method="post" id="email" class="softform"
                                  action="${pageContext.request.contextPath}/application-tracking/${period.slug}/applications/${application.number}/email">

                            <div class="form-group">
                                <label><fmt:message key='label.subject' /></label>
                                <input type="text" class="form-control" name="subject" placeholder="<fmt:message key='label.subject' />...">
                            </div>

                            <div class="form-group">
                                <label for="commentText"><fmt:message key='label.body' /></label>
                                <textarea id="commentText" name="body" class="form-control" placeholder="<fmt:message key='label.body' />..." required></textarea>
                            </div>

                            <button type="submit" class="btn btn-primary"><fmt:message key="label.send" /></button>
                        </form>
                    </div>
                </div>
            </div>
            <c:if test="${application.active}">
                <div role="tabpanel" class="tab-pane" id="danger">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h3 class="panel-title"><fmt:message key="label.danger.zone" /></h3>
                        </div>
                        <div class="panel-body">
                            <h4><fmt:message key="label.cancel" /></h4>
                            <form method="post" id="danger" class="softform"
                                  action="${pageContext.request.contextPath}/application-tracking/${period.slug}/applications/${application.number}/state">
                                  <input type="hidden" name="state" value="CANCELLED" />
                                <button type="submit" class="btn btn-danger"><fmt:message key="label.cancel" /></button>
                            </form>
                            <h4><fmt:message key="label.revert" /></h4>
                            <form method="post" id="danger" class="softform"
                                  action="${pageContext.request.contextPath}/application-tracking/${period.slug}/applications/${application.number}/state">
                                <c:set var="revert" value="${application.reversionState}"/>
                                <c:if test="${revert.present}">
                                    <input type="hidden" name="state" value="${revert.get().name()}"/>
                                    <button type="submit" class="btn btn-danger"><fmt:message key="label.revert" /></button>
                                </c:if>
                            </form>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</div>
</div>
<script src="${pageContext.request.contextPath}/scripts/patch-toolkit.js"></script>
<script src="${pageContext.request.contextPath}/themes/default/js/bootstrap.min.js"></script>
<script type="application/javascript">
    $(function () {
        $('[data-toggle="popover"]').popover();
    })
    $(document).ready(function(){
        $('#commentButton').attr('disabled',true);
        $('#commentText').keyup(function(){
            if($(this).val().length !=0)
                $('#commentButton').attr('disabled', false);
            else
                $('#commentButton').attr('disabled',true);
        })
        $("ul.nav-tabs > li > a").on("shown.bs.tab", function(e) {
            var id = $(e.target).attr("href").substr(1);
            window.location.hash = id;
        });

        var hash = '#' + window.location.hash.substring(2);
        $('#mainNav a[href="' + hash + '"]').tab('show');
        $('#tab-content').show();
        $('.softform').submit(function(e){
            e.preventDefault();
            var values = {};
            $.each($(this).serializeArray(), function(i, field) {
                values[field.name] = field.value;
            });
            $.post(this.action, values).success(function(data) {
                window.location.reload();
            });
        });
    });

</script>
