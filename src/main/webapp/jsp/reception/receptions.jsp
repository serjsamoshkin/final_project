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

        <nav aria-label="day">
            <div class="col-xs-3">
                <ul class="pager">
                    <c:choose>
                        <c:when test="${previous_day == ''}">
                            <li class=" previous disabled">
                                <a href="#"><span aria-hidden="true">&larr;</span> <fmt:message key="previous-day"/></a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class=" previous">
                                <a href="javascript: submit_day('${previous_day}')">
                                    <span aria-hidden="true">&larr;</span> <fmt:message key="previous-day"/></a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
            <div class="col-xs-6">
                <ul class="pager">
                    <li><a href="#"><ex:formatDate shortDate="${reservation_day}"/></a></li>
                </ul>
            </div>
            <div class="col-xs-3">
                <ul class="pager">
                    <li class="next"><a href="javascript: submit_day('${next_day}')">
                            <fmt:message key="next-day"/>
                        <span aria-hidden="true">&rarr;</span></a></li>
                </ul>
            </div>
        </nav>
    </div>

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
                                <ex:localizeFieldValue fieldName="name" value="${service.key}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
            <input id="day" name="day" type="hidden" value="${reservation_day}">
            <fmt:message key="duration"/>:
            <c:choose>
            <c:when test="${hours_duration != 0}">
                ${hours_duration} <fmt:message key="hours"/>
            </c:when>
                <c:when test="${minutes_duration != 0}">
                    ${minutes_duration} <fmt:message key="minutes"/>
                </c:when>
        </c:choose>
        </form>
    </div>

    <div class="container-fluid">
        <c:forEach items="${masters_schedule}" var="master">

            <div class="col-lg-3 col-md-6 col-xs-12">
                <ex:localizeFieldValue fieldName="name" value="${master.key}"/>
                <table class="table table-hover">
                    <tr>
                        <th><fmt:message key="reception-time"/></th>
                        <th></th>
                    </tr>
                    <c:forEach items="${master.value}" var="schedule">
                        <tr>
                            <%--<td><fmt:formatDate type="time" value="${schedule.key}"/></td>--%>
                            <td><ex:formatTime shortTime="${schedule.key}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${schedule.value == false}">
                                        <a href="javascript: submit_reservation('${reservation_day}', '${schedule.key}','${master.key.id}')"><fmt:message key="reserve"/></a>
                                    </c:when>
                                    <c:otherwise>
                                     <fmt:message key="reserved"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </c:forEach>
    </div>
</div>

<form method="get" name="submit_reserve_form" action="<c:url value="/reception/process_reservation"/>"></form>
<form method="get" name="submit_day_form" action="<c:url value="/reception/show_receptions"/>"></form>
<script type="text/javascript">

    var subm = document.getElementById("filter_service");
    subm.onchange = change_filter_service;

    function submit_reservation(day, time, master) {
        add_day_input(day);
        add_time_input(time);
        add_master_input(master);
        var sel = document.getElementById('filter_service');
        add_service_input(sel.options[sel.selectedIndex].value);

        document.forms["submit_reserve_form"].submit();
    }

    function change_filter_service() {
        document.forms["filter_service_form"].submit();
    }

    function submit_day(day) {
        var sel = document.getElementById('filter_service');
        add_service_input(sel.options[sel.selectedIndex].value);
        add_day_input(day);

        document.forms["submit_day_form"].submit();
    }

    function add_day_input(day) {
        $('<input>').attr({
            type: 'hidden',
            id: 'day',
            name: 'day',
            value: day
        }).appendTo('form')
    }

    function add_time_input(time) {
        $('<input>').attr({
            type: 'hidden',
            id: 'time',
            name: 'time',
            value: time
        }).appendTo('form')
    }

    function add_master_input(master) {
        $('<input>').attr({
            type: 'hidden',
            id: 'master',
            name: 'master',
            value: master
        }).appendTo('form')
    }

    function add_service_input(service) {
        if (service !== ""){
            $('<input>').attr({
                type: 'hidden',
                id: 'service_opt',
                name: 'filter_service_opt',
                value: service
            }).appendTo('form')
        }
    }
</script>
</body>
</html>

