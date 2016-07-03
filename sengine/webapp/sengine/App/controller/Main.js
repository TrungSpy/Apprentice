Ext.define('App.controller.Main', {
	extend : 'Ext.app.Controller',
	requires : [
			'Ext.data.proxy.Ajax', 
			'Ext.form.Panel', 
			'Ext.container.Viewport',
			'Ext.layout.container.Border', 
			'Ext.toolbar.Spacer',
			'Ext.toolbar.Paging', 
			'Ext.layout.container.Column',
			'Ext.layout.container.Form', 
			'Ext.layout.container.Table', 
			'Ext.form.Basic', 
			'Ext.view.DragZone',
			'Ext.grid.ViewDropZone', 
			'Ext.grid.column.Number', 
			'Ext.grid.column.CheckColumn', 
			'Ext.form.field.Checkbox',
			'Ext.form.field.Radio', 
			'Ext.form.FieldContainer',
			'Ext.form.RadioGroup',
			'Ext.menu.ColorPicker',
			'Ext.form.FieldSet'],
	init : function() {
		var ctx = {};
		ctx.vp =Ext.create('Ext.container.Viewport', {
			layout: 'border',
			items: [{
				region: 'north',
				height: 30,
				items: {
					xtype: 'box',
					style: 'background: #D2E2F4; color: #2B448C; height: 30px; font-size: 15px; font-weight: bold; padding: 4px 0px 0px 10px;',
					html: 'TRIP PLAN'
				}
			}, {
				region: 'west',
				collapsible: true,
				title: 'TRIP LIST',
				layout: 'fit',
				width: 150,
				split: true,
				listeners : {
					afterrender : function (){
						if(this.inited) return;
						this.inited = true;
						var me = this;
						Ext.Ajax.request({
							url: './data',
							params: {
								sid: 'route-list'
							},
							success: function(response){
								var obj = Ext.decode(response.responseText);
								var routes = [];
								Ext.each(obj.dataList, function(one) {
									routes.push({
										xtype : 'button',
										style: 'height: 100%; font-size: 12px; font-family: "メイリオ", sans-serif;',
										text : one.title,
										tour_id: one.m_tour_id,
										tour_description: one.description,
										style: 'width: 100%;',
										handler: function() {
											Ext.getCmp('trip_dsp').update('<pre>' + this.tour_description + '</pre>');
											Ext.Ajax.request({
												url: './data',
												params: {
													sid: 'route-detail',
													where: Ext.encode({
														'tour_id': this.tour_id
													})
												},
												success: function(response){
													var detail = Ext.decode(response.responseText);
													var p0 = detail.dataList[0];
													var p1 = detail.dataList[detail.dataList.length - 1];
													var wps = []
													if(detail.dataList.length > 2) {
														for (var i = 1; i < detail.dataList.length - 1; i++) {
															var wp = detail.dataList[i];
															wps.push({
																location: new google.maps.LatLng(wp.latitude, wp.longitude),
																stopover: true
															});
														}
													}
													
													var directionsService = new google.maps.DirectionsService;
													var directionsDisplay = new google.maps.DirectionsRenderer;
													directionsDisplay.setMap(ctx.map);
													directionsService.route({
														origin: new google.maps.LatLng(p0.latitude, p0.longitude),
														destination: new google.maps.LatLng(p1.latitude, p1.longitude),
														travelMode: google.maps.TravelMode.DRIVING,
														waypoints: wps,
														optimizeWaypoints: true
													}, function(response, status) {
														if (status === google.maps.DirectionsStatus.OK) {
															directionsDisplay.setDirections(response);
														}
													});
												}
											});
										}
									});
								});
								
								me.add(Ext.createWidget({
									xtype : 'panel',
									layout: {
										type:'vbox',
										pack:'start',
										align:'stretch'
									},
									items: routes
								}));
							}
						});
					}
				}
			}, {
				region: 'center',
				layout: 'border',
				items: [{
						region: 'center',
						height: 30,
						layout: 'fit',
						padding: '0px 0px 2px 0px',
						items: {
							xtype: 'box',
							id: 'main-map'
						},
						split: true
					}, {
						split: true,
						region: 'south',
						collapsible: true,
						title: 'PR',
						height: 100,
						layout: 'fit',
						items : [{
							xtype: 'panel',
							id: 'trip_dsp',
							style: 'height: 100%; font-size: 12px; font-family: "メイリオ", sans-serif; padding: 0px 0px 0px 10px',
							html: ''
						}]
				}]
			}]
		});
		ctx.map = new google.maps.Map(document.getElementById('main-map'), {
			center: {lat: 35.661777, lng: 139.704051},
			zoom: 8
		});
		TogetherJSConfig_siteName = 'TRIP PLAN';
		TogetherJS.config('cloneClicks', true);
		TogetherJS(this); 
	}
	
});
