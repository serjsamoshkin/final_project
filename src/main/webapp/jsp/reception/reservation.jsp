<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../parts/localized.jsp" %>
<%@ taglib prefix = "ex" uri = "mytags"%>
<html>
<head>
    <%@include file="../parts/header.jsp" %>
</head>
<body>
<div class="container">
    <%@ include file="../parts/navbar.jsp" %>


    <form class="form-horizontal" action="<c:url value="/reception/confirm_reservation"/>" method="get" name="confirm_form">
        <div class="form-group">
            <label class="col-sm-2 control-label"><fmt:message key="master"/></label>
            <div class="col-sm-4">
                <p class="form-control-static">${master.name}</p>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><fmt:message key="date"/></label>
            <div class="col-sm-4">
                <p class="form-control-static"><ex:formatDate shortDate="${date}"/> </p>
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label"><fmt:message key="time"/></label>
            <div class="col-sm-4">
                <p class="form-control-static"><ex:formatTime shortTime="${time}"/></p>
            </div>
        </div>
        <input type="hidden" name="master" value=${master.id}>
        <input type="hidden" name="day" value=${date}>
        <input type="hidden" name="time" value=${time}>
        <div class="form-group">
            <label for="service" class="col-sm-2 control-label"><fmt:message key="choose_service"/></label>
            <div class="col-sm-4">
                <select class="form-control .form-inline" id="service" name="filter_service_opt">
                    <option >
                    </option>
                    <c:forEach items="${service_map}" var="service">
                        <option value="${service.key.id}"
                                <c:if test="${service.value == true}">
                                    selected
                                </c:if>
                        >
                                ${service.key.name}
                        </option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <button type="submit" class="btn btn-default"><fmt:message key="reserve"/></button>

    </form>

</div>

</body>
</html>

