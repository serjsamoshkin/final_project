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


    <form class="form-horizontal" action="<c:url value="/reception/confirm_reservation"/>" method="post" name="confirm_form">
        <div class="form-group">
            <label class="col-sm-2 control-label"><fmt:message key="master"/></label>
            <div class="col-sm-4">
                <p class="form-control-static"><ex:localizeFieldValue fieldName="name" value="${master}"/></p>
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
            <div class="col-sm-4 inline-block">
                <p class="form-control-static"><ex:formatTime shortTime="${time}"/>   to: <ex:formatTime shortTime="${time_end}"/></p>
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
                            <ex:localizeFieldValue fieldName="name" value="${service.key}"/>
                        </option>
                    </c:forEach>
                </select>
                <fmt:message key="duration"/>:
                <c:choose>
                    <c:when test="${hours_duration != 0}">
                        ${hours_duration} <fmt:message key="hours"/>
                    </c:when>
                    <c:when test="${minutes_duration != 0}">
                        ${minutes_duration} <fmt:message key="minutes"/>
                    </c:when>
                </c:choose>
            </div>
        </div>

        <button type="submit" class="btn btn-default"><fmt:message key="reserve"/></button>

    </form>

</div>
<form method="get" name="filter_service_form" action="<c:url value="/reception/process_reservation"/>">
    <input type="hidden" name="master" value=${master.id}>
    <input type="hidden" name="day" value=${date}>
    <input type="hidden" name="time" value=${time}>
</form>
<script type="text/javascript">

    var subm = document.getElementById("service");
    subm.onchange = change_filter_service;

    function change_filter_service() {
        var sel = document.getElementById('service');
        add_service_input(sel.options[sel.selectedIndex].value)
        document.forms["filter_service_form"].submit();
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

