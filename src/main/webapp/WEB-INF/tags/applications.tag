<%@tag description="Applications Page template" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="base" value="${pageContext.request.contextPath}/public-applications/${period.slug}" />
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <title>Applications - ${period.name.content}</title>

    <!-- Bootstrap -->
    <link href="${pageContext.request.contextPath}/themes/${portal.configuration.theme}/css/style.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    <style>
    .container {
      background-color: #fff;
      color: #617383;
      border-bottom-right-radius: 10px;
      border-bottom-left-radius: 10px;
    }
    .header, .header:hover, .header:focus {
      color: inherit;
      text-decoration: none;
    }
    .locale-box {
      border: 1.5px solid #eee;
      border-radius: 2px;
      padding: 8px;
      text-transform: capitalize;
      cursor: pointer;
    }
    .footer {
      padding-bottom: 25px;
    }
    .locale-dropdown {
      margin-top: 8px;
      box-shadow: none;
      border: 1.5px solid #eee;
      border-radius: 2px;
      text-transform: capitalize;
    }
    .locale-dropdown>li>a {
      color: #617383;
      padding-left: 24px;
    }
    </style>
  </head>
  <body>
    <div class="container">
      <div class="page-header">
        <img class="pull-right" src="${pageContext.request.contextPath}/api/bennu-portal/configuration/logo" title="${portal.configuration.applicationTitle.content}" />
        <h1><a class="header" href="${pageContext.request.contextPath}/public-applications/${period.slug}">${period.name.content}</a></h1>
      </div>
      <jsp:doBody/>
      <div class="footer clearfix">
        <hr />
        <div class="row">
          <div class="col-sm-4">
            <span class="locale-box">
              <span class="glyphicon glyphicon-globe"></span>
              ${portal.locale.getDisplayLanguage(portal.locale)}
            </span>
            <div class="dropdown">
              <ul class="dropdown-menu locale-dropdown">
                <c:forEach var="locale" items="${portal.supportedLocales}">
                  <li><a href="" role="button" data-locale="${locale}">${locale.getDisplayLanguage(locale)}</a></li>
                </c:forEach>
              </ul>
            </div>
          </div>
          <div class="col-sm-4 text-center">
            ${portal.configuration.applicationCopyright.content}
          </div>
          <div class="col-sm-4"></div>
        </div>
      </div>
    </div>
    
    <script>
      $(".locale-box").click(function () {
        $(".locale-dropdown").toggle();
      });
      $("[data-locale]").click(function (e) {
        var locale = e.delegateTarget.getAttribute("data-locale").replace("_", "-");
        $.post("${pageContext.request.contextPath}/api/bennu-core/profile/locale/" + locale).always(function() { location.reload(); });
      });
    </script>
  </body>
</html>