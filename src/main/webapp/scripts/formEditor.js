var bennuToolkit = angular.module('bennuToolkit');

function toolkitDirective(name, widgetProvider) {
  bennuToolkit.directive(name, ['$timeout', function($timeout) {
    return {
      restrict: 'A',
      scope: {
        model: '=' + name
      },
      link: function(scope, el, attr) {
        el.hide();
        var handler = widgetProvider(el);
        scope.$watch('model', function(value) {
          if(value !== handler.get()) {
            handler.set(value);
          }
        });
        handler.onchange(function () {
          $timeout(function () {
            scope.model = handler.get();
          });
        });
      }
    }
  }]);
}

toolkitDirective('bennuDateTime', Bennu.datetime.createDateTimeWidget);
toolkitDirective('bennuDate', Bennu.datetime.createDateWidget);
toolkitDirective('bennuTime', Bennu.datetime.createTimeWidget);

var formEditor = angular.module('formsEdit', ['bennuToolkit',]);

formEditor.controller('FormEditorController', ['$scope','$http', function ($scope, $http) {
  $scope.data = [];
  $scope.description = [];

  $scope.validators = validators;
  
  $scope.fieldToEdit = {};

  $http.get(Bennu.contextPath + "/scripts/fields.json").then(function(e){
    $scope.description = e.data;
    $scope.data = data || [];
    $scope.currentTab = $scope.data[0];
  }, function(e){
    $.notify("Something happend", "error");
  });

  $scope.isMultipleChoice = function(fieldType){
    return $scope.description.filter(function(x){ 
      return x.type == fieldType; 
    })[0].multiple;
  }

  $scope.addTab = function(){
    var tab = {
        "title": $scope.tabName,
        "description": $scope.tabDescription,
        "slug": $scope.tabSlug,
        "fields": []
    }
    $scope.data.push(tab);
    if($scope.data.length == 1){
      $scope.currentTab = tab;
    }

    $scope.tabName = {};
    $scope.tabSlug = "";
    $scope.tabDescription = {};
    $("#addTab").modal("hide");
  };

  $scope.deleteTab = function(){
    var index = $scope.data.indexOf($scope.currentTab);
    if (index >= 0) {
      $scope.data.splice( index, 1 );
    }
    if ($scope.data.length){
      index = index==$scope.data.length?index-1:index;
      
      $scope.setTab($scope.data[index]);
      $(".tab" + index + " a").tab("show");  
    }else{
      $scope.setTab(null);
    }
    
  };

  $scope.showOptions = function(field){
    $scope.currentField = field;



    $("#editOptions").modal("show");
  };

  $scope.addOption = function(){
    $scope.currentField.options = $scope.currentField.options || []
    $scope.currentField.options.push({
      label:$scope.optionName,
      key:$scope.optionSlug
    });

    $scope.optionName = {};
    $scope.optionSlug = "";
    $("#addOption").modal("hide");
  };

  $scope.deleteOption = function(option){
    var index = $scope.currentField.options.indexOf(option);
    if (index > -1) {
      $scope.currentField.options.splice(index, 1);
    }
  };

  $scope.moveOptionUp = function(option){
    var index = $scope.currentField.options.indexOf(option), newPos = index-1;
    if(newPos == -1) {return;}
    move($scope.currentField.options, index, newPos);
  };

  $scope.moveOptionDown = function(option){
    var index = $scope.currentField.options.indexOf(option), newPos = index+1;
    if(newPos == $scope.currentField.options.length) {return;}
    move($scope.currentField.options, index, newPos);
  };

  $scope.setTab = function(tab){
    $scope.currentTab = tab;
    console.log(tab);
  };

  $scope.addField = function(){
    $scope.currentTab.fields.push({ "type": $scope.fieldType, "key": $scope.fieldKey, "label": $scope.fieldTitle });

    $scope.fieldType = "";
    $scope.fieldKey = "";
    $scope.fieldTitle = {};
    $("#addField").modal("hide");
  }

  var move = function(arr,old_index, new_index){
    if (new_index >= arr.length) {
        var k = new_index - arr.length;
        while ((k--) + 1) {
            arr.push(undefined);
        }
    }
    arr.splice(new_index, 0, arr.splice(old_index, 1)[0]);
    return arr; // for testing purposes
  }

  $scope.moveUpField = function(field){
      var index = $scope.currentTab.fields.indexOf(field), newPos = index-1;
      if(newPos == -1) {return;}
      move($scope.currentTab.fields, index, newPos);
  };

  $scope.moveDownField = function(field){
      var index = $scope.currentTab.fields.indexOf(field), newPos = index+1;
      if(newPos == $scope.currentTab.fields.length) {return;}
      move($scope.currentTab.fields, index, newPos);
  };

  $scope.editField = function(field){
      $scope.fieldToEdit = field
      $("#editField").modal("show");
  };

  $scope.deleteField = function(field){
    var index = $scope.currentTab.fields.indexOf(field);
    if (index > -1) {
      $scope.currentTab.fields.splice(index, 1);
    }
  };

  $scope.save = function(){
	var initialText = $("#saveBtn").text();
    $("#saveBtn").attr("disabled","disabled");
    $("#saveBtn").text("Saving ...");
    $http.post(window.location.pathname, $scope.data).then(function(){
      $.notify("Saved!", "success");
      $("#saveBtn").text(initialText);
      $("#saveBtn").attr("disabled",null);
    }, function(){
      $.notify("Something happend", "error");
      $("#saveBtn").text(initialText);
      $("#saveBtn").attr("disabled",null);
    });

  }
  
}]);

formEditor.directive('field', [ function() {

  return {

    restrict: 'E',

    scope: {
      ngModel: '=',
      value: '=',
      readOnly: '=',
      disabled: '='
    },

    templateUrl: Bennu.contextPath + '/scripts/field-directive.html',
    
    controller: ['$scope', '$element', '$attrs', function($scope, $element, $attrs) {
      $scope.window = window
      $scope.required = function(){ return false };

      $scope.shouldShow = function(){ return true };
    }],

    link: function(scope, element, attrs) {
      scope.errors = [];
      scope.getTemplate = function() {
        return Bennu.contextPath + '/scripts/field-'+scope.value.type+'.html';
      }
      scope.uploadFile = function(){}
      scope.validateIfNecessary = function() {}
    }
  }
}]);

formEditor.filter('optionlabel', function() {
  return function(value, options) {
    if(Array.isArray(options)){
      for (var i = 0; i < options.length; i++) {
        if (options[i].key == value){
          return options[i].label['pt-PT'];
        }
      }
      return value;
    }else{
      return options[value] ? options[value]['pt-PT'] : value;
    }
  };
});

