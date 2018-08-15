<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../parts/localized.jsp" %>
<html>
<head>
    <%@include file="../parts/header.jsp" %>
</head>
<body>
<div class="container">
    <%@ include file="../parts/navbar.jsp" %>


    <table class="table table-striped">

        <tr>
            <th><fmt:message key="email"/></th>
            <th><fmt:message key="name"/></th>
            <th><fmt:message key="roles"/></th>
        </tr>
        <c:forEach items="${users}" var="user">
            <tr>
                <td><c:out value="${user.email}"/></td>
                <td><c:out value="${user.name}"/></td>
                <td><c:out value="${user.rolesStr}"/></td>
            </tr>
        </c:forEach>

    </table>

</div>
</body>
</html>
