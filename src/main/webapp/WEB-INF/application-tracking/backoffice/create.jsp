<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%--@elvariable id="applicationTracking" type="org.fenixedu.applicationtracking.domain.ApplicationTracking"--%>

<h1><fmt:message key="label.periods.application.create" /></h1>
<ol class="breadcrumb">
	<li><a href="${pageContext.request.contextPath}/application-tracking"><fmt:message key="title.application.tracking" /></a></li>
	<li class="active"><fmt:message key="label.periods.application.create" /></li>
</ol>

<style>
    [ng-cloak] {
        display: none !important;
    }
</style>


<div ng-app="applications" ng-controller="CreatePeriodsController" ng-cloak>
    <div ng-show="!purpose">
        <div class="row">
            <div class="col-sm-9 col-sm-offset-3">
                <p class="lead"><fmt:message key="label.purpose.choose" /></p>
            </div>
        </div>
        <form class="form-horizontal" ng-submit="setPurpose(tmpPurpose)">
            <div class="form-group">
                <label class="control-label col-sm-3" for="purpose"><fmt:message key="label.purpose" /></label>
                <div class="col-sm-9">
                    <select ng-model="tmpPurpose" id="purpose" class="form-control"
                            ng-options="purpose.name for purpose in purposes track by purpose.id">
                    </select>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-9 col-sm-offset-3">
                    <button type="submit" ng-disabled="!tmpPurpose" class="btn btn-primary"><fmt:message key="label.next" /></button>
                </div>
            </div>
        </form>
    </div>
    <div ng-show="purpose">
        <h3>{{purpose.name}}</h3>
        <div ng-show="!template && !creatingFromScratch">
            <p>
                <fmt:message key="message.period.creation.options" />
            </p>
            <div class="row">
                <div class="col-sm-3">
                    <div class="panel panel-primary">
                        <div class="panel-heading">
                            <h4 class="panel-header">
                                <fmt:message key="label.period.create.new" />
                            </h4>
                        </div>
                        <div class="panel-body">
                            <p>
                                <fmt:message key="message.period.create.new" />
                            </p>

                            <p>
                                <button class="btn btn-default pull-right" ng-click="startPeriodCreation()"><fmt:message key="label.period.create.new" /></button>
                            </p>
                        </div>
                    </div>
                </div>
                <div ng-repeat="period in existingPeriods | orderBy: 'start'">
                    <div class="col-sm-3">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <h4 class="panel-header">
                                    {{period.name}}
                                </h4>
                            </div>
                            <div class="panel-body">
                                <p><strong><fmt:message key="label.period.start" />:</strong> {{period.start | date}}</p>
                                <p><strong><fmt:message key="label.period.end" />:</strong> {{period.end | date}}</p>

                                <p>
                                    <button class="btn btn-default pull-right" ng-click="setTemplate(period)"><fmt:message key="label.use.as.template" />
                                    </button>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div ng-show="template || creatingFromScratch">
            <p ng-show="template">
                <fmt:message key="label.period.create.copy" /> {{template.name}}
            </p>
            <p ng-show="creatingFromScratch">
                <fmt:message key="label.period.create.new" />
            </p>
            <form class="form-horizontal" ng-submit="createPeriod()">
                <div class="form-group" ng-show="template">
                    <label class="control-label col-sm-3" for="template"><fmt:message key="label.template" /></label>
                    <div class="col-sm-3">
                        <select class="form-control" ng-model="template" id="template"
                                ng-options="t.name for t in existingPeriods track by t.id"></select>
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-3" for="name"><fmt:message key="label.name" /></label>
                    <div class="col-sm-9">
                        <input type="text" ng-localized-string="newPeriod.name" id="name" class="form-control" required-any/>
                    </div>
                </div>
                <div class="form-group" ng-class="{'has-error': duplicateSlug}">
                    <label class="control-label col-sm-3" for="slug"><fmt:message key="label.slug" /></label>
                    <div class="col-sm-3">
                        <input type="text" ng-model="newPeriod.slug" ng-pattern="/^[a-zA-Z0-9\-]+$/" id="slug"
                               class="form-control"
                               required/>
                    </div>
                    <div class="col-sm-6" ng-show="duplicateSlug">
                        <em class="help-block"><fmt:message key="message.period.already.exists" /></em>
                    </div>
                    <div class="col-sm-6" ng-show="newPeriod.slug && !duplicateSlug">
                        <fmt:message key="label.period.url" />: <code>${portal.applicationUrl}/public-applications/{{newPeriod
                        .slug}}</code>
                    </div>
                    <div class="col-sm-6" ng-show="!newPeriod.slug">
                        <em><fmt:message key="error.slug.invalid" /></em>
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-3" for="start"><fmt:message key="label.period.start" /></label>
                    <div class="col-sm-3">
                        <input type="text" bennu-date-time="newPeriod.start" id="start" class="form-control" required/>
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-3" for="end"><fmt:message key="label.period.end" /></label>
                    <div class="col-sm-3">
                        <input type="text" bennu-date-time="newPeriod.end" id="end" class="form-control" required/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-9 col-sm-offset-3">
                        <button type="submit" class="btn btn-primary"><fmt:message key="label.create" /></button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

${portal.angularToolkit()}
<script src="${pageContext.request.contextPath}/scripts/patch-toolkit.js"></script>
<script>
    (function () {
        var base = '${pageContext.request.contextPath}/application-tracking';
        angular.module('applications', ['bennuToolkit']).controller('CreatePeriodsController', ['$scope', '$http', function ($scope, $http) {
            $http.get(base + '/purposes').success(function (data) {
                $scope.purposes = data;
            });
            $scope.setPurpose = function (purpose) {
                $scope.purpose = purpose;
                $http.get(base + '/find/' + purpose.id).success(function (data) {
                    $scope.existingPeriods = data;
                });
            };
            $scope.setTemplate = function (template) {
                $scope.template = template;
                $scope.newPeriod = {};
            };
            $scope.startPeriodCreation = function () {
                $scope.creatingFromScratch = true;
                $scope.newPeriod = {
                    purpose: $scope.purpose.id
                }
            };
            $scope.$watch('newPeriod.slug', function () {
                if ($scope.newPeriod) {
                    if ($scope.newPeriod.slug) {
                        $http.post(base + '/find?slug=' + $scope.newPeriod.slug).success(function (data) {
                            $scope.duplicateSlug = data.found;
                        });
                    } else {
                        $scope.duplicateSlug = false;
                    }
                }
            });
            $scope.$watch('newPeriod.name', function () {
                if ($scope.newPeriod) {
                    if ($scope.newPeriod.name && Bennu.localizedString.getContent($scope.newPeriod.name)) {
                        $scope.newPeriod.slug = Bennu.localizedString.getContent($scope.newPeriod.name).toString().toLowerCase()
                                .replace(/[àáâãåä]+/g, 'a')
                                .replace(/[èéêë]+/g, 'e')
                                .replace(/[ìíîï]+/g, 'i')
                                .replace(/[òóôõöőø]+/g, 'o')
                                .replace(/[ùúûüű]+/g, 'u')
                                .replace(/[ç]+/g, 'c')
                                .replace(/[ð]+/g, 'd')
                                .replace(/[ñ]+/g, 'n')
                                .replace(/[ýÿ]+/g, 'y')
                                .replace(/\s+/g, '-')
                                .replace(/[^\w\-]+/g, '')
                                .replace(/\-\-+/g, '-')
                                .replace(/^-+/, '')
                                .replace(/-+$/, '');
                    }
                }
            });
            $scope.createPeriod = function () {
                if ($scope.duplicateSlug) return;
                if ($scope.template) {
                    $scope.newPeriod.template = $scope.template.slug;
                }
                $http.post(base + '/create', $scope.newPeriod).success(function (data) {
                    location.href = base + '/' + $scope.newPeriod.slug;
                });
            }
        }]);
    })();
</script>
