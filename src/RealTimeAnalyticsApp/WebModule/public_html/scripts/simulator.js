/*
MIT License

Copyright (c) 2016 Alexandre Vieira https://github.com/vieiraae/rta

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
var simulator = {
	started: false,
	sendNotification: function() {		
		$.ajax({
			url: '/rta/resources/simulator/notification?type=' + $('#notification-type').val() + '&message=' + $('#notification-message').val(),
			type: 'PUT'
		}).then(function(data) {				
			if (data.status != "ok")
				rta.modal('Error', data.status);
		});			
	},
	sendJSStatement: function() {		
		$.ajax({
			url: '/rta/resources/simulator/jsstatement?statement=' + escape($('#js-statement').val()),
			type: 'PUT'
		}).then(function(data) {				
			if (data.status != "ok")
				rta.modal('Error', data.status);
		});			
	},
	updateStatus: function(data) {
		if (data)
			$('#simulator-status').html(data);
	},
	alreadyStarted: function(data) {
		if (data) {
			simulator.started = false;
			$('#simulator-status').html(data);
			$('#simulator-toggle').bootstrapToggle('on');		
		}
	}	
};

$(function() {
	$('#simulator-delay').slider({min  : 500, max  : 5000, value: 1000, step: 100, orientation: 'horizontal', tooltip: 'hide', tooltip_position: 'top', formatter: function(value) {
		return 'Delay: ' + value;
	}});
	$('#simulator-toggle').change(function() {
		if ($(this).prop('checked')) {
			if (simulator.started) {
				simulator.started = false;
				rta.websocket.send('rta.beans.Simulator.stop();');	
				$('#simulator-status').html('');
				$('#simulator-delay').slider("enable");
			}
		} else {
			simulator.started = true;
			$('#simulator-delay').slider("disable");
			var simulation = new Object();
			$('#simulator-status').html('Starting...');
			simulation.activeDashboard = dashboardManager.activeDashboard;
			simulation.delay = $('#simulator-delay').val();				
			rta.websocket.send('rta.beans.Simulator.start(' + JSON.stringify(simulation) + ');');	
		}
	});
});
