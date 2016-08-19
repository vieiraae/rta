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
var rta = {
	count: 0,
	websocket: null,
	connected: false,
	reconnecting: false,
	username: null,	
    url: ((window.location.protocol == "https:") ? "wss:" : "ws:") + "//" + window.location.host + "/rta/stream",
    initialize: function() {
        if ("WebSocket" in window) {
            rta.listen();
			rta.open();
        } else {
 			rta.connected = false;
            $('#direct-chat-messages').hide();
            $('#websockets-error').show();
        }
		$('#chat-message-text').keydown(function(e) {
					if (e.keyCode == 13) {
						chat.postChatMessage();
					}
		});
		$('#chat-post-button').click(function(e) {
					chat.postChatMessage();
		});		
		$('#users-dropdown').click(function(e) {
					chat.displayUsers();
		});
    },
    listen: function() {
        $('#websockets-frame').src = rta.url + '?' + rta.count;
        rta.count ++;
    },
    open: function() {
        rta.websocket = new WebSocket(rta.url);
        rta.websocket.onopen = function() {
			rta.connected = true;
			$('#direct-chat-messages').show();
			$('#websocket-status-off').hide();
			$('#websocket-status-on').show();			
        };
        rta.websocket.onmessage = function (evt) {
            eval(evt.data);
        };
        rta.websocket.onclose = function() {
			if (rta.connected) {
				rta.connected = false;
				chat.appendChatMessage("system", "Connection closed!");
				$('#websocket-status-off').show();
				$('#websocket-status-on').hide();
				reconnecting = true;
				setInterval(rta.reconnect, 5000);
			}
        };   
		rta.websocket.onerror = function() {
			if (!reconnecting) {
				if (rta.connected) {
					rta.connected = false;
					chat.appendChatMessage("system", "Connection error!");
					$('#websocket-status-off').show();
					$('#websocket-status-on').hide();
					reconnecting = true;
					setInterval(rta.reconnect, 5000);
				} else {
					alert("Connection Lost. Reload the page.");
				}					
			}
		};
    },
	reconnect: function() {
		$('#websocket-status-off').html('<i class="fa fa-spinner"></i> Reconnecting...');
		$.ajax({
			url: '/rta/resources/datagrid/ping',
			type: 'GET',
			dataType: "json",
			cache: false
		}).then(function(data) {				
			if (data.status == "ok")
				window.location.reload();		
		});				
	},
	setUser: function(data) {
        if (data) {
			rta.username = data.username;
			$('#user-placeholder1').html(rta.username);
			$('#user-placeholder2').html(rta.username);
			dashboardManager.initialize();
			notifications.initialize();
			$( "#main-div" ).fadeIn( "slow", function() {
			});			
        }		
	},
	logout: function() {
		rta.connected = false;
		window.location.href = "./login.jsp?action=logout";	
	},
	modal: function(title, body) {
		$("#modal-title").html('<i class="fa fa-info-circle"></i> ' + title);
		$("#modal-body").html(body);
		$("#modal-dialog").modal('show');
	}
};


$( window ).load(function() {
  rta.initialize();
});
