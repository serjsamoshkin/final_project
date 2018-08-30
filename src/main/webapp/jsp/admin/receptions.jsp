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

        <div  class="col-lg-12 ">
            <h2><fmt:message key="reception-list"/>: </h2>
            <table id="myTable" class="table table-hover tablesorter">
                <tr>
                    <th id = "reception_day" onclick="sortTable('reception_day')"><fmt:message key="reception-day"/> ${signMap.reception_day}</th>
                    <th id = "reception_time" onclick="sortTable('reception_time')"><fmt:message key="reception-time"/> ${signMap.reception_time}</th>
                    <th><fmt:message key="reception-end-time"/></th>
                    <th><fmt:message key="reception-client"/></th>
                    <th><fmt:message key="reception-user-email"/></th>
                    <th><fmt:message key="reception-master"/></th>
                    <th><fmt:message key="reception-service"/></th>
                    <th><fmt:message key="reception-status"/></th>
                    <th><fmt:message key="reception-review"/></th>
                </tr>
                <c:forEach items="${reception_list}" var="reception">
                    <tr>
                        <td><ex:formatDate shortDate="${reception.day}"/></td>
                        <td><ex:formatTime shortTime="${reception.time}"/></td>
                        <td><ex:formatTime shortTime="${reception.endTime}"/></td>
                        <td>${reception.user.name}</td>
                        <td>${reception.user.email}</td>
                        <td><ex:localizeFieldValue fieldName="name" value="${reception.master}"/></td>
                        <td><ex:localizeFieldValue fieldName="name" value="${reception.service}"/></td>
                        <td><fmt:message key="status-${reception.status}"/></td>
                        <td><c:if test="${reception.hasReview == true}">
                            <a href="<c:url value="/administrator/show_review?review_id=${reception.review.id}"/>">review</a>
                        </c:if></td>

                    </tr>
                </c:forEach>
            </table>

            <nav aria-label="pages">
                <ul class="pagination">
                    <li><span aria-hidden="true"><fmt:message key="page-label"/>:</span></li>
                    <c:forEach begin="1" end="${page_count}" step="1" varStatus="loop">
                        <li <c:if test="${loop.count == page}"> class="active" </c:if>>
                            <a href="javascript: go_to_page(${loop.count})">${loop.count} <span class="sr-only"></span></a></li>
                    </c:forEach>
                </ul>
            </nav>
        </div>
    </div>
</div>

<form method="get" name="action_form" action="<c:url value="/administrator/show_receptions"/>"></form>
<script type="text/javascript">

    function go_to_page(page_num) {
        var c_sort_field = "${current_sort_field}";
        var c_order = "${current_order}";
        if (c_sort_field !== '' && c_order !== '') {
            add_sort_input(c_sort_field);
            add_direction_input(c_order);
        }
        add_page_input(page_num);

        document.forms["action_form"].submit();
    }

    function sortTable(col_id) {

        var c_sort_field = "${current_sort_field}";
        var c_order = "${current_order}";
        if (c_sort_field !== col_id){
            c_order = "asc"
        }else {
            c_order = c_order === "asc" ? "desc" : "asc";
        }
        add_sort_input(col_id);
        add_direction_input(c_order);
        if ("${page}" !== ''){
            add_page_input("${page}");
        }

        document.forms["action_form"].submit();
    }

    function add_page_input(page_num) {
        $('<input>').attr({
            type: 'hidden',
            name: 'page',
            value: page_num
        }).appendTo('form')
    }

    function add_sort_input(sort_opt) {
        $('<input>').attr({
            type: 'hidden',
            name: 'sort_by',
            value: sort_opt
        }).appendTo('form')
    }

    function add_direction_input(direction) {
        $('<input>').attr({
            type: 'hidden',
            name: 'direction',
            value: direction
        }).appendTo('form')
    }

</script>

</body>
</html>

