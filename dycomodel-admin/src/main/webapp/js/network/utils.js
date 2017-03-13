	
	function drawSynapseOnInit($scope){
		for(var i in $scope.view.model.selectedn){
			document.getElementById($scope.view.model.selectedn[i]).click();
		}
	}
	
	function drawSynapses(neuron,$scope,show){
		if(neuron && neuron.id){
			var id = neuron.id.replace('n','');

			
			var selected = [];
			for(var i in $scope.view.model.network.synapses){
				var synapse = $scope.view.model.network.synapses[i];
				if(synapse.direction){
					if(synapse.direction.indexOf(id)>-1)
						selected.push(synapse);
				}					
			}
			for(var i in selected){
				var synapse = selected[i];
				if(synapse.direction){
					if(!synapse['drawcounter'])
						synapse['drawcounter'] = 0;					
					drawLineAndLabel(synapse, show);
				}
				
			}
		}		
	}
	
	
	function reDrawSynapses($scope){
		var selected = [];
		for(var i in $scope.view.model.network.synapses){
			var synapse = $scope.view.model.network.synapses[i];
			if(synapse['drawcounter'] && synapse['drawcounter']>0)
				selected.push(synapse);
									
		}
		for(var i in selected){
			var synapse = selected[i];
			if(synapse.direction){
				drawLineAndLabel(synapse, true, true);
			}
			
		}
	}	
	
	function cleanSynapses($scope){
		var selected = [];
		for(var i in $scope.view.model.network.synapses){
			var synapse = $scope.view.model.network.synapses[i];
//			if(synapse['drawcounter'] && synapse['drawcounter']>0)
				selected.push(synapse);
									
		}
		for(var i in selected){
			var synapse = selected[i];
			if(synapse.direction){
				drawLineAndLabel(synapse, false, false, true);
			}
			
		}
	}	

	
	function drawLineAndLabel(synapse, show, redraw, ignoreDrawcounter){
		var res = synapse.direction.split('|');
		var from = document.getElementById('n'+res[0]);
		var to = document.getElementById('n'+res[1]);
		if(from && to){
			var id_synapse = 's'+synapse.direction;
			var label = document.getElementById('sl'+synapse.direction);

			if(show){
				if(synapse.drawcounter==0 || (redraw && redraw==true)){
					var rectFrom = from.getBoundingClientRect();
					var rectTo = to.getBoundingClientRect();
					
					var centerFromX = rectFromX = rectFrom.left+rectFrom.width/2 + ((window.scrollX)?window.scrollX:document.documentElement.scrollLeft);
					var centerFromY = rectFromY = rectFrom.top + rectFrom.height/2 + ((window.scrollY)?window.scrollY:document.documentElement.scrollTop);
					var centerToX = rectToX = rectTo.left+rectTo.width/2 + ((window.scrollX)?window.scrollX:document.documentElement.scrollLeft);
					var centerToY = rectToY =rectTo.top + rectTo.height/2 + ((window.scrollY)?window.scrollY:document.documentElement.scrollTop);
					
					var alfa = Math.atan((rectFromY - rectToY)/(rectFromX - rectToX));
					var radius = 12.5;
					
					if(rectFromX <= rectToX){
						rectFromX = rectFromX + Math.cos(alfa)*radius;
						rectToX = rectToX - Math.cos(alfa)*radius;
						rectFromY = rectFromY + Math.abs(Math.sin(alfa)*radius);
						rectToY = rectToY  - Math.abs(Math.sin(alfa)*radius);
					}else if(rectFromX > rectToX){
						rectFromX = rectFromX - Math.cos(alfa)*radius;
						rectToX = rectToX + Math.cos(alfa)*radius;
						rectFromY = rectFromY + Math.abs(Math.sin(alfa)*radius);
						rectToY = rectToY - Math.abs(Math.sin(alfa)*radius);									
					}
					
					var maxZIndex = 0;
					var line;
					if(redraw && redraw==true){
						var element = document.getElementById(id_synapse);
						if(element)
							element.parentNode.removeChild(element);
					}
					
					document.body.appendChild(
						line = createLine(
								id_synapse,
								rectFromX,
								rectFromY,
								rectToX,
								rectToY
						)
					);
					
					
					line.addEventListener("mouseover", function(){
						if(label)
							addClass(label,'synapse-hover');
						try{
							if(this.popover)	
								this.popover.removePopover();
							
							var popover = new Popover(this,
								{
								template: '<div class="popover" role="tooltip">'
									  + '<div class="arrow"></div>'
									  + '<h3 class="popover-title">Synapse</h3>'
									  + '<div class="popover-content">'
									  + '<i>from:</i>&nbsp;neuron ['+res[0]+']<br/>'
									  + '<i>to:</i>&nbsp;neuron ['+res[1]+']<br/>'
									  + '<i>weight:</i>&nbsp;'+synapse.weight+'<br/>'
									  + '</div>'
									  + '</div>'
							});	
							this.popover = popover;
						}catch(e){
							
						}
						
						
					});
					line.addEventListener("mouseout", function(){
						removeClass(label,'synapse-hover');
						try{
							if(this.popover)	
								this.popover.removePopover();
						}catch(e){
							
						}

					});					
					
					if(line && line.style && line.style.zIndex){
						if(maxZIndex<line.style.zIndex)
							maxZIndex = line.style.zIndex;
					}

					
					if(label){
						label.style.position = 'absolute';
						label.style.display = 'block';

						
						var rectLabel = label.getBoundingClientRect();
						var centerLabelX = Math.min(centerFromX,centerToX) + Math.abs(centerFromX-centerToX)/2 ;
						var centerLabelY = Math.min(centerFromY,centerToY) + Math.abs(centerFromY-centerToY)/2 ;
						
						label.style.left = (centerLabelX-label.getBoundingClientRect().width/2)+'px';
						label.style.top = (centerLabelY-label.getBoundingClientRect().height/2)+'px';
						
						label.style.zIndex = (maxZIndex+1);
					}
					

				}
				if(redraw && redraw==true){
					
				}else
					synapse.drawcounter=synapse.drawcounter+1;
			}else
				synapse.drawcounter=synapse.drawcounter-1;
			
			if(synapse.drawcounter<=0 || (ignoreDrawcounter && ignoreDrawcounter==true)){
				var element = document.getElementById(id_synapse);
				if(element)
					element.parentNode.removeChild(element);	
				synapse.drawcounter=0;
				if(label)
					label.style.display = 'none';
			}
		}

	}
