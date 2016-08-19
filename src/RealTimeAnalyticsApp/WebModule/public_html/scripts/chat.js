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
var chat = {
    addChatMessage: function(data) {
        if (data) {
			chat.appendChatMessage(data.username, data.message);
        }
    },
	displayUsers: function() {
		$('#users-list').empty();
		$('#users-sayhello').hide();		
		if (rta.connected) {
			$.ajax({
				url: '/rta/resources/identity/cluster/users',
				type: 'GET',
				dataType: "json",
				cache: false				
			}).then(function(data) {				
				if (data.length == 1) {
					$('#users-connected').html('You are the only one connected');
				} else if (data.length > 1) {
					$('#users-connected').html('There are ' + (data.length - 1) + ' more user(s) connected');
					$('#users-sayhello').show();				
					data.forEach( function (arrayItem) {
						if (rta.username != arrayItem.username)
							$('#users-list').append("<li><span><i class='fa fa-user text-aqua'></i> " + arrayItem.username + "</span></li>");
					});
				}
			});			
		} else {
			$('#users-connected').html('You are not connected. Refresh the page to reconnect.');
		}
	},
	sayHello: function() {
		var message = new Object();
		message.username = rta.username;
		message.text = 'Hello &#9786;';			
		rta.websocket.send('rta.beans.Chat.sendChatMessageToAll(' + JSON.stringify(message) + ');');		
	},
	shake: function() {
		$("#main-div").shake(3,7,800);
	},
	postChatMessage: function() {
		var message = new Object();
		message.username = rta.username;
		message.text = $('#chat-message-text').val();			
		if (!message.text > 0) return;
		$('#chat-message-text').focus();		
		$('#chat-message-text').disabled = true;
		$('#chat-post-button').disabled = true;
		rta.websocket.send('rta.beans.Chat.sendChatMessageToAll(' + JSON.stringify(message) + ');');
		$('#chat-message-text').val('');		
	},
	appendChatMessage: function(dataUser, dataMessage) {
		$('#chat-message-text').disabled = false;
		$('#chat-post-button').disabled = false;	
		var currentDate = new Date();
		var time = currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds();	
		if (dataMessage == 'Hello &#9786;')
			chat.shake();
		if (dataUser == rta.username)
			$('#direct-chat-messages').append('<div class="direct-chat-msg"><div class="direct-chat-info clearfix"><span class="direct-chat-name pull-left">' + dataUser + '</span></i><span class="direct-chat-timestamp pull-right">' + time + ' <i class="fa fa-clock-o"></i></span></div><img class="direct-chat-img" src="adminLTE/dist/img/avatar5.png"/><div class="direct-chat-text">' + dataMessage + '</div></div>');
		else if (dataUser == 'system')
			$('#direct-chat-messages').append('<div class="direct-chat-msg right"><div class="direct-chat-info clearfix"><span class="direct-chat-name pull-right">' + dataUser + '</span><span class="direct-chat-timestamp pull-left">' + time + ' <i class="fa fa-clock-o"></i></span></div><img class="direct-chat-img" src="adminLTE/dist/img/chat-info.png"/><div class="direct-chat-text">' + dataMessage + '</div></div>');
		else
			$('#direct-chat-messages').append('<div class="direct-chat-msg right"><div class="direct-chat-info clearfix"><span class="direct-chat-name pull-right">' + dataUser + '</span><span class="direct-chat-timestamp pull-left">' + time + ' <i class="fa fa-clock-o"></i></span></div><img class="direct-chat-img" src="adminLTE/dist/img/chat-avatar.png"/><div class="direct-chat-text">' + dataMessage + '</div></div>');
		var d = $('#direct-chat-messages');
		d.animate({ scrollTop: d.prop('scrollHeight') }, 1000);			
	}
};
