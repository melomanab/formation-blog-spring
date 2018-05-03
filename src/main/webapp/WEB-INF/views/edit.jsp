<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<!-- c.f. welcome.jsp -->
<jsp:include page="header.jsp">
	<jsp:param value="Modification d'un article" name="title" />
</jsp:include>
<body class="container">
	<h1>Création d'un article</h1>
	
	<h2>
		<c:url value="/welcome.xx" var="welcomeUrl" />
		<a href="${welcomeUrl}">Retour à la liste</a>
	</h2>
</body>
</html>