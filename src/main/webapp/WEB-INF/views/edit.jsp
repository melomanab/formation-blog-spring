<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<!-- c.f. welcome.jsp -->
<jsp:include page="header.jsp">
	<jsp:param value="Modification d'un article" name="title" />
</jsp:include>
<body class="container">
	<h1>Création d'un article</h1>
	<c:url value="/modify.html" var="editUrl" />
	<form:form modelAttribute="editArticle" method="post"
		action="${editUrl}">
		<!-- Important pour conserver la valeur du champ 'id'
			lors de la soumission du formulaire. -->
		<form:hidden path="id" />
		<div class="form-group">
	 		<label for="title">Titre :</label>
			<form:input id="title" path="title"
				cssClass="form-control"  />
		</div>
		<div class="form-group">
	 		<label for="descr">Description :</label>
			<form:textarea id="descr" path="description"
				cssClass="form-control"></form:textarea>
		</div>
		<form:button class="btn btn-success">Valider</form:button>
	</form:form>
	<h2>
		<c:url value="/welcome.xx" var="welcomeUrl" />
		<a href="${welcomeUrl}">Retour à la liste</a>
	</h2>
</body>
</html>