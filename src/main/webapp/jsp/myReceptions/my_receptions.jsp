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

        <div class="col-lg-12">
            <table class="table table-hover">
                <tr>
                    <th><fmt:message key="reception-day"/></th>
                    <th><fmt:message key="reception-time"/></th>
                    <th><fmt:message key="reception-end-time"/></th>
                    <th><fmt:message key="reception-master"/></th>
                    <th><fmt:message key="reception-service"/></th>
                    <th><fmt:message key="reception-status"/></th>
                </tr>
                <c:forEach items="${reception_list}" var="reception">
                    <tr>
                        <td><ex:formatDate shortDate="${reception.day}"/></td>
                        <td><ex:formatTime shortTime="${reception.time}"/></td>
                        <td><ex:formatTime shortTime="${reception.endTime}"/></td>
                        <td><ex:localizeFieldValue fieldName="name" value="${reception.master}"/></td>
                        <td><ex:localizeFieldValue fieldName="name" value="${reception.service}"/></td>
                        <td><fmt:message key="status-${reception.status}"/></td>
                    </tr>
                </c:forEach>
            </table>

            <nav aria-label="pages">
                <ul class="pagination">
                    <li><span aria-hidden="true"><fmt:message key="page-label"/>:</span></li>
                    <c:forEach begin="1" end="${page_count}" step="1" varStatus="loop">
                        <li <c:if test="${loop.count == page}"> class="active" </c:if>>
                            <a href="<c:url value="/my_receptions/show_my_receptions?page=${loop.count}"/>">${loop.count} <span class="sr-only"></span></a></li>
                    </c:forEach>
                </ul>
            </nav>
        </div>
    </div>
</div>

</body>
</html>

