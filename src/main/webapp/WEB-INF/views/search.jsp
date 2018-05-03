<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<!-- c.f. welcome.jsp -->
<jsp:include page="header.jsp">
	<jsp:param value="Rechrecher des articles..." name="title" />
</jsp:include>
<body class="container">
	<h1>Entrez une chaîne à rechercher dans le titre des articles</h1>
	<form method="post" class="form-inline">
		<div class="form-group">
			<label for="search">Recherche :</label>
			<input id="search" name="search" class="form-control" />
		</div>
		<button>Valider</button>
	</form>
	<c:if test="${not empty resultList}">
		<c:forEach items="${resultList}" var="article">
			<fieldset>
				<legend>${article.title}</legend>
				<p>${article.description}</p>			
			</fieldset>
		</c:forEach>
	</c:if>
	<h2>
		<c:url value="/welcome.xx" var="welcomeUrl" />
		<a href="${welcomeUrl}">Retour à la liste</a>
	</h2>
</body>
</html>