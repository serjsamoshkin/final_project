<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="parts/localized.jsp" %>
<style type="text/css">

    <%--
    /* Adding !important forces the browser to overwrite the default style applied by Bootstrap */
    body {
        background: #F5F5F5 !important;
    }
    --%>


</style>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <%@include file="parts/header.jsp" %>
</head>
<body>


<div class="container">

    <%@ include file="parts/navbar.jsp" %>

    <h3><c:out value="Добро пожаловать, ${user.name}!"/></h3>

</div>


</body>
</html>