<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<jsp:include page="header.jsp" />
<body class="container">
	<h1>Création d'un article</h1>
	<form method="post">
		<div class="form-group">
			<label for="title">Titre :</label>
			<input id="title" name="title" class="form-control" />
		</div>
		<div class="form-group">
			<label for="descr">Description :</label>
			<textarea id="descr" name="descr" class="form-control"></textarea>
		</div>
		<button>Valider</button>
	</form>
	<h2>
		<c:url value="/welcome.xx" var="welcomeUrl" />
		<a href="${welcomeUrl}">Retour à la liste</a>
	</h2>
</body>
</html>