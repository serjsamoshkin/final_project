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
    <div class="col-lg-6 col-md-6 col-xs-9">
        <form action="<c:url value="/login/confirm"/>" method="post" role="form" data-toggle="validator">
            <h2><fmt:message key="login"/></h2>
            <div class="form-group">
                <label for="email"><fmt:message key="email"/>:</label>
                <input type="email" name="email" id="email" class="form-control" value="${email_r}">
            </div>
            <div class="form-group">
                <label for="password"><fmt:message key="password"/>:</label>
                <input type="password" name="password" id="password" class="form-control">
                <div class="has-error">
                    <p class="help-block" name="incorrect_email" id="incorrect_email">
                        <c:if test="${user_not_found == true}">
                            <fmt:message key="userNotFound"/>
                        </c:if>
                    </p>
                </div>
            </div>
            <button type="submit" class="btn btn-primary"><fmt:message key="login"/></button>
        </form>
    </div>
</div>
</body>
</html>