var GMGoogleChartsUtils = function(Richfaces) {

	var chart;
	var data;
	var selectedChartValueRow;
	
	var aggregation = 'MONTH';
	
	var dateReviver = function (key, value) {
		var a;
		if (typeof value === 'string') {
			a = Date.parse(value);
			if (a) {
				return new Date(a);
			}
		}
		return value;
	}


	var chartMareggiate;
	var dataMareggiate;
	var chartAvvisiMeteo;
	var dataAvvisiMeteo;
	var chartDatiMeteo;
	var dataDatiMeteo;
	var chartLocalitaDanno;
	var dataLocalitaDanno;
	var chartDanniTotali;
	var dataDanniTotali;
	var map;

	/* USA IL MODAL PANEL DI RICHFACES */
	function showRFModalPanelChart(modalPanelId, chartContainerId, chartData) {
		Richfaces.showModalPanel(modalPanelId);
		title = chartData.split("|")[0];
		values = chartData.split("|")[1];
		drawChart(chartContainerId, values, title );
	}
	/* USA IL MODAL PANEL DI RICHFACES */
	function hideRichfacesModalPanel(modalPanelId) {
		Richfaces.hideModalPanel(modalPanelId);
	}

	
	function setAggregation(value)
	{
		console.log(value);
		aggregation = value;
	}

	function drawMareggiateChart(chartContainerId,data)
	{

		console.log(data);

		dataMareggiate = google.visualization.arrayToDataTable(JSON.parse(data, dateReviver ));
		
		if(dataMareggiate.getNumberOfRows() == 0)
		{
			console.log('nessuna mareggiata nel periodo selezionato');
			//TODO mostra nessun dato
			return;
		}

		var options = {
				title: "Mareggiate",
				legend:'bottom',
				isStacked: true,
				hAxis: {
					format: aggregation == 'MONTH' ? 'MMMM yyyy' : 'yyyy',
					gridlines: {
                		color: 'transparent'
            		},
				},
				
				vAxis: {
					format:'#'
				}
		};
		
		

		var formatter_long = new google.visualization.DateFormat({
			pattern: aggregation == 'MONTH' ? 'MMMM yyyy' : 'yyyy'
		});
		formatter_long.format(dataMareggiate, 0);
		
		var dataView = new google.visualization.DataView(dataMareggiate);
        dataView.setColumns([{calc: function(data, row) { return data.getFormattedValue(row, 0); }, type:'string'}, 1,2]);


		chartMareggiate = new google.visualization.ColumnChart(document.getElementById(chartContainerId));
		
		
		chartMareggiate.draw(dataView, options);
	}

	function drawAvvisiMeteoChart(chartContainerId,data)
	{

		console.log(data);

		dataAvvisiMeteo = google.visualization.arrayToDataTable(JSON.parse(data, dateReviver ));
		
		if(dataAvvisiMeteo.getNumberOfRows() == 0)
		{
			console.log('nessun avviso meteo nel periodo selezionato');
			//TODO mostra nessun dato
			return;
		}

		var options = {
				title: "Avvisi meteo",
				legend:'bottom',
				isStacked: true,
				hAxis: {
					format: aggregation == 'MONTH' ? 'MMMM yyyy' : 'yyyy',
					gridlines: {
                		color: 'transparent'
            		},
				},
				
				vAxis: {
					format:'#'
				}
		};
		
		

		var formatter_long = new google.visualization.DateFormat({
			pattern: aggregation == 'MONTH' ? 'MMMM yyyy' : 'yyyy'
		});
		formatter_long.format(dataAvvisiMeteo, 0);
		
		var dataView = new google.visualization.DataView(dataAvvisiMeteo);
        dataView.setColumns([{calc: function(data, row) { return data.getFormattedValue(row, 0); }, type:'string'}, 1,2,3]);


        chartAvvisiMeteo = new google.visualization.ColumnChart(document.getElementById(chartContainerId));
		
		
        chartAvvisiMeteo.draw(dataView, options);
	}

	function drawDatiMeteoChart(chartContainerId,data)
	{

		console.log(data);

		dataDatiMeteo = google.visualization.arrayToDataTable(JSON.parse(data));
		
		if(dataDatiMeteo.getNumberOfRows() == 0)
		{
			console.log('nessun dato meteo nel periodo selezionato');
			//TODO mostra nessun dato
			return;
		}

		var options = {
				title: "Dati meteo",
				legend:'bottom',
				hAxis: {
					gridlines: {
                		color: 'transparent'
            		}
				},
				
				vAxis: {
					format:'#'
				}
		};

		var dataView = new google.visualization.DataView(dataDatiMeteo);

        chartDatiMeteo = new google.visualization.LineChart(document.getElementById(chartContainerId));
		
		
        chartDatiMeteo.draw(dataView, options);
	}
	
	function drawLocalitaDannoChart(chartContainerId,data)
	{

		console.log(data);

		dataLocalitaDanno = google.visualization.arrayToDataTable(JSON.parse(data, dateReviver ));
		
		if(dataLocalitaDanno.getNumberOfRows() == 0)
		{
			console.log('nessun danno per località nel periodo selezionato');
			//TODO mostra nessun dato
			return;
		}

		var options = {
				title: "Danni per località",
				legend:'bottom',
				isStacked: true,
				vAxis: {
					format:'#'
				}
		};

		var dataView = new google.visualization.DataView(dataLocalitaDanno);

		chartLocalitaDanno = new google.visualization.BarChart(document.getElementById(chartContainerId));
		
		function selectHandler() {
		    var selectedItem = chartLocalitaDanno.getSelection()[0];
		    if (selectedItem) {
		      
		    	var jsonDanniTotali = '[["Danno","Totale"],';
		    	for (i = 1; i < dataLocalitaDanno.getNumberOfColumns(); i++) { 
		    		jsonDanniTotali += ('["' + dataLocalitaDanno.getColumnLabel(i) + '",' + dataLocalitaDanno.getValue(selectedItem.row, i) + ']');
		    		if((i+1) == dataLocalitaDanno.getNumberOfColumns()) 
		    			jsonDanniTotali += ']';
		    		else
		    			jsonDanniTotali += ',';
		    	}
		    	console.log("json: " + jsonDanniTotali);
		    	
		    	drawDanniTotaliChart('danniTotaliChartPnl', jsonDanniTotali,'Danni totali '+dataLocalitaDanno.getValue(selectedItem.row, 0));
		    }
		}
		
		google.visualization.events.addListener(chartLocalitaDanno, 'select', selectHandler);
		
		chartLocalitaDanno.draw(dataView, options);
	}
	
	function drawDanniTotaliChart(chartContainerId,data,title)
	{

		console.log(data);

		dataDanniTotali = google.visualization.arrayToDataTable(JSON.parse(data, dateReviver ));
		
		if(dataDanniTotali.getNumberOfRows() == 0)
		{
			console.log('nessun danno nel periodo selezionato');
			//TODO mostra nessun dato
			return;
		}

		var options = {
				title: title,
				legend:'bottom',
				hAxis: {
					gridlines: {
                		color: 'transparent'
            		}
				},
				
				vAxis: {
					format:'#'
				}
		};

		var dataView = new google.visualization.DataView(dataDanniTotali);

		chartDanniTotali = new google.visualization.PieChart(document.getElementById(chartContainerId));
		
		chartDanniTotali.draw(dataView, options);
	}
	
	function hideAnalysisChart(chartContainerId)
	{
		document.getElementById(chartContainerId).style.visibility="hidden";
	}
	
	function showAnalysisChart (chartContainerId)
	{
		document.getElementById(chartContainerId).style.visibility="visible";
	}

	function drawChart(chartContainerId, chartData, title) {
		data = google.visualization.arrayToDataTable(JSON.parse(chartData));

		var options = {
				title: title
		};

		chart = new google.visualization.LineChart(document.getElementById(chartContainerId));
		chart.draw(data, options);
		google.visualization.events.addListener(chart, 'select', chartSelectHandler);

	}

	function chartSelectHandler() {
		selectedChartValueRow = chart.getSelection()[0].row;
		jQuery('#chartEditValueBox input[type=text]').val(data.getValue(chart.getSelection()[0].row, chart.getSelection()[0].column));
		jQuery('#chartEditValueBox input[type=hidden]').val(selectedChartValueRow);
		jQuery('#chartEditValueBox').css('display','inline-block');
	}

	function clearChartEditValueBox() {
		jQuery('#chartEditValueBox input[type=text]').val('');
		jQuery('#chartEditValueBox input[type=hidden]').val('');
		jQuery('#chartEditValueBox').css('display','none');
	}

	/* USA MODAL PANEL CUSTOM */
	function showChartModalPanel(modalPanelId, chartContainerId, chartData) {
		title = chartData.split("|")[0];
		values = chartData.split("|")[1];

		// set chartEditValueBox default values
		data = google.visualization.arrayToDataTable(JSON.parse(values));
		jQuery('#chartEditValueBox input[type=text]').val(data.getValue(0,1));
		jQuery('#chartEditValueBox input[type=hidden]').val('0');

		jQuery('#modalOverlay').addClass('modalOverlay');
		jQuery('#'+modalPanelId).removeClass('modalPanelHidden');
		jQuery('#'+modalPanelId).addClass('modalVisible');
		drawChart(chartContainerId, values, title );
	}

	/* USA MODAL PANEL CUSTOM */
	function hideChartModalPanel(modalPanelId) {
		jQuery('#chartEditValueBox input[type=text]').val(data.getValue(0,1));
		jQuery('#chartEditValueBox input[type=hidden]').val('0');
		jQuery('#chartEditValueBox').css('display','none');
		jQuery('#modalOverlay').removeClass('modalOverlay');
		jQuery('#'+modalPanelId).removeClass('modalVisible');
		jQuery('#'+modalPanelId).addClass('modalPanelHidden');
	}

	// revealing public stuff
	return {
		showRFModalPanelChart:showRFModalPanelChart,
		hideRichfacesModalPanel:hideRichfacesModalPanel,
		showChartModalPanel:showChartModalPanel,
		hideChartModalPanel:hideChartModalPanel,
		clearChartEditValueBox:clearChartEditValueBox,
		drawMareggiateChart:drawMareggiateChart,
		hideAnalysisChart:hideAnalysisChart,
		setAggregation:setAggregation,
		drawAvvisiMeteoChart:drawAvvisiMeteoChart,
		drawDatiMeteoChart:drawDatiMeteoChart,
		drawLocalitaDannoChart:drawLocalitaDannoChart,
		drawDanniTotaliChart:drawDanniTotaliChart,
		showAnalysisChart:showAnalysisChart
	}

}(Richfaces);