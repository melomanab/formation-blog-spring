<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<!-- Utilisation de la variable "title" définie dans jsp:include avec jsp:param. -->
		<title>${param.title}</title>
		<!-- La variable jsUrl contient "/blog/js" grâce au tag c:url. -->
		<c:url value="/js" var="jsUrl" />
		<script src="${jsUrl}/jquery-3.3.1.min.js"></script>
		<script src="${jsUrl}/bootstrap.min.js"></script>
		<c:url value="/css/bootstrap.min.css" var="bootstrapCssUrl" />
		<link rel="stylesheet" href="${bootstrapCssUrl}">
	</head>
</html>