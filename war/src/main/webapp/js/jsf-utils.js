var JSFUtils = function(Richfaces) {
	
	function clearDateValue() {
		
		for (var i = 0; i < arguments.length; i++) {
				document.getElementById(arguments[i]).component.resetSelectedDate();
		  }
	}
	
	
	function onStartValidityChanged(startDateComponentId,endDateComponentId) {
		
		var startDateComponent = document.getElementById(startDateComponentId).component;
		var endDateComponent = document.getElementById(endDateComponentId).component;
		
		
		endDateComponent.selectDate(startDateComponent.getSelectedDate(),false,null);
	
	}
	
	function onEndValidityClick(startDateComponentId,endDateComponentId) {
		
		var startDateComponent = document.getElementById(startDateComponentId).component;
		var endDateComponent = document.getElementById(endDateComponentId).component;
		
		// SOLO SE VUOTO, ALTRIMENTI LA CAMBIA SEMPRE E NON SI PUO' METTERE L'ORA:
		if(endDateComponent.getSelectedDate()==null)
			endDateComponent.selectDate(startDateComponent.getSelectedDate(),false,null);
	
	}
	
	
	function onAttachmentTypeRejected()
	{
		alert('Solo file pdf, png, jpg, txt sono accettati o file duplicato');
	}
	
	function onAttachmentTypeRejectedCsv()
	{
		alert('Solo file csv sono accettati o file duplicato');
	}

	// revealing public stuff
	return {
		clearDateValue:clearDateValue,
		onEndValidityClick:onEndValidityClick,
		onStartValidityChanged:onStartValidityChanged,
		onAttachmentTypeRejected:onAttachmentTypeRejected,
		onAttachmentTypeRejectedCsv:onAttachmentTypeRejectedCsv
	}
  
}(Richfaces);