<%--@elvariable id="period" type="org.fenixedu.applicationtracking.domain.Period"--%>
<%--@elvariable id="applications" type="com.google.gson.JsonArray"--%>
<%--@elvariable id="states" type="com.google.gson.JsonArray"--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h1><fmt:message key="label.applications.view" /></h1>
<ol class="breadcrumb">
	<li><a href="${pageContext.request.contextPath}/application-tracking"><fmt:message key="title.application.tracking" /></a></li>
	<li><a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a></li>
	<li class="active"><fmt:message key="label.applications.view" /></li>
</ol>
<h3>
    <a href="${pageContext.request.contextPath}/application-tracking/${period.slug}"><c:out value="${period.name.content}"/></a>
</h3>

<c:if test="${empty period.applicationSet}">
    <div class="panel panel-default">
        <div class="panel-body">
            <fmt:message key="message.applications.none" />
        </div>
    </div>
</c:if>

<c:if test="${not empty period.applicationSet}">
    <style>
        [ng-cloak] {
            display: none !important;
        }
    </style>
    <div ng-app='applications' ng-cloak>
        <div ng-controller="ApplicationsController">
            <div class="row">
                <div class="col-sm-5">
                    <form action="${pageContext.request.contextPath}/application-tracking/${period.slug}/applications/export"
                          method="POST"
                          ng-submit="exportView()" style="display: inline">
                        <input type="hidden" name="numbers"/>
                        <button type="submit" class="btn btn-default" ng-disabled="filtered.length == 0"><span
                                class="glyphicon glyphicon-download-alt"></span>
                            <fmt:message key="label.export.excel" />
                        </button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/application-tracking/${period.slug}/calculateSeriation" style="display: inline">
                        <button type="submit" class="btn btn-default"><fmt:message key="label.seriation.calculate" /></button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/application-tracking/${period.slug}/applySeriation" style="display: inline">
                        <button type="submit" class="btn btn-default"><fmt:message key="label.seriation.apply" /></button>
                    </form>
                </div>
                <div class="col-sm-3" ng-show="basicSearch">
                    <input type="text" class="form-control" ng-model="filter" placeholder="<fmt:message key='label.search' />"/>
                </div>
                <div class="col-sm-2" ng-show="basicSearch">
                    <div class="dropdown">
                        <button type="button" class="btn btn-default" ng-click="showStateFilter = !showStateFilter">
                            <fmt:message key="label.state" /> <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" ng-show="showStateFilter" style="display: block">
                            <li ng-repeat="state in states">
                                <label class="checkbox-label">
                                    <input type="checkbox" ng-model="activeStates[state]" ng-change="changeFilter()">
                                    {{state}}
                                </label>
                            </li>
                        </ul>
                    </div>
                    <div class="dropdown">
                        <button type="button" class="btn btn-default" ng-click="showPaidFilter = !showPaidFilter">
                            <fmt:message key="label.paid" /> <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu" ng-show="showPaidFilter" style="display: block">
                            <li>
                                <label class="checkbox-label">
                                    <input type="checkbox" ng-model="paidFilter['true']" ng-change="changeFilter()">
                                    <fmt:message key="label.yes" />
                                </label>
                            </li>
                            <li>
                                <label class="checkbox-label">
                                    <input type="checkbox" ng-model="paidFilter['false']" ng-change="changeFilter()">
                                    <fmt:message key="label.no" />
                                </label>
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="col-sm-5" ng-show="!basicSearch">
                    <div ng-repeat="filter in advancedFilters">
                        <select class="form-control" ng-options="field as field.title for field in searchableFields"
                                ng-model="filter.field">
                            <option value="">--<fmt:message key="label.field.choose" />--</option>
                        </select>
                        <div ng-if="filter.field">
                            <label class="radio-inline">
                                <input type="radio" ng-model="filter.type" value="present"
                                       name="searchType-{{filter.field.name}}"><fmt:message key="label.filter.present" />
                            </label>
                            <label class="radio-inline">
                                <input type="radio" ng-model="filter.type" value="absent"
                                       name="searchType-{{filter.field.name}}"><fmt:message key="label.filter.absent" />
                            </label>
                            <label class="radio-inline" ng-if="filter.field.searchable">
                                <input type="radio" ng-model="filter.type" value="matches"
                                       name="searchType-{{filter.field.name}}"><fmt:message key="label.filter.matches" />
                            </label>
                            <input type="text" ng-model="filter.value" ng-if="filter.type == 'matches'">
                        </div>
                    </div>
                    <button ng-click="createNewFilter()" class="btn btn-default"
                            title="Add Filter"><span class="glyphicon glyphicon-plus"></span></button>
                    <button ng-click="performAdvancedSearch()" class="btn btn-default" ng-show="advancedFilters.length">
                            <span class="glyphicon glyphicon-search"></span> <fmt:message key="label.search" />
                    </button>
                </div>
                <div class="col-sm-2 text-right">
                    <button type="button" class="btn btn-default" ng-click="toggleAdvancedSearch()"
                            ng-class="{'active': !basicSearch}">
                        <span class="glyphicon glyphicon-search"></span> <fmt:message key="label.search.advanced" />
                    </button>
                </div>
            </div>
            <br/>
            <table class="table table-striped" ng-show="filtered.length > 0">
                <thead>
                    <tr>
                        <th><a href="" ng-click="setSort('number')"><fmt:message key="label.number" /></a></th>
                        <th><a href="" ng-click="setSort('creation')"><fmt:message key="label.date.creation" /></a></th>
                        <th><a href="" ng-click="setSort('name')"><fmt:message key="label.name" /></a></th>
                        <th><a href="" ng-click="setSort('state')"><fmt:message key="label.state" /></a></th>
                        <c:if test="${not empty period.purposeConfiguration.applicationFee}">
                            <th><a href="" ng-click="setSort('paid')"><fmt:message key="label.paid" /></a></th>
                        </c:if>
                        <th><a href="" ng-click="setSort('rank')"><fmt:message key="label.rank" /></a></th>
                        <th><a href="" ng-click="setSort('placement')"><fmt:message key="label.placement" /></a></th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <tr ng-repeat="application in filtered | skip: currentPage * perPage | limitTo: perPage">
                        <td><code>{{application.number}}</code></td>
                        <td>{{application.prettyCreation}}</td>
                        <td>{{application.name}}</td>
                        <td>{{application.state}}</td>
                        <c:if test="${not empty period.purposeConfiguration.applicationFee}">
                            <td>
                                <span ng-show="application.paid"><fmt:message key="label.yes" /></span>
                                <span ng-show="!application.paid"><fmt:message key="label.no" /></span>
                            </td>
                        </c:if>
                        <td>{{application.rank}}</td>
                        <td>{{application.placement}}</td>
                        <td>
                            <a ng-href="${pageContext.request.contextPath}/application-tracking/${period.slug}/applications/{{application.number}}"><fmt:message key="label.manage" /></a>
                        </td>
                    </tr>
                </tbody>
            </table>
            <div class="panel panel-default" ng-show="filtered.length == 0">
                <div class="panel-body">
                    <fmt:message key="message.no.match" />
                </div>
            </div>
            <div class="row">
                <div class="col-sm-4 form-inline">
                    <fmt:message key="label.per.page" />: <select ng-model="perPage" class="form-control">
                    <option>10</option>
                    <option>25</option>
                    <option>50</option>
                    <option>100</option>
                </select>
                </div>
                <div class="col-sm-4 text-center">
                    <ul class="pagination" ng-show="pages() > 1">
                        <li ng-click="previousPage()" ng-class="{disabled: currentPage <= 0}">
                            <a href>«</a>
                        </li>
                        <li class="disabled">
                            <a href>{{currentPage + 1}} / {{pages()}}</a>
                        </li>
                        <li ng-click="nextPage()" ng-class="{disabled: currentPage >= pages() - 1}">
                            <a href>»</a>
                        </li>
                    </ul>
                </div>
                <div class="col-sm-4">
                    <em class="pull-right"><fmt:message key="message.showing.applications" /></em>
                </div>
            </div>
        </div>
    </div>
</c:if>

<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular.min.js"></script>
<script>
    (function () {
        var base = "${pageContext.request.contextPath}/application-tracking/${period.slug}/applications";
        angular.module('applications', []).controller('ApplicationsController', ['$scope', '$filter',
            '$http', function ($scope, $filter, $http) {
                $scope.predicate = 'number';
                $scope.reverse = false;
                $scope.basicSearch = true;
                $scope.applications = ${applications};
                $scope.states = ${states};
                $scope.activeStates = {};
                $scope.paidFilter = {'true': true, 'false': true};
                $scope.states.map(function (el) {
                    $scope.activeStates[el] = true;
                });
                $scope.filtered = $scope.applications;
                $scope.perPage = 25;
                $scope.currentPage = 0;
                $scope.pages = function () {
                    return Math.ceil($scope.filtered.length / $scope.perPage);
                };
                $scope.applications.map(function (application) {
                    application.prettyCreation = $filter('date')(application.creation, 'yyyy-MM-dd HH:mm');
                });
                $scope.previousPage = function () {
                    if ($scope.currentPage > 0) $scope.currentPage--;
                };
                $scope.nextPage = function () {
                    if ($scope.currentPage < $scope.pages() - 1) $scope.currentPage++;
                };
                var resetPage = function () {
                    $scope.currentPage = 0;
                };
                $scope.changeFilter = function () {
                    resetPage();
                    var filtered = $filter('filter')($scope.applications, $scope.filter);

                    filtered = filtered.filter(function (el) {
                        return $scope.activeStates[el.state] && $scope.paidFilter[el.paid || 'false'];
                    });

                    $scope.filtered = $filter('orderBy')(filtered, $scope.predicate, $scope.reverse);
                };
                $scope.$watch('perPage', resetPage);
                $scope.$watch('filter', $scope.changeFilter);
                $scope.setSort = function (predicate) {
                    $scope.reverse = ($scope.predicate == predicate) ? !$scope.reverse : false;
                    $scope.predicate = predicate;
                    $scope.changeFilter();
                };
                $scope.exportView = function () {
                    $("input[name=numbers]").val($scope.filtered.map(function (el) {
                        return el.number;
                    }).join(","));
                };
                $scope.toggleAdvancedSearch = function () {
                    if ($scope.basicSearch) {
                        $scope.basicSearch = false;
                        if (!$scope.searchableFields) {
                            $http.post(base + '/searchable-fields').success(function (data) {
                                $scope.searchableFields = data;
                            });
                        }
                    } else {
                        $scope.basicSearch = true;
                    }
                };
                $scope.advancedFilters = [];
                $scope.createNewFilter = function() {
                    $scope.advancedFilters.push({'type': 'present'});
                };
                $scope.availableFields = function() {
                    // TODO Implement this
                    return $scope.searchableFields;
                };
                $scope.performAdvancedSearch = function() {

                };
            }]).filter('skip', function () {
            return function (input, count) {
                if (!input) return input;
                return input.slice(count);
            }
        });
        $("[title]").tooltip();
    })();
</script>

<style>
    .checkbox-label {
        width: 100%;
        cursor: pointer;
        padding: 4px 15px;
        color: #555;
        margin: 0;
    }

    .checkbox-label:hover, .checkbox-label:focus {
        text-decoration: none;
        background-color: #eee;
    }

    .dropdown {
        float: left;
        margin-right: 5px;
    }
</style>