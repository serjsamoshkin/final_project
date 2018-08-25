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
                    <th><fmt:message key="reception-time"/></th>
                    <th><fmt:message key="reception-end-time"/></th>
                    <th><fmt:message key="client-service"/></th>
                    <th><fmt:message key="reception-status"/></th>
                    <th></th>
                    <th></th>
                </tr>
                <c:forEach items="${reception_list}" var="reception">
                    <tr>
                        <td><ex:formatTime shortTime="${reception.time}"/></td>
                        <td><ex:formatTime shortTime="${reception.endTime}"/></td>
                        <td>${reception.service}</td>
                        <td><fmt:message key="status-${reception.status}"/></td>
                    </tr>
                </c:forEach>
            </table>
            <nav aria-label="pages">
                <ul class="pagination">
                    <li class="disabled"><a href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
                    <li class="active"><a href="#">1 <span class="sr-only">(current)</span></a></li>
                    <li>
                        <a href="#" aria-label="Next">
                            <span aria-hidden="true">&raquo;</span>
                        </a>
                    </li>
                </ul>
            </nav>
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

