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


    <div class="container-fluid">
        <form action="<c:url value="/reception/show_receptions"/>" method="get" class="form-inline" name="filter_service_form">
            <div class="form-group">
                <label for="filter_service"><fmt:message key="filter-service-label"/>: </label>
                <select class="form-control .form-inline" id="filter_service" name="filter_service_opt">
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
            <input id="day" name="day" type="hidden" value="${reservation_day}">
        </form>
    </div>


</div>

</body>
</html>

