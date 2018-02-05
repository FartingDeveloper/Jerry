<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML>
<html>
<head>
    <title>Login</title>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8"/>
    <link href="css/bootstrap.css" rel="stylesheet" type="text/css" media="all">
    <link href="css/style.css" rel="stylesheet" type="text/css" media="all"/>
    <link href="css/form.css" rel="stylesheet" type="text/css" media="all">
    <!-- Custom Theme files -->
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <script type="application/x-javascript"> addEventListener("load", function() { setTimeout(hideURLbar, 0); }, false); function hideURLbar(){ window.scrollTo(0,1); }>
    </script>
    <!--Google Fonts-->
    <link href='http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800' rel='stylesheet' type='text/css'>
    <!-- start-smoth-scrolling -->
    <script type="text/javascript" src="js/move-top.js"></script>
    <script type="text/javascript" src="js/easing.js"></script>
        <script type="text/javascript">
                jQuery(document).ready(function($) {
                    $(".scroll").click(function(event){
                        event.preventDefault();
                        $('html,body').animate({scrollTop:$(this.hash).offset().top},1000);
                    });
                });
        </script>
    <!-- //end-smoth-scrolling -->
</head>
<body>
<div class="title-main">
			<a href="${pageContext.request.contextPath}/login.jsp"><h1>SocialNetwork</h1></a>

</div>
 <div class="banner">
		<div class="form">
		    <form action="${pageContext.request.contextPath}/login" method="post">
		      <input type="text" name="login" placeholder="email"/>
		      <input type="password" name="password" placeholder="password"/>
		      <button>Login</button>
		      <p class="message">Not registered? <a href="${pageContext.request.contextPath}/signUp.jsp">Create an account</a></p>
		    </form>
		 </div>
</div>
</body>
</html>
