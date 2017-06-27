var formEngine = angular.module('FormEngineApp', ['bennuToolkit', 'ngFileUpload']);

formEngine.controller('MainCtrl', ['$scope', '$http','$window', function($scope, $http, $window) {
    $scope.status = applicationStatus;
	$scope.user = user;
    $scope.status.slots.currentSlots = $scope.status.slots.maxSlots;
    
    $scope.getSlots = function(){
      if($scope.status.slots.currentSlots == 0) $scope.addSlot();
      return new Array($scope.status.slots.currentSlots);
    };
    
    $scope.getSelectedSlots = function(){
      return $scope.status.slots.selected;
    };
    
    $scope.getMaxSlots = function(){
    	return $scope.status.slots.maxSlots == 0?
    			Object.keys($scope.status.slots.available.slots).length :
    			Math.min(Object.keys($scope.status.slots.available.slots).length, $scope.status.slots.maxSlots);
    };
    
    $scope.addSlot = function(){
    	if ($scope.canAddSlots()) {
        	$scope.status.slots.currentSlots += 1;
        	$scope.status.slots.selected.push(new Array(1));
    	}
    };
    
    $scope.canAddSlots = function() {
    	return $scope.status.slots.maxSlots == 0 && $scope.status.slots.currentSlots < $scope.getMaxSlots();
    };

    $scope.getOrdinal = function(n) {
       var s=["th","st","nd","rd"],
           v=n%100;
       return n+(s[(v-20)%10]||s[v]||s[0]);
    };

    $scope.saveFormByBackoffice = function(){
        var purpose = $scope.status.steps.purpose.forms.map(function(x,i){
            var slug = x.slug
            var answer = $scope.status.steps.purpose.answers;
            answer.slug = slug;
            if (answer==null){return null;}
            return {
            	answers: answer,
            	forms: x
            };
         });
        
        var requirements = $scope.status.steps.requirements.map(function(x,i){
            var slug = x.forms[0].slug
            var answer = $scope.status.steps.requirements[i].answers;
            if (answer[slug] == null) {
            	answer[slug] = {
            		fields: {}
            	};
            }
            answer[slug].slug = slug;
            return {
              answers: answer,
              forms: x.forms
            };
        });
        var answers = {
    		purpose: purpose[0],
    		requirements: requirements
        };
        
        $http({
            method  : 'POST',
            url     : window.location.pathname + '/answer',
            data    : { slots: $scope.status.slots.selected, formsAnswer: answers },
        }).success(function(data){
        	
        });
    };
    
    $scope.saveUser = function(){
    	if ($scope.user["email"] == null) $scope.user["email"] = "";
    	if ($scope.user["givenNames"] == null) $scope.user["givenNames"] = "";
    	if ($scope.user["familyNames"] == null) $scope.user["familyNames"] = "";
        $http({
            method  : 'POST',
            url     : window.location.pathname + '/user',
            data    : $scope.user,
        }).success(function(data){
        });
    };
    
    $scope.saveSlots = function(){
        $http({
            method  : 'POST',
            url     : window.location.pathname + '/answer',
            data    : { slots: $scope.status.slots.selected },
        }).success(function(data){
        	location.reload();
        });
    };

    $scope.saveForm = function(){
      var answers = $scope.status.steps.forms.map(function(x,i){
        var slug = x.slug
        var answer = $scope.status.steps.answers[slug]
        if (answer==null){return null;}
        return {
          slug: slug,
          fields: answer.fields
        };
      }); 
      $http({
        method  : 'POST',
        url     : window.location.pathname + '/answer',
        data    : { slots: $scope.status.slots.selected, formsAnswer:answers },
      }).success(function(data){

      });
    };

    $scope.submitForm = function(){
      var answers = $scope.status.steps.forms.map(function(x,i){
        var slug = x.slug
        var answer = $scope.status.steps.answers[slug].fields
        if (answer==null){return null;}
        return {
          slug: slug,
          fields: answer
        };
      });
      $http({
        method  : 'POST',
        url     : window.location.pathname + '/submit',
        data    : { slots: $scope.status.slots.selected, formsAnswer: answers},
      }).success(function(data){
    	  location.reload();
      });
    };
}]);

formEngine.directive('field', ['Upload', '$http', '$window', function(Upload, $http, $window) {

  return {
    restrict: 'E',
    scope: {
      ngModel: '=',
      value: '=',
      readOnly: '=',
      disabled: '='
    },
    templateUrl: Bennu.contextPath + '/scripts/field-directive.html',
    controller: ['$scope', '$element', '$attrs', '$window', function($scope, $element, $attrs, $window) {

      $scope.window = window
      
      var accessor = function helper(data, accessor) {

          var keys = accessor.split('.');
          var formKey = keys.shift();
          var i;
          for (i = 0; i < data.steps.forms.length; i++) {
            var form = data.steps.forms[i]
            if(form.slug == formKey){
              break;
            }
          }

          result = data.steps.answers[i];
          while (keys.length > 0 && result) {
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
          var ctx = $scope.$parent.$parent.status;
          if(ctx.steps.answers && $scope.value[attr]) {
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
        return Bennu.contextPath + '/scripts/field-'+scope.value.type+'.html';
      };
      scope.downloadFile = function(downloadUrl) {
        window.location = downloadUrl;
      };
      scope.uploadFile = function(file) {
        if(file) {
          if(scope.ngModel) {
            $http.delete($window.location.pathname+"/files/"+scope.ngModel);
          }
          Upload.upload({
            url: $window.location.pathname+"/files",
            data: { file: file }
          }).then(function (resp) {
            console.log(resp.data);
                  console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
                  scope.ngModel = resp.data;
              }, function (resp) {
                  console.log('Error status: ' + resp.status);
              }, function (evt) {
                  var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                  console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
              });
        }
      };
      scope.validateIfNecessary = function() {
        if(scope.value.validator) {
          var validator = validators[scope.value.validator];
          eval(validators[scope.value.validator].code);
          console.log(validators[scope.value.validator].code);
          if(!validate(scope.ngModel)) {
            scope.errors.push(validators[scope.value.validator].errorMessage);
          }
        }
      };
    }
}}]);

formEngine.directive("slotSelection", function() {
    return {
        scope: {
            "data": "=",
            "pos": "=",
            "writeback": "="
        },
        controller: ["$scope", function($scope) {

            if ($scope.writeback.length>0){
                $scope.selectedOption = $scope.data.slots[$scope.writeback[$scope.pos]];
            }

            $scope.onSelectOption = function() {
                if(!$scope.selectedOption) {
                    delete $scope.writeback[$scope.data.key];
                } else {
                    $scope.writeback[$scope.pos] = $scope.selectedOption.code;
                }
            }
        }],
        templateUrl: function(){
            return Bennu.contextPath + '/scripts/slot-selection-recursive.html';
        }
    };
});

formEngine.filter('optionlabel', function() {
  return function(value, options) {
    if(Array.isArray(options)){
      for (var i = 0; i < options.length; i++) {
        if (options[i].key == value){
          return options[i].label[Bennu.locale.tag];
        }
      }
      return value;
    }else{
      return options[value] ? options[value][Bennu.locale.tag] : value;
    }
  };
});

formEngine.filter('slotslabel', function() {
  return function(value, options) {
    var options = options.slots;
    var result = [];
    for (var i = 0; i < value.length; i++) {
      var option = options[value[i]]
      result.push(option.name[Bennu.locale.tag]);
      if (options[value[i]].children){
        options = options[value[i]].children.slots;
      }
    }
    if(result){
      return result.join(" \u2022 ");  
    }else{
      return ""
    }
    
  };
});

