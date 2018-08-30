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

    <div class="container-fluid">

        <h2><fmt:message key="Review-user"/>: ${reception.user.name} (${reception.user.email})</h2>
        <br>
        <form class="form-horizontal">
            <div class="form-group">
                <label class="col-sm-2 "><fmt:message key="Reception-day"/>:</label>
                <div class="col-sm-4">
                    <p class="form-control-static">${reception.day}</p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 "><fmt:message key="Reception-time"/>:</label>
                <div class="col-sm-4">
                    <p class="form-control-static">${reception.time}</p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 "><fmt:message key="Reception-end-time"/>:</label>
                <div class="col-sm-4">
                    <p class="form-control-static">${reception.endTime}</p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 "><fmt:message key="Reception-master"/>:</label>
                <div class="col-sm-4">
                    <p class="form-control-static"><ex:localizeFieldValue fieldName="name" value="${reception.master}"/></p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 "><fmt:message key="Reception-service"/>:</label>
                <div class="col-sm-4">
                    <p class="form-control-static"><ex:localizeFieldValue fieldName="name" value="${reception.service}"/></p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 "><fmt:message key="Comment"/>:</label>
                <div class="col-sm-8">
                    <textarea readonly class="form-control" rows="5" id="comment" name="comment">${review.text}</textarea>
                </div>
            </div>
        </form>

    </div>
</div>

</body>
</html>

