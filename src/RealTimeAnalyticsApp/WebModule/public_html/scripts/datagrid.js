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
var datagrid = {
	displayInfo: function() {	
		$.ajax({
			url: '/rta/resources/datagrid/info',
			type: 'GET',
			dataType: "json",
			cache: false
		}).then(function(data) {				
			if (data && data.length != 0) {
				$('#datagrid-members').empty();
				data.forEach( function (arrayItem) {
					if (arrayItem.local)
						$('#datagrid-members').append("<li><a title='Application Server Data Grid Member'><i class='menu-icon fa fa-connectdevelop bg-green'></i><div class='menu-info'><h4 class='control-sidebar-subheading'>" + arrayItem.id + " <span class='label label-primary'>Your Server</span></h4><p>Since <i class='fa fa-clock-o'></i> " + arrayItem.timestamp + "</p></div></a></li>");
					else
						$('#datagrid-members').append("<li><a><i class='menu-icon fa fa-connectdevelop bg-green'></i><div class='menu-info'><h4 class='control-sidebar-subheading'>" + arrayItem.id + "</h4><p>Since <i class='fa fa-clock-o'></i> " + arrayItem.timestamp + "</p></div></a></li>");					
				});
			}
		});			
	}
};

