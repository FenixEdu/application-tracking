

<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://fenixedu.org/functions" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="base" value="${pageContext.request.contextPath}/public-applications/${period.slug}/slots" scope="request" />

<t:applications>
  <jsp:body>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
		<%-- ${portal.angularToolkit()} --%>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/angular.js/1.4.8/angular.min.js"></script>
		${portal.angularToolkit().replaceAll('angular.min.js', 'xpto')}
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/danialfarid-angular-file-upload/11.0.1/ng-file-upload-shim.min.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/danialfarid-angular-file-upload/11.0.1/ng-file-upload.min.js"></script>

		<script type="text/javascript">

			var hodor = angular.module('HodorApp', ['bennuToolkit', 'ngFileUpload']);

			hodor.controller('MainCtrl', ['$scope', '$http', function($scope, $http) {


					$scope.steps = ${application}

					$scope.dataset = {};

					$scope.selectStep = function(step) {
						$scope.currentStep = step;
					};


				$scope.submitForm = function(){
					$http({
		          method  : 'POST',
		          url     : '${applicationUrl}/answert',
		          data    : { 'answers' : JSON.stringify($scope.dataset) },
		          headers : { 'Content-Type' : 'application/x-www-form-urlencoded' }
		        }).success(function(data){
		            if (data.errors) {
		              // Showing errors.
		              $scope.errorName = data.errors.name;
		              $scope.errorUserName = data.errors.username;
		              $scope.errorEmail = data.errors.email;
		            } else {
		              $scope.message = data.message;
		            }
		        });
		      };
			}]);

			hodor.directive('field', ['Upload', '$http', function(Upload, $http) {
				return {
					restrict: 'E',
					scope: {
						ngModel: '=',
						value: '='
					},

					templateUrl: 'scripts/field-directive.html',
					controller: ['$scope', '$element', '$attrs', function($scope, $element, $attrs) {

						var accessor = function helper(data, accessor) {
						    var keys = accessor.split('.'),
						        result = data;

						    while (keys.length > 0) {
						        var key = keys.shift();
						        if (typeof result[key] !== 'undefined') {
						            result = result[key];
						        }
						        else {
						            result = null;
						            break;
						        }
						    }

						    return result;
						};

						var predicator = function(attr, returnByDefault) {
							return function() {
								var ctx = $scope.$parent.$parent.dataset;
								if(ctx && $scope.value[attr]) {
									if($scope.value[attr].exists) {
										var key = $scope.value[attr].exists.key;
										var originalValue = accessor(ctx, key);
										return originalValue != undefined && originalValue != "";
									} else if($scope.value[attr].equals) {
										var key = $scope.value[attr].equals.key;
										var value = $scope.value[attr].equals.value;
										var originalValue = accessor(ctx, key);
										return originalValue === value;
									} else {
										return $scope.value[attr];
									}
								} else {
									return returnByDefault;
								}
							}
						};

						$scope.required = predicator('required', false);

						$scope.shouldShow = predicator('include-if', true);

					}],

					link: function(scope, element, attrs) {
						scope.errors = [];

						scope.getTemplate = function() {
							return '${pageContext.request.contextPath}/scripts/field-'+scope.value.type+'.html';
						};

						scope.uploadFile = function(file) {
							if(file) {
								if(scope.ngModel) {
									$http.delete("${url}/files/"+scope.ngModel);
								}
								Upload.upload({
									url: "${url}/files",
									data: { file: file }
								}).then(function (resp) {
						            console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
						            scope.ngModel = resp.data.id;
						        }, function (resp) {
						            console.log('Error status: ' + resp.status);
						        }, function (evt) {
						            var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
						            console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
						        });
							}
						};
						scope.validateIfNecessary = function() {
							if(scope.value.validator && typeof scope.value.validator === 'function') {
								scope.errors = scope.value.validator(scope.ngModel);
							}
						};
					}
				}}]);

		</script>
	</head>
	<body ng-app="HodorApp" ng-controller="MainCtrl">

		<form>
			<h2>{{application.title | i18n}}</h2>
			<ul>
				<li ng-repeat="step in steps" ng-click="selectStep($index)">({{step.slug}}) {{step.title | i18n }}</li>
			</ul>
			CurrentStep: {{currentStep}}
			<div ng-show="currentStep > -1" class="input-group" ng-repeat="field in steps[currentStep].fields | orderBy: 'index'">
				<field value="field" ng-model="dataset[steps[currentStep]['slug']][field.key]" />
			</div>
		</form>

		<h3><fmt:message key="label.json.to.send" /></h3>

		{{dataset}}

	</body>
</html>
