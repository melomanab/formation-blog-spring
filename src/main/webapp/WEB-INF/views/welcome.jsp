<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<!-- Inclusion d'une page JSP dans la page actuelle.
 Attention aux éléments HTML uniques (balises head, body principalement). -->
<!-- Utilisation de l'objet HttpServletRequest pour stocker un paramètre "title" -->
<jsp:include page="header.jsp">
	<jsp:param value="Welcome !" name="title" />
</jsp:include>
<body>
	<h1>Liste des articles</h1>
	<c:forEach items="${articles}" var="article">
		<div title="${article.id}">
			<h2>${article.title}</h2>
			<p>${article.description}</p>
		</div>
	</c:forEach>
	<h2>
		<c:url value="/form.zzz" var="createUrl" />
		<a href="${createUrl}">Créer un article</a>
	</h2>
</body>
</html>