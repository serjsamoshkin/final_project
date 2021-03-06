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

        <div class="col-lg-8 ">
            ${master.name}!!!!!
            <table class="table table-hover">
                <tr>
                    <th><fmt:message key="reception-time"/></th>
                    <th><fmt:message key="client-name"/></th>
                    <th><fmt:message key="client-service"/></th>
                    <th><fmt:message key="reception-status"/></th>
                    <th></th>
                    <th></th>
                </tr>
                <c:forEach items="${master_schedule}" var="schedule">
                    <c:set var="reception" scope="page" value="${schedule.value}"/>
                    <c:set var="time" scope="page" value="${schedule.key}"/>
                    <tr>
                        <td><ex:formatTime shortTime="${time}"/></td>
                        <c:choose>
                            <c:when test="${reception.reserved == true}">
                                <td>${reception.user}</td>
                                <td>${reception.service}</td>
                                <td><fmt:message key="status-${reception.status}"/></td>
                            </c:when>
                            <c:otherwise>
                                <td></td><td></td><td></td>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${reception.processed == false}">
                                <td>
                                    <a href="javascript: submit_done('${reception.id}', '${reception.version}')"><fmt:message key="done"/></a>
                                </td>
                                <td>
                                    <a href="javascript: submit_canceled('${reception.id}', '${reception.version}')"><fmt:message key="canceled"/></a>
                                </td>
                            </c:when>
                            <c:otherwise>
                                <td></td><td></td>
                            </c:otherwise>
                        </c:choose>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>

<form method="get" name="change_status_form" action="<c:url value="/master/change_reception"/>"></form>
<script type="text/javascript">

    function submit_done(id, version) {
        add_id_input(id);
        add_status_input('DONE');
        add_version_input(version);

        document.forms["change_status_form"].submit();
    }

    function submit_canceled(id, version) {
        add_id_input(id);
        add_status_input('CANCELED');
        add_version_input(version);

        document.forms["change_status_form"].submit();
    }

    function add_id_input(id) {
        $('<input>').attr({
            type: 'hidden',
            name: 'id',
            value: id
        }).appendTo('form')
    }

    function add_version_input(version) {
        $('<input>').attr({
            type: 'hidden',
            name: 'version',
            value: version
        }).appendTo('form')
    }

    function add_status_input(status) {
        $('<input>').attr({
            type: 'hidden',
            name: 'status',
            value: status
        }).appendTo('form')
    }

</script>

</body>
</html>

