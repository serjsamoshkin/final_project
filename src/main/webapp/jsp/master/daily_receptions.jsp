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

        <div class="col-lg-3 ">
            ${master.name}
            <table class="table table-striped">
                <tr>
                    <th><fmt:message key="reception-time"/></th>
                    <th><fmt:message key="reservation-label"/></th>
                </tr>
                <c:forEach items="${master_schedule}" var="schedule">
                    <tr>
                        <td><ex:formatTime shortTime="${schedule.key}"/></td>
                        <td>
                            <c:if test="${schedule.value.reserved == true}">
                                <c:set var = "reception" scope = "page" value = "${schedule.value}"/>
                                ${schedule.value.user},
                                ${reception.user}
                                <fmt:message key="reserved"/>
                            </c:if>
                        </td>
                    </tr>

                </c:forEach>
            </table>
        </div>
    </div>
</div>

</body>
</html>

