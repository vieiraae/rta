<%@ page import="weblogic.security.Security"%>
<%@ page import="weblogic.security.SubjectUtils"%>
<%@ page import="weblogic.servlet.security.ServletAuthentication"%>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>Sign in to Real-Time Analytics</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.4 -->
    <link href="adminLTE/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
    <!-- Font Awesome Icons -->
    <link href="adminLTE/font-awesome-4.4.0/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
    <!-- Theme style -->
    <link href="adminLTE/dist/css/AdminLTE.css" rel="stylesheet" type="text/css" />

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <body class="login-page">
    <div class="login-box">
      <div class="login-logo">
        <a><i class="fa fa-dashboard"></i> Real-Time Analytics</a>
      </div><!-- /.login-logo -->
      <div class="login-box-body">
        <p class="login-box-msg">Sign in to start your session</p>
        <form action="./login.jsp" method="post">
          <div class="form-group has-feedback">
            <input name="username" id="username" type="text" class="form-control" placeholder="Username" autocomplete="off" />
            <span class="glyphicon glyphicon-user form-control-feedback"></span>
          </div>
          <div class="form-group has-feedback">
            <input name="password" id="password" type="password" class="form-control" placeholder="Password" value="welcome1" />
            <span class="glyphicon glyphicon-lock form-control-feedback"></span>
          </div>
          <div class="form-group has-feedback">
            <button id="signin" type="submit" class="btn btn-primary btn-block btn-flat">Sign In</button>
			<br/>
            <input id="remember" type="checkbox"> Remember Me</input>
          </div>
		  <input type="hidden" name="action" value="login"/>
        </form>
		<br/>
		<%
		if (request.getParameter("action") != null && (request.getParameter("action").toString().length()) > 0) {
			 switch (request.getParameter("action")) {
				 case "login":
					ServletAuthentication.generateNewSessionID(request);
					if (request.getParameter("username") != null && (request.getParameter("username").toString().length()) > 0) {
						if (ServletAuthentication.weak(request.getParameter("username").toString(), request.getParameter("password").toString(), request.getSession()) == ServletAuthentication.AUTHENTICATED)
							out.print("<div class='login-success'>Welcome " + SubjectUtils.getUsername(Security.getCurrentSubject()) + "</div><script type='text/javascript'>window.location.assign('.');</script>");										
						else
							out.print("<div id='login-message' class='login-error'>Invalid Credentials. Please try again.</div>");
					}
					break;
				 case "logout":
					if (ServletAuthentication.invalidateAll(request))
						out.print("<div id='login-message' class='login-success'>Logout successful...</div>");
					else
						out.print("<div id='login-message'  class='login-error'>Failed to logout. Please try again.</div>");
					break;
				 case "reject":
					out.print("<div id='login-message' class='login-error'>Only one connection is allowed per user.</div>");
					break;
				 case "error":
					out.print("<div id='login-message' class='login-error'>Unknow error.</div>");
					break;				 
				 default:
					out.print("<div id='login-message' class='login-error'>Unrecognized action.</div>");				 
			 }			
		}
		%>	
		
		<!--
        <div class="social-auth-links text-center">
          <p>- OR -</p>
          <a href="#" class="btn btn-block btn-social btn-facebook btn-flat"><i class="fa fa-facebook"></i> Sign in using Facebook</a>
          <a href="#" class="btn btn-block btn-social btn-google btn-flat"><i class="fa fa-google-plus"></i> Sign in using Google+</a>
        </div>

        <a href="#">I forgot my password</a><br>
        <a href="register.html" class="text-center">Register a new membership</a>
		 -->
		 
      </div><!-- /.login-box-body -->
    </div><!-- /.login-box -->

    <!-- jQuery 2.1.4 -->
    <script src="adminLTE/plugins/jQuery/jQuery-2.1.4.min.js" type="text/javascript"></script>
    <!-- Bootstrap 3.3.2 JS -->
    <script src="adminLTE/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
	<script>
        function readCookie(name) {
            var nameEQ = name + "=";
            var ca = document.cookie.split(';');
            for (var i = 0; i < ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) == ' ') c = c.substring(1, c.length);
                if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
            }
            return null;
        }
		$('#signin').click(function(e) {
					if ($('#remember').is(":checked"))
						document.cookie = "rta_remember_login=" + $('#username').val() + "; path=/";
					else
						document.cookie = "rta_remember_login=; path=/";
				});				
		var username = readCookie("rta_remember_login");
		if (username) {
			$("#remember").prop("checked", true);
			$('#username').val(username);
		}
		$('#username').focus();
		function cleanLoginMessage() {
			var elem = document.getElementById("login-message");			
			if (elem != null) {
				elem.style.display = "none";
			}
		}
		setTimeout(cleanLoginMessage, 5000);
	</script>

  </body>
</html>
