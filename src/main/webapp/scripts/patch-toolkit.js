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