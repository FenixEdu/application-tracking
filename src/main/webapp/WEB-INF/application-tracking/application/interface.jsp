<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://fenixedu.org/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="base" value="${pageContext.request.contextPath}/public-applications/${period.slug}/slots" scope="request" />

<t:applications>
  <jsp:body>
    <script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/jquery.min.js"></script>
    <%-- ${portal.angularToolkit()} --%>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/angular.js/1.5.0/angular.js"></script>
    ${portal.angularToolkit().replaceAll('angular.min.js', 'xpto')}
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/danialfarid-angular-file-upload/11.0.1/ng-file-upload-shim.min.js"></script>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/danialfarid-angular-file-upload/11.0.1/ng-file-upload.min.js"></script>

    <script>
    // patch to resolve conflicting jQuery versions;
      var _jqueryTmp = jQuery;

      $(function(){
        jQuery = _jqueryTmp;
        $ = _jqueryTmp;
      });
    </script>

    <script>
      var applicationStatus = ${status};
      var validators = ${validators};
    </script>
    <script src="${pageContext.request.contextPath}/scripts/patch-toolkit.js"></script>
    <script src="${pageContext.request.contextPath}/scripts/formEngine.js"></script>

    <div ng-app="FormEngineApp" ng-controller="MainCtrl">

<div class="row">
  <div class="col-sm-6">
      <ul class="nav nav-pills" role="tablist">
        <li role="presentation" class="active"><a href="#step-1" aria-controls="step-1" role="tab" data-toggle="tab"><fmt:message key="label.choices" /></a></li>
        <li role="presentation" ng-repeat="form in status.steps.forms track by $index"><a href="#step-{{$index + 2}}" aria-controls="step-{{$index + 2}}" role="tab" data-toggle="tab" >{{form.title | i18n}}</a></li>
        <li role="presentation"><a href="#step-{{finalStep + 2}}" ng-init="finalStep = status.steps.forms.length" aria-controls="step-{{finalStep + 2}}" role="tab" data-toggle="tab"><fmt:message key="label.final" /></a></li>
      </ul>
  </div>
  <div class="col-sm-6">
    <button type="submit" ng-click="submitForm()" class="pull-right btn btn-primary"><fmt:message key="label.application.submit" /></button>
    <button type="submit" ng-click="saveForm()" class="pull-right btn btn-default"><fmt:message key="label.save" /></button>
  </div>
</div>

 <div class="tab-content">
    <div role="tabpanel" class="tab-pane active" id="step-1">
      <h2><fmt:message key="label.choices" /></h2>
      <div class="form-group" ng-repeat="slot in getSlots() track by $index">
        <label for="exampleInputPassword1">{{getOrdinal($index + 1)}} <fmt:message key="label.choice" /></label>
        <slot-selection data="$parent.status.slots.available" pos="0" writeback="status.slots.selected[$index]"></slot-selection>
      </div>
      <div>
        <button type="submit" ng-click="addSlot()" ng-show="canAddSlots()" class="btn btn-default"><fmt:message key="label.choice.add" /></button>
        <button type="submit" href="#step-2" role="tab" data-toggle="tab" class="pull-right btn btn-default"><fmt:message key="label.next" /></button>
      </div>
    </div>

    <div ng-repeat="form in status.steps.forms track by $index" role="tabpanel" class="tab-pane" id="step-{{$index + 2}}">
      <h2 ng-init="formIndex = $index">{{form.title | i18n}}</h2>
      
      <div class="form-group" ng-repeat="field in form.fields | orderBy: 'index'">
        {{status.steps.answers[$parent.form.slug].fields.fields}}
        <field value="field" ng-model="status.steps.answers[$parent.form.slug].fields[field.key]" />
      </div>
      <div>
        <button type="submit" ng-href="#step-{{$index + 1}}" role="tab" data-toggle="tab" class="btn btn-default"><fmt:message key="label.previous" /></button>
        <button type="submit" ng-href="#step-{{$index + 3}}" role="tab" data-toggle="tab" class="pull-right btn btn-default"><fmt:message key="label.next" /></button>
      </div>
    </div>

    <div role="tabpanel" class="tab-pane" ng-init="finalStep = status.steps.forms.length" id="step-{{finalStep + 2}}">
      <h2><fmt:message key="label.forms.check" /></h2>

      <p><fmt:message key="message.forms.check" /></p>
      <div>
        <button type="submit" href="#step-{{finalStep + 1}}" role="tab" data-toggle="tab" class="btn btn-default"><fmt:message key="label.previous" /></button>
      </div>
    </div>
  </div>

    </div>

  <script src="${pageContext.request.contextPath}/themes/default/js/bootstrap.min.js"></script>
  </jsp:body>
</t:applications>

<script>
$(function() {
	$('div.tab-pane button[data-toggle="tab"]').on('click', function ()
	{
	    var link = this.getAttribute('href');
	    $('ul.nav-pills a[href="' + link + '"]').trigger('click');
	});
});
</script>
