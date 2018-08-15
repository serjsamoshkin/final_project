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

    <div class="col-lg-6 col-md-6 col-xs-9">

        <h2><fmt:message key="registration"/>:</h2>
        <form action="<c:url value="/registration/register"/>" method="post" data-toggle="validator" role="form">
            <div class="form-group">
                <label for="user-name" class="control-label"><fmt:message key="name"/></label>
                <input type="text" class="form-control" name="user-name" id="user-name" required value=${user_name_r}>
            </div>
            <div class="form-group">
                <label for="email" class="control-label"><fmt:message key="yourEmail"/></label>
                <input type="email" class="form-control" name="email" id="email"
                       placeholder="example@examlpe.com" required value=${user_email_r}>
                <div class="has-error">
                    <p class="help-block" name="incorrect_email" id="incorrect_email">
                        <c:if test="${incorrect_email == true}">
                            <fmt:message key="incorrect_email"/>
                        </c:if>${incorrect_email_r}
                    </p>
                </div>
            </div>
            <div class="form-group">
                <label for="password" class="control-label"><fmt:message key="password"/></label>
                <div class="form row">
                    <div class="form-group col-lg-6">
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder=
                               <fmt:message key="yourPassword"/> required>
                    </div>
                    <div class="form-group col-lg-6">
                        <input type="password" class="form-control" id="confirmPassword"
                               placeholder=
                               <fmt:message key="confirmPassword"/> required>
                    </div>
                </div>
            </div>

            <script type="text/javascript">
                var password = document.getElementById("password");
                var confirmPassword = document.getElementById("confirmPassword");
                var email = document.getElementById("email");

                password.onchange = validatePassword;
                confirmPassword.onchange = validateConfirmPassword;
                email.onchange = validateEmail;

                function validatePassword() {
                    var strongRegex = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{6,})");
                    //console.log(strongRegex.test(password.value));
                    if (!strongRegex.test(password.value)) {
                        password.setCustomValidity("<fmt:message key="passwordNotMatch"/>");
                    }
                    else {
                        password.setCustomValidity('');

                    }
                }

                function validateConfirmPassword() {
                    if (password.value !== confirmPassword.value) {
                        confirmPassword.setCustomValidity("<fmt:message key="pleaseEnterTheSamePassword"/>");
                    } else {
                        confirmPassword.setCustomValidity('');
                    }
                }

                function validateEmail() {
                    $.ajax({
                        url: "/registration/check-email",
                        data: {"email": email.value},
                        cache: false,
                        type: "POST",
                        success: function (responseText) {
                            checkEmailResult(responseText);
                        },
                        error: function (xhr) {

                        }
                    });
                }

                function checkEmailResult(responseText) {
                    if (responseText !== '') {
                        email.setCustomValidity("<fmt:message key="incorrect_email"/>");
                        $("#incorrect_email").text("<fmt:message key="incorrect_email"/>");
                    } else {
                        email.setCustomValidity('');
                        $("#incorrect_email").text('');
                    }
                }
            </script>

            <div class="form-group">
                <button type="submit" class="btn btn-primary" id="submit"><fmt:message key="register"/></button>
            </div>
        </form>
    </div>
</div>


</body>
</html>


