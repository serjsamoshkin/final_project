
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../parts/localized.jsp" %>
<html>
<head>
    <%@include file="../parts/header.jsp" %>
</head>
<body>
<div class="container">
    <%@ include file="../parts/navbar.jsp" %>
    <h1><fmt:message key="serverErrorEncountered"/></h1>
    <h2><fmt:message key="pleaseTryAgain"/></h2>
</div>
</body>
</html>
