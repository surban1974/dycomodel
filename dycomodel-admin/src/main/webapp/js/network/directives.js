app.directive('resize', function ($window) {
    return function (scope, element, attr) {
        angular.element($window).bind('resize', function () {
        	reDrawSynapses(scope);
        });
        
    }
}); 
app.directive('validNumber', function() {
    return function(scope, element, attrs) {
		var keyCode = [8,9,37,39,48,49,50,51,52,53,54,55,56,57,96,97,98,99,100,101,102,103,104,105,110,190,188];
	    element.bind("keydown", function(event) {
			if(keyCode.indexOf(event.which) == -1 || (event.target.value.indexOf('.')>-1 && event.which==190) || (event.target.value.indexOf('.')>-1 && event.which==190)){
	        	scope.$apply(function(){
	            	scope.$eval(attrs.onlyNum);
	                event.preventDefault();
	            });
	            event.preventDefault();
	        }
	    });
	    
	    element.bind("blur", function(event) {
			if(event.target.value==''){
//				event.target.value=event.target.value/1;
	            event.preventDefault();
	        }else{
	        	event.target.value=event.target.value/1;
	        	event.preventDefault();
	        }
	    });
	    
	};
 });
