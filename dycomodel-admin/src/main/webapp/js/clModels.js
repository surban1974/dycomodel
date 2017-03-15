/**
* Name: classhidra-models.js
* Version: 1.1.2 
* Creation date: (19/05/2016)
* @author: Svyatoslav Urbanovych svyatoslav.urbanovych@gmail.com
*/
var GenericModel = {
	extend: function(props) {
	    for(var prop in props) {
	        if(props.hasOwnProperty(prop)) {
	            this[prop] = props[prop];
	        }
	    }
	    return this;
	}	
}

var GenericServerModel = Object.create(GenericModel);
GenericServerModel.extend(
{
	error : '',
	warning : '',
	info : '',
	success : '',
	
		
	init: function(model){
		json2model(this,model);
		return this;
	},

	
	message : function(){
		
		if(this.error && this.error!=''){
			alert("ERROR "+this.error);					 
		}
		if(this.success && this.success!=''){
			alert(this.success);	
		}	
		if(this.info && this.info!=''){
			alert("INFO: "+this.info);
		}
		if(this.warning && this.warning!=''){
			alert("WARNING: "+this.warning);
		}
			
		return this;
	}					
}
);


var GenericViewModel = Object.create(GenericModel);
GenericViewModel.extend(
{
		initialisation : true,
		model : null,
			
		set : function(serverModel){
			this.model = serverModel;
			return this;
		},
		
		init : function(parsed){
			this.initialisation = true;
			try{
				this.model.init(parsed.model);
			}catch(e){
				showException(e);
			}
			this.initialisation = false;	
			return this;
		},
		
		post : function($http,newValue){
		},

		postDirty : function($http,newValue){
		},		
		
		getDirty : function(oldModel,exclude,include){
			var result = [];
			dirtyModelElements(this,oldModel,result,null,exclude,include);
			return result;
		},
		
		message : function(){
			this.model.message();
			return this;
		}		
}
);


function json2model(source,json){
	if(isEmpty(source))
		source = json;
	else{
		for(var property in source){
//			if(source.hasOwnProperty(property)){
				if (typeof json[property] === "undefined") {
				}else{
					if (typeof source[property] === 'function')
						source[property](json[property]);		
					else if(typeof source[property] === 'object'){
						if(isEmpty(source[property]))
							source[property] = json[property];
						else if(Array === source[property].constructor){
							if(source[property].length != json[property].length)
								source[property] = json[property];
							else
								json2model(source[property],json[property]);
						}else
							json2model(source[property],json[property]);
					}
					else	
						source[property] = json[property];
				}
//			}
		}
	}
}

function isEmpty(obj) {
    for(var prop in obj) {
        if(obj.hasOwnProperty(prop))
            return false;
    }

    return true && JSON.stringify(obj) === JSON.stringify({});
}

function dirtyModelElements(newModel, oldModel, array, prefix, exclude, include){
	for(var property in newModel){
			if(property.indexOf("$$hashKey")>=0){
				return;
			}		
			var exc=false;
			var inc=true;
			if(typeof newModel[property] === 'object'){
				
				if(exclude && typeof exclude === 'function')
					exc = exclude(((!prefix || prefix=='')?'':prefix+'.')+property);
				if(include && typeof include === 'function')
					inc = include(((!prefix || prefix=='')?'':prefix+'.')+property);	
				
				if(!exc && inc){
					if(newModel[property] && oldModel[property])
						dirtyModelElements(newModel[property],oldModel[property],array,((!prefix || prefix=='')?'':prefix+'.')+property,exclude);
				}
			}else if (typeof newModel[property] === 'function'){
			}else{	
				
				if(exclude && typeof exclude === 'function')
					exc = exclude(property) || exclude(((!prefix || prefix=='')?'':prefix+'.')+property);
				if(include && typeof include === 'function')
					inc = include(property) && include(((!prefix || prefix=='')?'':prefix+'.')+property);				
				
				if(!exc && inc){
					try{
						if(newModel[property] != oldModel[property]){
							var data = {};
							data[((!prefix || prefix=='')?'':prefix+'.')+property] = newModel[property];
							array.push(data);
						}
					}catch(e){
						
					}
				}
			}
	}
}

