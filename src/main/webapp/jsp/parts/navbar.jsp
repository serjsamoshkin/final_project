<%@ page contentType="text/html;charset=UTF-8" %>

<nav class="navbar navbar-default">
    <div class="container-fluid">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/"><fmt:message key="web-site-name"/></a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">

                <li class="nav-item ${init_active}">
                    <a class="nav-link" href="<c:url value="/"/>"><fmt:message key="home"/></a>
                </li>
                <c:if test="${user.authenticated == false or user.user == true}">
                    <li class="nav-item ${reception_active}">
                        <a class="nav-link" href="<c:url value="/reception/show_receptions"/>"><fmt:message key="book-service"/></a>
                    </li>
                </c:if>
                <c:if test="${user.master == true}">
                    <li class="nav-item ${master_active}">
                        <a class="nav-link" href="<c:url value="/master/"/>"><fmt:message key="master"/></a>
                    </li>
                </c:if>
            </ul>
            <ul class="nav navbar-nav navbar-right">

                <c:if test="${user.authenticated == true}">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value="#"/>">${user.name}</a>
                    </li>
                </c:if>
                <c:if test="${user.user == true}">
                    <li class="nav-item ${my_receptions_active}">
                        <a class="nav-link" href="<c:url value="/my_receptions/show_my_receptions"/>"><fmt:message key="my-receptions"/></a>
                    </li>
                </c:if>
                <c:if test="${user.authenticated == false}">
                    <li class="nav-item ${registration_active}">
                        <a class="nav-link" href="<c:url value="/registration/show_registration_form"/>"><fmt:message key="register"/></a>
                    </li>
                </c:if>
                <c:choose>
                    <c:when test="${user.authenticated == false}">
                        <li class="nav-item ${login_active}">
                            <a class="nav-link" href="/login">
                                </span><fmt:message key="login"/>
                                <span class="glyphicon glyphicon-log-in"></span>
                            </a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item">
                            <a class="nav-link" href="<c:url value="/login/logout"/>"><fmt:message key="logout"/></a>
                        </li>
                    </c:otherwise>
                </c:choose>
                <ul class="nav navbar-nav navbar-right">
                    <li role="presentation" class="dropdown">
                        <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                            <span class="glyphicon glyphicon-globe"></span> <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu">
                            <li><a href="<c:url value="/set_locale?lang=en"/>">Eng</a></li>
                            <li><a href="<c:url value="/set_locale?lang=ru"/>">Rus</a></li>
                        </ul>
                    </li>
                </ul>
            </ul>
        </div>
    </div>
</nav>

