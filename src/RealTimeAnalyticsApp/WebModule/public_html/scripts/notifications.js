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
var notifications = {
	initialize: function() {
		$('#notifications-list').empty();
		$.ajax({
			url: '/rta/resources/notification/all',
			dataType: "json"
		}).then(function(data) {				
			if (data.length > 0) {	
				data.forEach( function (arrayItem) {
					notifications.addNotification(arrayItem);
				});
			}
		});			
	},
    addNotification: function(data) {
		$('#kpi1-icon').animate({backgroundColor: '#B6DADA'}, 800, 'linear');
		if (data) {
			var currentDate = new Date();
			var time = currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds();
			var classType = '';
			switch (data.type)
			{
			   case 0:
					classType = "fa-info text-green";
					break;
			   case 1:
					classType = "fa-warning text-yellow";
					break;
			   case 2:
					classType = "fa-flag text-red";
					break;
			   case 3:
					classType = "fa-life-bouy text-red";
					break;
			   case 4:
					classType = "fa-bomb text-red";
					break;
			   default: classType = "";
			}			
			$('#notifications-list').prepend("<li id='" + data.id + "'><span>&nbsp;<i class='fa " + classType + "'></i> " + data.message + "<div class='pull-right'><small><i class='fa fa-clock-o'></i> " + time + "</small> <a title='Remove the Notification' href='#' onclick='notifications.delObj(this);'><i class='fa fa-close text-red'></i></a></div></span></li>");
			$('#notifications-count').html($('#notifications-list').children().length);
			$('#notifications-message').html('You have ' + $('#notifications-list').children().length + ' alert notification(s)');
		}
    },
	delObj: function(elem) {
		$.ajax({
			url: '/rta/resources/notification?id=' + $(elem).parent().parent().parent().attr('id'),
			method: 'DELETE'
		}).then(function(data) {					
			if (data.status != "ok")
				rta.modal('Error', data.status);
		});	
	},
	removeNotification: function(data) {		
		if (data) {
			$('#' + data.id).remove();
			var count = $('#notifications-list').children().length;			
			$('#notifications-count').html(count);	
			if (count > 0)
				$('#notifications-message').html('You have ' + $('#notifications-list').children().length + ' alert notification(s)');
			else
				$('#notifications-message').html('There are no alert notification(s)');				
		}
	}
	
};


