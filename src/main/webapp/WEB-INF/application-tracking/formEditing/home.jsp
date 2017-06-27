<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="base" value="${pageContext.request.contextPath}/public-applications/${period.slug}" scope="request" />
<h1><fmt:message key="label.form.edit" /></h1>
<ol class="breadcrumb">
    <li><a href="${pageContext.request.contextPath}/application-tracking"><fmt:message key="title.application.tracking" /></a></li>
    <li><a href="${pageContext.request.getHeader('Referer')}"><fmt:message key="label.previous" /></a></li>
    <li class="active"><fmt:message key="label.form.edit" /></li>
</ol>
<script src="https://cdnjs.cloudflare.com/ajax/libs/notify/0.4.2/notify.min.js"></script>
${ portal.angularToolkit() }
<script>
  var data = ${ form };
  
  var validators = ${ validators };
</script>
<script src="${pageContext.request.contextPath}/scripts/formEditor.js"></script>

<div ng-app="formsEdit" ng-controller="FormEditorController">
<p>
    <div class="pull-right">
    <label><fmt:message key="label.evaluations.use" /></label>&nbsp;<input type="checkbox" ng-model="formHasGrading" value="true" />
  </div>
  <button class="btn btn-primary" id="saveBtn" ng-click="save()"><fmt:message key="label.form.save" /></button>
  <button class="btn btn-default" data-toggle="modal" data-target="#addTab"><fmt:message key="label.tab.add" /></button>

</p>
  <p class="help-block" ng-if="!data.length">
      <fmt:message key="message.tabs.none" />
  </p>
  <ul class="nav nav-tabs">
    <li ng-repeat="tab in data" role="{{tab.slug}}" class="tab{{$index}} {{ $index==0?'active':'' }}"><a href="{{ '#' + tab.slug }}" ng-click="setTab(tab)" aria-controls="{{tab.slug}}" role="tab" data-toggle="tab">{{ tab.title | i18n }}</a></li>
  </ul>

    <!-- Tab panes -->
  <div class="tab-content">
    <div ng-repeat="tab in data" role="tabpanel" class="{{ 'tab-pane ' + ($index==0?'active':'') }}" id="{{tab.slug}}">
      <div class="row" style="padding-top:20px;">
        <div class="col-sm-10">
          <p class="help-block">
            {{tab.description | i18n}}
          </p>
        </div>
        <div class="col-sm-2">
          <button class="pull-right btn btn-default" data-toggle="modal" data-target="#editTab"><fmt:message key="label.tab.edit" /></button>
        </div>
      </div>
      <div class="fields">
        <div class="row" ng-repeat="field in tab.fields">
          <div class="col-sm-10">
            <field read-only="false" disabled="true" value="field" />
          </div>
          <div class="col-sm-2">
            <div class="dropdown" style="padding-top:32px;">
              <button ng-if="isMultipleChoice(field.type)" ng-click="showOptions(field)" class="pull-right btn btn-default dropdown-toggle" type="button" id="dropdownMenu1">
                <span class="glyphicon glyphicon-plus"></span>
              </button>

              <button ng-click="editField(field)" class="pull-right btn btn-default dropdown-toggle" type="button" id="dropdownMenu1">
                <span class="glyphicon glyphicon-pencil"></span>
              </button>

              <button ng-click="moveUpField(field)" class="pull-right btn btn-default dropdown-toggle" type="button" id="dropdownMenu1">
                <span class="glyphicon glyphicon-arrow-up"></span>
              </button>

              <button ng-click="moveDownField(field)" class="pull-right btn btn-default dropdown-toggle" type="button" id="dropdownMenu1">
                <span class="glyphicon glyphicon-arrow-down"></span>
              </button>
            </div>
          </div>
        </div>
      </div>
      <p style="padding-top:20px;">
        <button class="btn-default btn" data-toggle="modal" data-toggle="modal" data-target="#addField"><fmt:message key="label.field.add" /></button>
      </p>
    </div>

  <!-- Modal -->
  <div class="modal fade" id="addField" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title" id="myModalLabel"><fmt:message key="label.field.add" /></h4>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.title" /></label>
            <input ng-localized-string="fieldTitle" type="email" class="form-control title" placeholder="<fmt:message key='label.title' />">
          </div>

          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.key" /></label>
            <input ng-model="fieldKey" type="text" class="form-control key" placeholder="<fmt:message key='label.key' />">
          </div>

          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.type" /></label>

            <select ng-model="fieldType" class="form-control">
              <option ng-repeat="desc in description" value="{{desc.type}}">{{ desc.name | i18n }}</option>
            </select>
          </div>
          <div class="checkbox">
            <label>
              <input type="checkbox"> <fmt:message key="label.required" />
            </label>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.close" /></button>
          <button type="button" class="btn btn-primary" ng-click="addField()"><fmt:message key="label.create" /></button>
        </div>
      </div>
    </div>
  </div>

    <!-- Modal -->
  <div class="modal fade" id="editTab" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title" id="myModalLabel"><fmt:message key="label.tab.edit" /></h4>
        </div>
        <div class="modal-body">

          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.apply.to" /></label>
            <input ng-localized-string="currentTab.title" type="text" class="form-control" id="tabName" placeholder="<fmt:message key='label.apply.to' />">
          </div>

          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.slug" /></label>
            <input ng-model="currentTab.key" type="text" class="form-control" id="tabSlug" placeholder="<fmt:message key='label.slug' />">
          </div>

          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.description" /></label>
            <textarea ng-localized-string="currentTab.description" id="tabDescription" class="form-control"></textarea>
          </div>

        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-danger pull-left" ng-click="deleteTab()" data-dismiss="modal"><fmt:message key="label.delete" /></button>
          <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.close" /></button>
        </div>
      </div>
    </div>
  </div>

    <!-- Modal -->
  <div class="modal fade" id="addTab" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title" id="myModalLabel"><fmt:message key="label.tab.create" /></h4>
        </div>
        <div class="modal-body">

          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.title" /></label>
            <input ng-localized-string="tabName" type="text" class="form-control" id="tabName" placeholder="<fmt:message key='label.title' />">
          </div>

          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.slug" /></label>
            <input ng-model="tabSlug" type="text" class="form-control" id="tabSlug" placeholder="<fmt:message key='label.slug' />">
          </div>

          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.description" /></label>
            <textarea ng-localized-string="tabDescription" id="tabDescription" class="form-control"></textarea>
          </div>

        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.close" /></button>
          <button type="button" class="btn btn-primary" ng-click="addTab()"><fmt:message key="label.save.changes" /></button>
        </div>
      </div>
    </div>
  </div>



  <!-- Modal -->
  <div class="modal fade" id="editField" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title"><fmt:message key="label.field.edit" /></h4>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.title" /></label>
            <input ng-localized-string="fieldToEdit.label" type="email" class="form-control title" placeholder="<fmt:message key='label.title' />">
          </div>

          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.key" /></label>
            <input ng-model="fieldToEdit.key" type="text" class="form-control key" placeholder="<fmt:message key='label.key' />">
          </div>

          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.type" /></label>

            <select ng-model="fieldToEdit.type" class="form-control">
              <option ng-repeat="desc in description" value="{{desc.type}}">{{ desc.name | i18n }}</option>
            </select>
          </div>
          <div class="form-group">
            <div class="checkbox">
              <label>
                <input type="checkbox"> <fmt:message key="label.required" />
              </label>
            </div>
          </div>
          <div class="form-group">
            <label for="validatorInput"><fmt:message key="label.validator" /></label>

            <select ng-model="fieldToEdit.validator" class="form-control">
              <option value="undefined"><fmt:message key="label.none" /></option>
              <option ng-repeat="validator in validators" value="{{validator.slug}}">{{ validator.title | i18n }}</option>
            </select>

          </div>
          <h4 ng-show="formHasGrading"><fmt:message key="label.evaluation" /></h4>
          <div class="form-group" ng-show="formHasGrading && (fieldToEdit.type === 'text' || fieldToEdit.type === 'textarea')">
            <label><fmt:message key="label.answer.correct" /></label>
            <input class="form-control" type="text" ng-model="fieldToEdit.correctAnswer" />
          </div>

          <div class="form-group" ng-show="formHasGrading">
            <label><fmt:message key="label.answer.correct.worth" /></label>
            <input class="form-control" type="number" ng-model="fieldToEdit.worthCorrect" />
          </div>

          <div class="form-group" ng-show="formHasGrading">
            <label><fmt:message key="label.answer.incorrect.worth" /></label>
            <input class="form-control" type="number" ng-model="fiedlToEdit.worthIncorrect" />
          </div>
          
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-danger pull-left" ng-click="deleteField(fieldToEdit)" data-dismiss="modal"><fmt:message key="label.field.delete" /></button>
          <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.close" /></button>
        </div>
      </div>
    </div>
  </div>


  <!-- Modal -->
  <div class="modal fade" id="editOptions" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title"><fmt:message key="label.options.edit" /></h4>
        </div>
        <div class="modal-body">
          <p>
            <button data-toggle="modal" data-target="#addOption" class="btn btn-default"><fmt:message key="label.option.add" /></button>
          </p>
          <table class="table table-striped ">
            <tr ng-show="formHasGrading">
              <th colspan="3"></td>
              <th style="text-align: center"><fmt:message key="label.correct.answer" /></td>
            </tr>
            <tr ng-repeat="option in currentField.options">
              <td>
                {{option.label | i18n}}
              </td>
              
              <td>
                <code>{{option.key}}</code>
              </td>

              <td>
                <button ng-click="deleteOption(option)" class="pull-right btn btn-danger dropdown-toggle" type="button" id="dropdownMenu1">
                  <span class="glyphicon glyphicon-trash"></span>
                </button>

                <button ng-click="moveOptionUp(option)" class="pull-right btn btn-default dropdown-toggle" type="button" id="dropdownMenu1">
                  <span class="glyphicon glyphicon-arrow-up"></span>
                </button>

                <button ng-click="moveOptionDown(option)" class="pull-right btn btn-default dropdown-toggle" type="button" id="dropdownMenu1">
                  <span class="glyphicon glyphicon-arrow-down"></span>
                </button>
              </td>
              <td ng-show="formHasGrading" style="text-align: center">
                <input type="{{currentField.type === 'checkbox-group' ? 'checkbox' : 'radio'}}" name="currentField.key" ng-model="correctOption" value="true" />
              </td>
            </tr>
          </table>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.close" /></button>
        </div>
      </div>
    </div>
  </div>

      <!-- Modal -->
  <div class="modal fade" id="addOption" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title" id="myModalLabel"><fmt:message key="label.option.add" /></h4>
        </div>
        <div class="modal-body">

          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.option" /></label>
            <input ng-localized-string="optionName" type="text" class="form-control" id="tabName" placeholder="<fmt:message key='label.option' />">
          </div>

          <div class="form-group">
            <label for="exampleInputEmail1"><fmt:message key="label.slug" /></label>
            <input ng-model="optionSlug" type="text" class="form-control" id="tabSlug" placeholder="<fmt:message key='label.slug' />">
          </div>

        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="label.close" /></button>
          <button type="button" class="btn btn-primary" ng-click="addOption()"><fmt:message key="label.save.changes" /></button>
        </div>
      </div>
    </div>
  </div>

</div>
