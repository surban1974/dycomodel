	function postRendering($scope,$http,$timeout){
		
		[].forEach.call(
				document.getElementsByClassName('neuron'),
				function(e){
					if(e.id && e.id.indexOf('n0,')==0)
						document.getElementById(e.id.replace('n0,','i0,')).style.display='block';
					
					e.addEventListener(
						'click',
						function() {
						  	if(this.className){
						  		if(this.className=='neuron'){
						  			this.className='neuron-expand';	
						  			drawSynapses(this,$scope,true);
									$http.post(
											'network-selectneuron',
											JSON.stringify({neuron:{layer:this.id.replace('n','').split(',')[0]/1,order:this.id.replace('n','').split(',')[1]/1},sel:true})
									)
									.success(function(data, status, headers, config) { 
										$scope.view.model.selectedn = data.selectedn;
//										$scope.avoidUpdateDirty=$scope.avoidUpdateDirty+1;
									}
									);
						  		}else if(this.className=='neuron-expand'){
						  			this.className='neuron';
						  			drawSynapses(this,$scope,false);
									$http.post(
											'network-selectneuron',
											JSON.stringify({neuron:{layer:this.id.replace('n','').split(',')[0]/1,order:this.id.replace('n','').split(',')[1]/1},sel:false})
									)
									.success(function(data, status, headers, config) { 
										$scope.view.model.selectedn = data.selectedn;
//										$scope.avoidUpdateDirty=$scope.avoidUpdateDirty+1;
									}
									);									
						  		}

						  	}
						},
						false);
					e.addEventListener(
							'mouseover',
							function() {
								try{
									if(this.popover)	
										this.popover.removePopover();
									
									var popover = new Popover(this);	
									this.popover = popover;
								}catch(e){
									
								}
							},
							false)
				}
		);

		

		setPostRendered($scope,true);

		
	}
	
	function setPostRendered($scope,val){
		$scope.isPostRendered = val;
		if(val==true){
			window.setTimeout(function(){
				document.getElementById("waiter").style.visibility='hidden';
			},400);
			if(document.getElementById("canvas").style.display=='none')
				document.getElementById("canvas").style.display='block';
		}else
			document.getElementById("waiter").style.visibility='visible';
		
	}
	
	function addEventToElements($scope,$http,$timeout){
		
		if($scope.isAddEvents==false){
		
			var btnLoad = document.getElementById('loadButton');
			btnLoad.addEventListener('click', function() {
				
			  	new Button(btnLoad,'loading'); 
			  	$http({
					url:'network-restart',
					method : 'POST'
			    }).success(function(data, status, headers, config) { 
			    	$scope.view.init(data);
			    	if($scope.view.model.worker && $scope.view.model.worker.executionState){
			    		if($scope.view.model.worker.executionState==1){		    			
			    			$timeout(function () {
			    				queryWorker($scope,$http,$timeout);
			    				addEventToHideElements($scope,$http,$timeout);
			    			});
	
			    		}else
			    			new Button(btnLoad,"reset");
	
			    	}else
						new Button(btnLoad,"reset");
				});
			});	
			
			var btnReset = document.getElementById('resetButton');
			btnReset.addEventListener('click', function() {
				if($scope.view.model.worker.executionState!=1){
				  	new Button(btnReset,'loading'); 
				  	$http({
						url:'network-reset',
						method : 'POST'
				    }).success(function(data, status, headers, config) { 
				    	setPostRendered($scope,false);
				    	cleanSynapses($scope);
				    	$scope.view.model.network = {
							neurons : [],
							synapses : [] 
					    };
				    	$scope.view.init(data);
		
		//		    	isPostRendered==true;
						new Button(btnReset,"reset");
					});
				}
			});			
	
			var btnData = document.getElementById('dataButton');
			btnData.addEventListener('click', function() {
			  	new Button(btnData,'loading'); 
	
				$http({
					url:'network-viewdata',
					method : 'POST'
			    }).success(function(data, status, headers, config) { 
			    	var btnid = '_'+new Date().getTime();
			    	var txt = '<textarea id="txtUpdateData'+btnid+'" style="width:100%;height:400px;white-space: pre; word-wrap: normal;">';
			    	if(data){
			    		[].forEach.call(
			    				data.data,
			    				function(d){
			    					for(var i=0;i<d.length;i++)
			    						txt+=d[i]+';';
			    					txt+='\n';	
			    				}
			    			);
			    	}
			    	txt+='</textarea>';
					new clpopup({
						type: 'info',
						footer: '<button id="btnUpdateData'+btnid+'" type="button" class="btn btn-danger" data-loading-text="Wait..." >Update</button>',
						message: txt,
						success: function(){
							var instance = this;
							var btnUpdate = document.getElementById('btnUpdateData'+btnid);
							btnUpdate.addEventListener('click', function() {
							  	new Button(btnUpdate,'loading'); 
								$http
								.post('network-updatedata',JSON.stringify({data:document.getElementById('txtUpdateData'+btnid).value}))
								.success(function(data, status, headers, config) { 
									new Button(btnUpdate,"reset");
									instance.close();
								});
							});
						}
					}).open();					 
				 
	
					new Button(btnData,"reset");
				});
			});
			
			var btnPreview = document.getElementById('previewButton');
			btnPreview.addEventListener('click', function() {
			  	new Button(btnPreview,'loading'); 
	
				$http({
					url:'network-viewnetwork',
					method : 'POST'
			    }).success(function(data, status, headers, config) { 
			    	
			    	var btnid = '_'+new Date().getTime();
			    	var txt = ''
			    	if(data){
			    		txt+=data.network;	
			    		txt=txt.replace(/\\n/g, '\n').replace(/\\r/g, '\r').replace(/\\t/g, '\t').replace(/\\"/g, '"');
			    		txt=txt.replace(/\\/g, '');
			    	}
			    	txt='<textarea  id="txtUpdateNetwork'+btnid+'" style="width:100%;height:400px;white-space: pre; word-wrap: normal;">'+txt+'</textarea>';
			    	
					new clpopup({
						type: 'info',
						footer: '<button id="btnUpdateNetwork'+btnid+'" type="button" class="btn btn-danger" data-loading-text="Wait..." >Update</button>',
						message: txt,
						success: function(modal){
							var instance = this;
							var btnUpdate = document.getElementById('btnUpdateNetwork'+btnid);
							btnUpdate.addEventListener('click', function() {
							  	new Button(btnUpdate,'loading'); 
					
								$http
								.post('network-updatenetwork',JSON.stringify({networksource:document.getElementById('txtUpdateNetwork'+btnid).value}))
								.success(function(data, status, headers, config) { 
									new Button(btnUpdate,"reset");
									instance.close();
									if(data.model.error && data.model.error!=""){
								    	$scope.view.init(data);
								    	$scope.view.message();
									}else{
										
								    	setPostRendered($scope,false);
								    	cleanSynapses($scope);
								    	$scope.view.model.network = {
											neurons : [],
											synapses : [] 
									    };
								    	$scope.view.init(data);
								    	

								    	
//								    	setPostRendered(true);
									}
								});
							});
						}
					}).open();					 
	
					new Button(btnPreview,"reset");
				});
			});
			
			var btnAInfo = document.getElementById('infoAButton');
			btnAInfo.addEventListener('click', function() {
			  	$http({
					url:'network-ainfo',
					method : 'POST'
			    }).success(function(data, status, headers, config) { 

					var tmplt = '<div class="popover" role="tooltip">'
							  + '<div class="arrow"></div>'
							  + '<h3 class="popover-title">Algorithm '+data.algorithm.id+'</h3>'
							  + '<div class="popover-content">';
							  
				  	for(var property in data.algorithm){
						if (property!='id' &&
							property!='definition' &&	
							typeof data.algorithm[property] != 'function' &&
							typeof data.algorithm[property] != 'object')
								tmplt+='<i>'+property+'</i>:'+data.algorithm[property]+'<br/>';
					}
							  
					tmplt+='</div></div>';  
					
					if(btnAInfo.popover){
//						btnAInfo.popover.close();
						btnAInfo.popover.options.template = tmplt;
					}else{	
						try{
							var popover = new Popover(btnAInfo,
							{
								trigger:"click",
								template: tmplt
							});
							btnAInfo.popover = popover;
							popover.open();
						}catch(e){
							
						}
					}
				});
			});				
			
			var selectActivationFunctions = document.getElementById('activationFunctions');
			if(selectActivationFunctions){
				selectActivationFunctions.value = $scope.view.model.activationFunctions;
				selectActivationFunctions.addEventListener('change', function() {
					if($scope.view.model.worker.executionState!=1){
						$http
						.post('network-change',JSON.stringify({type:'activationFunctions',value:selectActivationFunctions.value}))
						.success(function(data, status, headers, config) { 
					    	$scope.view.init(data);
					    	$scope.view.message();
						});
					}
				}
				);
			}
			
			
			var selectTrainingStrategy = document.getElementById('trainingStrategy');
			if(selectActivationFunctions){
				selectTrainingStrategy.value = $scope.view.model.strategy;
				selectTrainingStrategy.addEventListener('change', function() {
					if($scope.view.model.worker.executionState!=1){
						$http
						.post('network-change',JSON.stringify({type:'trainingStrategy',value:selectTrainingStrategy.value}))
						.success(function(data, status, headers, config) { 
					    	$scope.view.init(data);
					    	$scope.view.message();
						});
					}
				}
				);
			}
			
			var selectTrainingAlgorithm = document.getElementById('trainingAlgorithm');
			if(selectTrainingAlgorithm){
				selectTrainingAlgorithm.value = $scope.view.model.algorithm;
				selectTrainingAlgorithm.addEventListener('change', function() {
					if($scope.view.model.worker.executionState!=1){
						$http
						.post('network-change',JSON.stringify({type:'trainingAlgorithm',value:selectTrainingAlgorithm.value}))
						.success(function(data, status, headers, config) { 
					    	$scope.view.init(data);
					    	$scope.view.message();
						});
					}
				}
				);
			}
			
			var selectinitWeight = document.getElementById('initWeight');
			if(selectinitWeight){
				selectinitWeight.value = $scope.view.model.initWeight;
				selectinitWeight.addEventListener('change', function() {
					if($scope.view.model.worker.executionState!=1){
						$http
						.post('network-change',JSON.stringify({type:'initWeight',value:selectinitWeight.value}))
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
				);
			}			
			
			if($scope.view.model.worker.executionState==1){		    			
				$timeout(function () {
					new Button(document.getElementById('loadButton'),'loading'); 
					queryWorker($scope,$http,$timeout);
					addEventToHideElements($scope,$http,$timeout);
				});
	
			}else
				addEventToHideElements($scope,$http,$timeout);
			
			$scope.isAddEvents=true;
		}
		
	}

	function addEventToHideElements($scope,$http,$timeout){
		var btnStop = document.getElementById('stopButton');
		if(btnStop){
			btnStop.addEventListener('click', function() {
			  	new Button(btnStop,'loading'); 
			  	$http({
					url:'network-stop',
					method : 'POST'
			    }).success(function(data, status, headers, config) { 
			    	$scope.view.init(data);
					new Button(btnStop,"reset");
				});
			});
		}
	
	}	
	
	function queryWorker($scope,$http,$timeout){
		var btnLoad = document.getElementById('loadButton');
		$timeout(function () {
		  	$http({
				url:'network-check',
				method : 'POST'
		    }).success(function(data, status, headers, config) { 
		    	$scope.view.init(data);
		    	if($scope.view.model.worker && $scope.view.model.worker.executionState){
		    		if($scope.view.model.worker.executionState==1)
		    			queryWorker($scope,$http,$timeout)	
		    		else
		    			new Button(btnLoad,"reset");
		    	}else
					new Button(btnLoad,"reset");
			});

    	},
    	500);

	}	
