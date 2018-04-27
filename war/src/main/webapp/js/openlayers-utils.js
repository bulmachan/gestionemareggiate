var GMOpenLayerUtils = function(Richfaces) {

	var map;
	var osm,wms,wms1,wms2;
	var vectors;
	var select;
	var shapeSelectionOutput = '';
	var geometryTextHiddenField;
	var modalPanelMap;
	var geographic = new OpenLayers.Projection("EPSG:32632");
	var geographicProj = new Proj4js.Proj("EPSG:32632");
	var mercator = new OpenLayers.Projection("EPSG:3857");
	var mercatorProj = new Proj4js.Proj("EPSG:3857");
	var estensione_costa;
	
	var selectControl;
	
	function initDanniMap(mapContainerName, data) {
		
		console.log(data);
		
		var scales = [25, 50, 100, 500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 7500, 10000, 15000, 25000, 35000, 50000, 60000, 75000, 100000, 150000, 175000, 200000, 250000, 300000, 400000, 500000, 600000, 750000, 1000000, 1200000, 1400000, 1600000, 2000000];
		var estensione = new OpenLayers.Bounds(1350000,5453000,1430000,5599000);

		var options = {
				projection: mercator,
				displayProjection: geographic,
				units: "m",
				maxResolution: 156543.0339,
				maxExtent: estensione,
				numZoomLevels: 36,
				scales: scales/*,
		controls: []*/
		};
		
		if(modalPanelMap)
			modalPanelMap.destroy();

		modalPanelMap = new OpenLayers.Map(mapContainerName, options);

		var wms = new OpenLayers.Layer.WMS( "OpenLayers WMS", 
				"http://vmap0.tiles.osgeo.org/wms/vmap0", {layers: 'basic'} );

		var osm = new OpenLayers.Layer.OSM( "Open Street Map" );
		osm.id = "OSM";	

		vectors = new OpenLayers.Layer.Vector("Vector Layer");

		modalPanelMap.addLayers([osm, vectors]);
		
		
		var geoJSON = JSON.parse(data);
		var geojson_format = new OpenLayers.Format.GeoJSON();
       
		vectors.addFeatures(geojson_format.read(geoJSON));
		
		
		modalPanelMap.setCenter(new OpenLayers.LonLat(1390195,5527089), 10);
		
		selectControl = new OpenLayers.Control.SelectFeature(vectors, {

		    onSelect: onFeatureSelect,

		    onUnselect: onFeatureUnselect

		});

		modalPanelMap.addControl(selectControl);
		modalPanelMap.addControl(new OpenLayers.Control.MousePosition());
		
		selectControl.activate();
	}
	
	function onFeatureSelect(event) {
        console.log(event);
       
        var content = '<div class="markerContent">Tipo di Danno:' + event.data.tipoDanno+'</div>';
       
        popup = new OpenLayers.Popup.FramedCloud("pop", 
                                 event.geometry.getBounds().getCenterLonLat(),
                                 null,
                                 content,
                                 null, true, onPopupClose);
        event.popup = popup;
        modalPanelMap.addPopup(popup);
    }
	
	function onPopupClose(evt) {
		selectControl.unselectAll();
    }
	
	function onFeatureUnselect(event) {
		console.log(event);
        var popup = event.popup;
        if(popup) {
        	modalPanelMap.removePopup(popup);
            popup.destroy();
            delete popup;
        }
    }

	function init(mapContainerName, geometryTextField, geometryPointTextField, geometryLineStringTextField, geometryPolygonTextField) {

		
		
		// CREIAMO UNA CLASSE "WMS_SGSS" CHE ESTENDE WMS e modifichiamo il metodo getFullRequestString:
		OpenLayers.Layer.WMS_SGSS = OpenLayers.Class(OpenLayers.Layer.WMS, {
			getFullRequestString:function(newParams, altUrl) {
				var projectionCode = this.map.getProjection();
				var value = (projectionCode == "none") ? null : projectionCode
				if (parseFloat(this.params.VERSION) >= 1.3) {
					this.params.CRS = value;
				} else {
					this.params.SRS = value;
				}
				// MODIFICA MARUCCI
				if(this.params.SRS == "EPSG:900913")
					this.params.SRS = "EPSG:3857";

				return OpenLayers.Layer.Grid.prototype.getFullRequestString.apply(this, arguments);
			}				

		});		


		OpenLayers.Control.MousePosition_SGSS = OpenLayers.Class(OpenLayers.Control.MousePosition, {

			redraw: function(evt) {

				var lonLat;

				if (evt == null) {
					this.reset();
					return;
				} else {
					if (this.lastXy == null ||
						Math.abs(evt.xy.x - this.lastXy.x) > this.granularity ||
						Math.abs(evt.xy.y - this.lastXy.y) > this.granularity)
					{
						this.lastXy = evt.xy;
						return;
					}

					lonLat = this.map.getLonLatFromPixel(evt.xy);
					if (!lonLat) { 
						// map has not yet been properly initialized
						return;
					}  
					//console.log(lonLat);
					//console.log(this.map.getProjectionObject());
					//console.log(this.displayProjection);
					
					if (this.displayProjection) {
						lonLat.transform(new OpenLayers.Projection("EPSG:900913"), 
										 this.displayProjection );
					}      
					this.lastXy = evt.xy;
					
				}
				
				var newHtml = this.formatOutput(lonLat);

				if (newHtml != this.element.innerHTML) {
					this.element.innerHTML = newHtml;
				}
			}

		});	

    



		
		var scales = [25, 50, 100, 500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 7500, 10000, 15000, 25000, 35000, 50000, 60000, 75000, 100000, 150000, 175000, 200000, 250000, 300000, 400000, 500000, 600000, 750000, 1000000, 1200000, 1400000, 1600000, 2000000];
		var estensione = new OpenLayers.Bounds(1350000,5453000,1430000,5599000);
		estensione_costa = new OpenLayers.Bounds(1293730,5450086,1477978,5603510);

		var options = {
				projection: mercator,
				displayProjection: geographic,
				units: "m",
				maxResolution: 156543.0339,
				maxExtent: estensione,
				numZoomLevels: 36,
				scales: scales/*,
		controls: []*/
		};
		map = new OpenLayers.Map(mapContainerName,options);
		geometryTextHiddenField = geometryTextField;
		geometryPointTextHiddenField = geometryPointTextField;
		geometryLineStringTextHiddenField = geometryLineStringTextField;
		geometryPolygonTextHiddenField = geometryPolygonTextField;
		wms = new OpenLayers.Layer.WMS( "OpenLayers WMS",
				"http://vmap0.tiles.osgeo.org/wms/vmap0?", {layers: 'basic'});

		osm = new OpenLayers.Layer.OSM( "Open Street Map" );
		osm.id = "OSM";		

		wms1 = new OpenLayers.Layer.WMS_SGSS( "OrtoAgea2008",
				"http://servizigis.regione.emilia-romagna.it/wms/agea2008_rgb", 
				{layers: 'Agea2008_RGB'}, 
				{projection: "EPSG:3857", singleTile: true, ratio: 1, isBaseLayer: true, visibility: false, attribution: "<img src='img/mIconRERLayer.png' style='width: 20px; vertical-align: middle;' /> Fonte: Regione Emilia-Romagna - <a href='http://geoportale.regione.emilia-romagna.it/it/licenze' target='_blank'>licenza d'uso</a>"}
		);
		wms1.id = "Orto";

		wms2 = new OpenLayers.Layer.WMS_SGSS( "Localita",
				"http://servizigis.regione.emilia-romagna.it/wms/costa", 
				{layers: 'Localita_costiere,Macro_localita_costiere','format': 'image/png','transparent': 'TRUE', 'bgcolor': '0xFFFFFF'},
				{projection: "EPSG:3857", singleTile: true, ratio: 1, isBaseLayer: false, visibility: true}
		);
		wms2.id = "Localita";
		
		
		vectors = new OpenLayers.Layer.Vector("Vector Layer");
		/*vectors.events.on({'beforefeatureadded': function(){
			  //vectors.destroyFeatures();
		}});*/
		vectors.events.on({'featureadded': serialize});

		map.addLayers([osm,wms1,wms2,vectors]);

		map.addControl(new OpenLayers.Control.MousePosition_SGSS({numDigits:0,prefix:'Coordinate WGS84 UTM32: '}));
		map.addControl(new OpenLayers.Control.EditingToolbar(vectors));

		var options = {
				/*hover: true,
			onSelect: serialize*/
		};

		select = new OpenLayers.Control.SelectFeature(vectors, options);
		map.addControl(select);
		select.activate();
		
		map.zoomToExtent(estensione_costa);
		//map.setCenter(new OpenLayers.LonLat(1390195,5527089), 8);
		
		//map.panTo(new OpenLayers.LonLat(1390195,5527089));
		//map.zoomToScale(1000000,false);

	}

	function centerMap() {
		//map.setCenter(new OpenLayers.LonLat(1390195,5527089), 9);
		map.zoomToExtent(estensione_costa);
	}
	
	function centerToPoint(selectMenu)
	{
		var value = selectMenu.options[selectMenu.selectedIndex].value;
		//console.log(value);
		if(value){
			var valueArray = value.split(";");
			if(valueArray.length == 2){
				var coordinate = valueArray[1].split(",");
				map.setCenter(new OpenLayers.LonLat(coordinate[0],coordinate[1]), 18);
				map.zoomToScale(8000,false);

			}
		}

	}
	function toggleBase(t){
		if(!wms1.visibility){
			map.setBaseLayer(wms1);
			wms1.setVisibility(true);
			t.value=" Mappa ";
		} else if(!osm.visibility){
			map.setBaseLayer(osm);
			osm.setVisibility(true);
			t.value=" Foto  ";
		}
		
	}

	function clearVectors() {
		vectors.destroyFeatures();
		geometryTextHiddenField.value='';
		pointList = [];
		polygonList = [];
		linestringList = [];

	}

	function saveVectors() {
		if(pointList.length>0){
			var multiPointGeometry = new OpenLayers.Geometry.MultiPoint(pointList);
			var multiPointFeature = new OpenLayers.Feature.Vector(multiPointGeometry);
			var multiPointGeometryWKT = new OpenLayers.Format.WKT().write(multiPointFeature);
			//geometryPointTextHiddenField.value=multiPointGeometryWKT;
			//jQuery('#geometryPointHiddenText').val(multiPointGeometryWKT);
			//console.log(multiPointGeometryWKT);
		}	
		if(linestringList.length>0){
			var multiLinestringGeometry = new OpenLayers.Geometry.MultiLineString(linestringList);
			var multiLinestringFeature = new OpenLayers.Feature.Vector(multiLinestringGeometry);
			var multiLinestringGeometryWKT = new OpenLayers.Format.WKT().write(multiLinestringFeature);
			//geometryLineStringTextHiddenField.value=multiLinestringGeometryWKT;
			//jQuery('#geometryLineStringHiddenText').val(multiLinestringGeometryWKT);
			//console.log(multiLinestringGeometryWKT);
		}
		if(polygonList.length>0){
			var multiPolygonGeometry = new OpenLayers.Geometry.MultiPolygon(polygonList);
			var multiPolygonFeature = new OpenLayers.Feature.Vector(multiPolygonGeometry);
			var multiPolygonGeometryWKT = new OpenLayers.Format.WKT().write(multiPolygonFeature);
			//geometryPolygonTextHiddenField.value=multiPolygonGeometryWKT;
			//jQuery('#geometryPolygonHiddenText').val(multiPolygonGeometryWKT);
			//console.log(multiPolygonGeometryWKT);
		}	
	}

	function escapeRegExp(str) {
		return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
	}	
	function replaceAll(find, replace, str) {
		return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
	}	

	var pointList = [];
	var polygonList = [];
	var linestringList = [];
	function serialize(feature) {
		/*featAdded=feature.feature;
		tipoFeatAdded=feature.feature.geometry.CLASS_NAME;
		console.log(tipoFeatAdded);
		var wkt = new OpenLayers.Format.WKT();
		var geom = wkt.write(featAdded);
		console.log(geom);*/
		
		var geom = feature.feature.geometry;
		var listaCompleta="";
		//console.log(geom);
		// NON LO FACCIAMO PERCHE' DOVREMMO CICLARE TUTTI I PUNTI...
		//OpenLayers.Projection.transform(geom,mercator,geographic);
		//Proj4js.transform(mercatorProj,geographicProj,geom);
		//console.log(geom);

		if(geom.CLASS_NAME=='OpenLayers.Geometry.Point'){
			pointList.push(geom);
		} else if(geom.CLASS_NAME=='OpenLayers.Geometry.LineString'){
			linestringList.push(geom);
		} else if(geom.CLASS_NAME=='OpenLayers.Geometry.Polygon'){
			var reader = new jsts.io.WKTReader();
			var geomToVerify = new OpenLayers.Format.WKT().write(feature.feature);
			//console.log(geomToVerify);
			var geomToVerify = reader.read(geomToVerify);        
			//console.log(geomToVerify.isValid());			
			if(geomToVerify.isValid()){
				polygonList.push(geom);
			} else {
				alert("Attenzione, la geometria non è valida e quindi non può essere inserita, è necessario disegnarla nuovamente");
				vectors.removeFeatures(feature);
				vectors.destroyFeatures(feature.feature);
				
			}
		}

		if(pointList.length>1){
			var multiPointGeometry = new OpenLayers.Geometry.MultiPoint(pointList);
			var multiPointFeature = new OpenLayers.Feature.Vector(multiPointGeometry);
			var multiPointGeometryWKT = new OpenLayers.Format.WKT().write(multiPointFeature);

			multiPointGeometryWKT=multiPointGeometryWKT.replace("((","(").replace("))",")");
			multiPointGeometryWKT=replaceAll("),(",",",multiPointGeometryWKT);

			listaCompleta+=multiPointGeometryWKT+";";


		} else if (pointList.length==1){

			var pointGeometry = pointList[0];
			var pointFeature = new OpenLayers.Feature.Vector(pointGeometry);
			var pointGeometryWKT = new OpenLayers.Format.WKT().write(pointFeature);

			listaCompleta+=pointGeometryWKT+";";

		}

		if(polygonList.length>1){		
			var multiPolygonGeometry = new OpenLayers.Geometry.MultiPolygon(polygonList);
			var multiPolygonFeature = new OpenLayers.Feature.Vector(multiPolygonGeometry);
			var multiPolygonGeometryWKT = new OpenLayers.Format.WKT().write(multiPolygonFeature);
			
			listaCompleta+=multiPolygonGeometryWKT+";";

		} else if (polygonList.length==1){

			var polygonGeometry = polygonList[0];
			var polygonFeature = new OpenLayers.Feature.Vector(polygonGeometry);
			var polygonGeometryWKT = new OpenLayers.Format.WKT().write(polygonFeature);
			
			
			listaCompleta+=polygonGeometryWKT+";";

		}

		if(linestringList.length>1){

			var multiLinestringGeometry = new OpenLayers.Geometry.MultiLineString(linestringList);
			var multiLinestringFeature = new OpenLayers.Feature.Vector(multiLinestringGeometry);
			var multiLinestringGeometryWKT = new OpenLayers.Format.WKT().write(multiLinestringFeature);
			
			listaCompleta+=multiLinestringGeometryWKT+";";

		} else if (linestringList.length==1){

			var linestringGeometry = linestringList[0];
			var linestringFeature = new OpenLayers.Feature.Vector(linestringGeometry);
			var linestringGeometryWKT = new OpenLayers.Format.WKT().write(linestringFeature);
			
			listaCompleta+=linestringGeometryWKT+";";
		}		



		geometryTextHiddenField.value=listaCompleta;
		jQuery('#geometryHiddenText').val(listaCompleta);

		//console.log(listaCompleta);
	}

	function getShapeSelectionOutput() {
		return shapeSelectionOutput;
	}

	function showModalPanelMap(modalPanelId, mapContainerId, wktFeature) {

		showRichfacesModalPanel(modalPanelId);

		var scales = [25, 50, 100, 500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 7500, 10000, 15000, 25000, 35000, 50000, 60000, 75000, 100000, 150000, 175000, 200000, 250000, 300000, 400000, 500000, 600000, 750000, 1000000, 1200000, 1400000, 1600000, 2000000];
		var estensione = new OpenLayers.Bounds(1350000,5453000,1430000,5599000);

		var options = {
				projection: mercator,
				displayProjection: geographic,
				units: "m",
				maxResolution: 156543.0339,
				maxExtent: estensione,
				numZoomLevels: 36,
				scales: scales/*,
		controls: []*/
		};

		modalPanelMap = new OpenLayers.Map('modalPanelMap', options);

		var wms = new OpenLayers.Layer.WMS( "OpenLayers WMS", 
				"http://vmap0.tiles.osgeo.org/wms/vmap0", {layers: 'basic'} );

		var osm = new OpenLayers.Layer.OSM( "Open Street Map" );
		osm.id = "OSM";	

		vectors = new OpenLayers.Layer.Vector("Vector Layer");

		modalPanelMap.addLayers([osm, vectors]);

		wkt = new OpenLayers.Format.WKT();

		drawWKT(modalPanelMap, vectors, wktFeature);
	}

	function drawWKT(map, vectors, wktFeatures) {

		var wktFeatures = wktFeatures.split(";");
		var bounds;
		for(var y=0; y<wktFeatures.length; y++){

			var features = wkt.read(wktFeatures[y]);
			
			if(features) {
				if(features.constructor != Array) {
					features = [features];
				}
				for(var i=0; i < features.length; i++) {
					if (!bounds) {
						bounds = features[i].geometry.getBounds();
					} else {
						bounds.extend(features[i].geometry.getBounds());
					}

				}
				vectors.addFeatures(features);
				

			} else {
				console.log('Bad WKT');
			}
		}
		if(bounds)
			map.zoomToExtent(bounds);
	}

	function debugGeometry() {
		console.log();
	}
	
	function hideModalPanelMap(modalPanelId) {
		hideRichfacesModalPanel(modalPanelId);
		modalPanelMap.destroy();
	}

	function showRichfacesModalPanel(modalPanelId) {
		Richfaces.showModalPanel(modalPanelId);
	}

	function hideRichfacesModalPanel(modalPanelId) {
		Richfaces.hideModalPanel(modalPanelId);
	}
	
	

	// revealing public stuff
	return {
		init:init,
		clearVectors:clearVectors,
		saveVectors:saveVectors,
		centerMap:centerMap,
		centerToPoint:centerToPoint,
		toggleBase: toggleBase,
		getShapeSelectionOutput:getShapeSelectionOutput,
		showModalPanelMap:showModalPanelMap,
		hideModalPanelMap:hideModalPanelMap,
		initDanniMap:initDanniMap
	}

}(Richfaces);