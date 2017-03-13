
app.controller('networksCtrl', 	
	function($scope, $http, $timeout) {

		$scope.isPostRendered = false;
		$scope.isAddEvents = false;
		$scope.avoidUpdateDirty = 0;
		$scope.view = Object.create(GenericViewModel)
		.extend({
			post : function($http,newValue){
			},
			postDirty : function($http,newValue){
				if(!self.initialisation)
					$http.post('network-diff', JSON.stringify(newValue))
					.success(function(data, status, headers, config) {
					});
			}
		})
		.set(
				Object.create(GenericServerModel)
				.extend({
					activationFunctions:"",
					defaultNetwork:"",
					initWeight:"",
					algorithm:"",
					strategy:"",
					worker:{},
					network:{
						neurons : [],
						synapses : [],
						changed:0
				    },
				    selectedn:[],
				    
					
				    message : function(){
		    			if(this.error && this.error!=''){
		    				new clpopup({
		    					type: 'danger',
		    					message: this.error
		    				}).open();					 
		    			}
		    			if(this.success && this.success!=''){
		    				new clpopup({
		    					type: 'success',
		    					message: this.success
		    				}).open();					 
		    			}	
		    			if(this.info && this.info!=''){
		    				new clpopup({
		    					type: 'info',
		    					message: this.info
		    				}).open();					 
		    			}
		    			if(this.warning && this.warning!=''){
		    				new clpopup({
		    					type: 'warning',
		    					message: this.warning
		    				}).open();					 
		    			}
		    			return this;
				    }
				})
		);
		
		$scope.calculation = function() {

				if($scope.view.model.worker.executionState!=1){
					$timeout(function () {
					  	$http
					  	.post('network-exec')
					  	.success(function(data, status, headers, config) { 
					    	$scope.view.init(data);
						});
					},200);
				}
		
		}	
/*		
		$scope.changeInputValue = function(layer, order, output, prevOutput){
			console.log('INP changed '+new Date().getTime());
		}
*/		
		$scope.chengeType = function(type, prevType){
			if($scope.view.model.worker.executionState!=1){
				if(prevType)
					$scope.view.model.defaultNetwork = prevType;
				else if(type==$scope.view.model.defaultNetwork)
					return;
				

				var btnid = '_'+new Date().getTime();
				new clpopup({
					type: 'warning',
					footer: '<button id="btnUpdateType'+btnid+'" type="button" class="btn btn-danger" data-loading-text="Wait..." >Update</button>',
					message: 'This action will cause a loss of data. <font color="red"><b>Are you sure to change</b></font> the current network type?',
					success: function(){
						var instance = this;
						var btnUpdate = document.getElementById('btnUpdateType'+btnid);
						btnUpdate.addEventListener('click', function() {
						  	new Button(btnUpdate,'loading'); 
							$http
							.post('network-change',JSON.stringify({type:'type',value:type}))
							.success(function(data, status, headers, config) { 
								
								setPostRendered($scope,false);
						    	cleanSynapses($scope);
						    	$scope.view.model.network = {
									neurons : [],
									synapses : [] 
							    };
						    	$scope.view.init(data);
						    	
								new Button(btnUpdate,"reset");
								instance.close();
							});
						});
					}
				}).open();					 
				
				
			}
			
		}

//Layers		
		$scope.removeLayerPopup = function(layer,$event) {
			if($event.target){
				if($event.target.popover){
				}else{
					try{
						var popover = new Popover($event.target,
						{
							trigger:"click",
							template: '<div class="popover" role="tooltip">'
								  + '<div class="arrow"></div>'
								  + '<h3 class="popover-title">Remove layer '+layer+'</h3>'
								  + '<div class="popover-content"><center>'
								  + '<button  type="button"  data-toggle="button" data-loading-text="Confirm" class="btn btn-warning" onclick="scopeRemoveLayer('+layer+')">Confirm</button>'
								  + '</center></div>'
								  + '</div>'
						});
						$event.target.popover = popover;
						popover.open();
					}catch(e){
						
					}
						
				}
			}
		}
		$scope.removeLayer = function(layer) {
			if($scope.view.model.worker.executionState!=1){

				$http
					.post('network-removelayer',JSON.stringify({layer:layer}))
					.success(function(data, status, headers, config) { 
						setPostRendered($scope,false);
				    	cleanSynapses($scope);
				    	$scope.view.model.network = {
							neurons : [],
							synapses : [] 
					    };
				    	$scope.view.init(data);
	
					});	
			}
	    };
		$scope.addLayer = function() {
			if($scope.view.model.worker.executionState!=1){
			  	$http
			  	.post('network-addlayer')
			  	.success(function(data, status, headers, config) { 
			  		setPostRendered($scope,false);
			    	cleanSynapses($scope);
			    	$scope.view.model.network = {
						neurons : [],
						synapses : [] 
				    };
			    	$scope.view.init(data);

				});
			}
		}
	    
//Neuron
		$scope.removeNeuronPopup = function(layer,order,$event) {
			if($event.target){
				if($event.target.popover){
				}else{
					try{
						var popover = new Popover($event.target,
						{
							trigger:"click",
							template: '<div class="popover" role="tooltip">'
								  + '<div class="arrow"></div>'
								  + '<h3 class="popover-title">Remove last neuron from layer '+layer+'</h3>'
								  + '<div class="popover-content"><center>'
								  + '<button  type="button"  data-toggle="button" data-loading-text="Confirm" class="btn btn-warning" onclick="scopeRemoveNeuron('+layer+','+order+')">Confirm</button>'
								  + '</center></div>'
								  + '</div>'
						});
						$event.target.popover = popover;
						popover.open();
					}catch(e){
						
					}
						
				}
			}
		}	    
	    $scope.removeNeuron = function(layer,order) {
	    	if($scope.view.model.worker.executionState!=1){
					$http
					.post('network-removeneuron',JSON.stringify({layer:layer,order:order}))
					.success(function(data, status, headers, config) { 
						setPostRendered($scope,false);
				    	cleanSynapses($scope);
				    	$scope.view.model.network = {
							neurons : [],
							synapses : [] 
					    };
				    	$scope.view.init(data);
		
					});
	    	}	    	
	    }	 
	    $scope.addNeuron = function(layer) {
	    	if($scope.view.model.worker.executionState!=1){
				$http
				.post('network-addneuron',JSON.stringify({layer:layer}))
				.success(function(data, status, headers, config) { 
					setPostRendered($scope,false);
			    	cleanSynapses($scope);
			    	$scope.view.model.network = {
						neurons : [],
						synapses : [] 
				    };
			    	$scope.view.init(data);
	
				});
	    	}
	    	
	    }
	    
//Synapse	    
		$scope.removeSynapsePopup = function(synapse,$event) {
			if($event.target){
				if($event.target.popover){
				}else{
					if($scope.view.model.worker.executionState!=1){
						try{
							var popover = new Popover($event.target,
							{
								trigger:"click",
								template: '<div class="popover" role="tooltip">'
									  + '<div class="arrow"></div>'
									  + '<h3 class="popover-title">Remove synapse from ['+synapse.direction.split('|')[0]+'] to ['+synapse.direction.split('|')[1]+']</h3>'
									  + '<div class="popover-content"><center>'
									  + '<button  type="button"  data-toggle="button" data-loading-text="Confirm" class="btn btn-warning" onclick="scopeRemoveSynapse(\''+synapse.direction+'\')">Confirm</button>'
									  + '</center></div>'
									  + '</div>'
							});
							$event.target.popover = popover;
							popover.open();
						}catch(e){
							
						}
					}
				}
			}
			
		}

	    $scope.removeSynapses = function(layer,order) {
	    	if($scope.view.model.worker.executionState!=1){

	    		
					$http
					.post('network-removesynapses')
					.success(function(data, status, headers, config) { 
						setPostRendered($scope,false);
				    	cleanSynapses($scope);
				    	$scope.view.model.network = {
							neurons : [],
							synapses : [] 
					    };
				    	$scope.view.init(data);
		
					});
	    	}
	    	
	    }	   

		$scope.removeSynapse = function(fromlayer,fromorder,tolayer,toorder, direction){

	    	if($scope.view.model.worker.executionState!=1){
	    		
    			var target = document.getElementById('sl'+direction);
    			if(target && target.popover)
    				target.popover.close();
	    		
				var json = JSON.stringify(
						{
							from:{
								layer:fromlayer,
								order:fromorder
							},
							to:{
								layer:tolayer,
								order:toorder
							}
						}
						);
	    		
				$http
				.post('network-removesynapse',json)
				.success(function(data, status, headers, config) { 
					setPostRendered($scope,false);
			    	cleanSynapses($scope);
			    	$scope.view.model.network = {
						neurons : [],
						synapses : [] 
				    };
			    	$scope.view.init(data);
	
				});
	    	}			
		}	
		
		$scope.addSynapse = function(){
			if($scope.view.model.selectedn.length==2){
				var n0 = $scope.view.model.selectedn[0].replace('n','');
				var n1 = $scope.view.model.selectedn[1].replace('n','');
				var n0split = n0.split(',');
				var n1split = n1.split(',');
				
				var json = null;
				
				
				if(n0split[0]/1>n1split[0])
					json = JSON.stringify(
							{
								from:{
									layer:n1split[0],
									order:n1split[1]
								},
								to:{
									layer:n0split[0],
									order:n0split[1]
								}
							}
						);
				else
					json = JSON.stringify(
							{
								from:{
									layer:n0split[0],
									order:n0split[1]
								},
								to:{
									layer:n1split[0],
									order:n1split[1]
								}
							}
						);
				$http
				.post('network-addsynapse',json)
				.success(function(data, status, headers, config) { 
					setPostRendered($scope,false);
			    	cleanSynapses($scope);
			    	$scope.view.model.network = {
						neurons : [],
						synapses : [] 
				    };
			    	$scope.view.init(data);
	
				});				
				
			}
			return '';
		};
		
		$scope.getAddSynapseLabel = function(){
			if($scope.view.model.selectedn.length==2){
				var n0 = $scope.view.model.selectedn[0].replace('n','');
				var n1 = $scope.view.model.selectedn[1].replace('n','');
				var n0split = n0.split(',');
				var n1split = n1.split(',');
				
				for(var i in $scope.view.model.network.synapses){
					if($scope.view.model.network.synapses[i].direction==(n1+'|'+n0) || $scope.view.model.network.synapses[i].direction==(n0+'|'+n1))
						return '';
				}
				
				if(n0split[0]/1>n1split[0])
					return 'Add synapse from ['+n1+'] to ['+n0+']'
				else
					return 'Add synapse from ['+n0+'] to ['+n1+']'
			}
			return '';
		};
	    
		
		$http({
            method : 'GET',
            url : 'pages/included/top_menu.html'
	    }).success(function(data, status, headers, config) {
	    	document.getElementById("top_menu").innerHTML = data;
	    	document.getElementById("top_menu_networks").className += " active";
	    	
			$http({
	            method : 'POST',
	            url : 'network-json'
		    }).success(function(data, status, headers, config) {
		    	$scope.view.init(data);
		    	
		    	$scope.$watch('view', function(newValue, oldValue) {
		    		if($scope.isPostRendered==false){
						$timeout(function () {
					    	postRendering($scope,$http,$timeout);
					    	addEventToElements($scope,$http,$timeout);
					    	drawSynapseOnInit($scope);
					    });
		    		}else if($scope.view.model.worker && $scope.view.model.worker.executionState && $scope.view.model.worker.executionState==1){
		    			
		    		}else{
		    			$timeout(function () {
				    		var diff = newValue.getDirty(oldValue,function(property){
				    			if(property && 
				    				(
				    					property.indexOf('$$')==0 ||
				    					property=='error' ||
				    					property=='info' ||
				    					property=='warning'||
				    					property=='success' ||
				    					property=='drawcounter' ||
				    					property=='model.selectedn'
				    				)
				    			)
				    				return true;
				    			return false;
				    		});
							if(diff.length>0){
								if($scope.avoidUpdateDirty>0)
									$scope.avoidUpdateDirty = $scope.avoidUpdateDirty-1;
								else{
//									console.log('dirty'+diff.length+' tmp='+new Date().getTime());
									$scope.view.postDirty($http,diff);	
								}
							}
		    			});
		    		}
					
		  		},true);
		    });
	    	
	    	
	    });
});