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
                    <li><a href="#"><fmt:formatDate type="date" value="${reservation_day}"/></a></li>
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
                                ${service.key.name}
                        </option>
                    </c:forEach>
                </select>
            </div>
            <input id="day" name="day" type="hidden" value="${reservation_day_txt}">
        </form>
    </div>

    <div class="container-fluid">
        <c:forEach items="${masters_schedule}" var="master">

            <div class="col-lg-3 col-md-6 col-xs-12">
                    ${master.key.name}
                <table class="table table-striped">
                    <tr>
                        <th><fmt:message key="reception-time"/></th>
                        <th></th>
                    </tr>
                    <c:forEach items="${master.value}" var="schedule">
                        <tr>
                            <td><fmt:formatDate type="time" value="${schedule.key}"/></td>
                            <td>
                                <a href="javascript: submit_reservation('${schedule.key}','${master.key.id}')"><fmt:message key="reserve"/></a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </c:forEach>
    </div>
</div>

<form method="post" name="submit_reserve_form" action="<c:url value="/reception/process_reservation"/>">
    <input id="day_time" name="time" type="hidden">
    <input id="master" name="master" type="hidden">
    <input id="service_opt" name="filter_service_opt" type="hidden">
</form>
<form method="get" name="submit_day_form" action="<c:url value="/reception/show_receptions"/>">
    <input id="filter_service_opt" name="filter_service_opt" type="hidden">
</form>
<script type="text/javascript">

    var subm = document.getElementById("filter_service");
    subm.onchange = change_filter_service;


    function submit_reservation(day_time, master) {
        document.getElementById("day_time").value = day_time;
        document.getElementById("master").value = master;
        var sel = document.getElementById('filter_service');
        document.getElementById("service_opt").value = sel.options[sel.selectedIndex].value;
        document.forms["submit_reserve_form"].submit();
    }

    function change_filter_service() {
        document.forms["filter_service_form"].submit();
    }

    function submit_day(day) {
        add_day_input(day);
        var sel = document.getElementById('filter_service');
        document.getElementById("filter_service_opt").value = sel.options[sel.selectedIndex].value;
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


</script>
</body>
</html>

