<%-- 
    Document   : screen-dimension-error
    Created on : Feb 8, 2015, 5:20:00 AM
    Author     : Mershack
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Screen dimension Error</title>
    </head>
    <body>
        <div>
            <p>Please the screen dimension of your device is not appropriate for this study. <br>
                This study requires at least a device that has the following dimensions:
                width -  <%=request.getParameter("width")%>px,   height - <%=request.getParameter("height")%>px.
                 </p> 
        </div>
    </body>
</html>


