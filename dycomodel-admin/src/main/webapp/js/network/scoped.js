	
	function scopeRemoveSynapse(direction){
		direction.split('|')[0].split(',')[0]/1
		var appElement = document.querySelector('[ng-app=networksApp]');
		var appScope = angular.element(appElement).scope();
		appScope.removeSynapse(
				direction.split('|')[0].split(',')[0]/1,
				direction.split('|')[0].split(',')[1]/1,
				direction.split('|')[1].split(',')[0]/1,
				direction.split('|')[1].split(',')[1]/1,
				direction
				);

	}
	
	function scopeRemoveLayer(layer){
		var appElement = document.querySelector('[ng-app=networksApp]');
		var appScope = angular.element(appElement).scope();
		appScope.removeLayer(layer);
	}
	
	function scopeRemoveNeuron(layer,order, layerlength){
		if(layerlength && layerlength==0)
			return;
		var appElement = document.querySelector('[ng-app=networksApp]');
		var appScope = angular.element(appElement).scope();
		appScope.removeNeuron(layer,order);
	}	
	
	function scopeRemoveSynapses(){
		var appElement = document.querySelector('[ng-app=networksApp]');
		var appScope = angular.element(appElement).scope();
		appScope.removeSynapses();
	}
	
	function scopeSetDefault(type){
		var appElement = document.querySelector('[ng-app=networksApp]');
		var appScope = angular.element(appElement).scope();
		appScope.chengeType(type);		
	}