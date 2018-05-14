# README 3: Accès aux données avec Spring - II

- Projet: BlogSpring
- Auteur: *Beatriz MORENO*

Objectifs
Rajouter fonctionnalité "Suppresion d'un article"
Rajouter fonctionnalité "Edition d'un article"
Rajouter fonctionnalité "Recherche d'un article"

## Suppresion d'un article
### Modification de la vue `welcome.jsp`
- Inserer un icone *delete* par article
	 - Generer variable *imgUrl* (en dehors du forEach)
	 - Inclure un icone delete.png à travers le paramètre avec la balise img
- Associer l'icone à un requête delete
	- Generer variable *deleteUrl* (en dehors du forEach)
	- Inclure le l'url `${deleteUrl}/${article.id}.html` avec la balise  `<a href>` dans le forEach

> Attention: la balise img sera imbriquée dans la balise a !!!
> Pour un style poussé on va avoir besoin des containers Flex...	
```xml
<body class="container">
	<h1>Liste des articles</h1>
	<!-- Génération urls resources statiques -->
	<c:url value="/images" var="imgUrl" />
	<!-- Génération urls de matching avec le controlleur -->
	<c:url value="/form.zzz" var="createUrl" />
	<c:url value="/delete" var="deleteUrl" />

	<c:forEach items="${articles}" var="article">
	<!-- ===Section article=== -->
			<!-- Id (caché) -->
			<div title="${article.id}">
			
			<!-- Titre -->
			<div style="display:inline-flex;justify-content: space-evenly;min-width: 300px;">			
				<h2>${article.title}</h2>
					<!-- Ajout des icônes alignés avec le titre de l'article -->
					<span style="position: relative"> 
							<a href="${deleteUrl}/${article.id}.html"> 
							<img src="${imgUrl}/delete.png"
							style="position: absolute; top: 50%; transform: translate(0, -50%);" />
							</a>
					</span>
			</div>		
			<!-- Description -->
			<p>${article.description}</p>
				
			</div> <!-- End: Id (caché) -->
	</c:forEach>

	<p>

			<img src="${imgUrl}/add.png"> <a href="${createUrl}">Créer
			un article</a>
	</p>
```
### Modifications de la classe  `IndexController`
- Créer la méthode qui traite la requête *'/delete/{articleId}'*
```java
	/**
	 * Methode qui traite la requête avec l'url "/delete/{articleId}"
	 * et supprime l'article selectionnée dans la vue de la BDD, puis
	 * puis affiche la page de bienvenue avec la liste d'articles mise à jour
	 * @param id Identifiant de l'article
	 * @return ModelAndView Page de bienvenue 
	 */
	@GetMapping("/delete/{articleId}")
	ModelAndView delete(@PathVariable(name = "articleId") Integer id) {
		// Supprimer l'article.
		this.articleRepository.deleteById(id);
		// Renvoyer vers la vue welcome.
		return this.displayIndex();
	}
```

## Edition d'un article
### Modification de la vue `welcome.jsp`
- Insérer l'icône 'edit.png' aligné avec le titre
```xml
<!-- Ajout d'un icône edit aligné avec le titre de l'article -->
<span style="position: relative"> 
	<img src="${imgUrl}/edit.png" 
		style="position: absolute; 
		top: 50%; transform: translate(0, -50%);" />
</span>
```
- Imbriquer cet icône dans une balise `<a href=...> ...image... </a>` qui **génère la requête '/edit/articleId'**
```xml
<!-- Ajout d'un icône edit aligné avec le titre de l'article -->
	<span style="position: relative"> 
		<a href="${editUrl}/${article.id}">
					<img src="${imgUrl}/edit.png" 
					style="position: absolute; top: 50%; 
					transform: translate(0, -50%);" />
		</a>
	</span>
```
### Modifications de la classe  `IndexController`
Créer la méthode **displayEdit()** qui traite la requête *'/edit/{id}'* et renvoi sur un **formulaire d'édition** de l'article **pré rempli avec les infos de l'article en base de données**

- Récupération de la valeur id, passée dans l'url, comme argument de la méthode (annotation @PathVariable)
- Récupation d'un objet article correspondant à l'id de l'url, qui est rajouté comme **modèle** à la **vue 'edit'**
> RQ Utilisation du type **Optional** de `java.util` pour gerer l'absence de resultats dans la requête
- Renvoi de la vue dynamique mav

```java
	@GetMapping("/edit/{id}")
	ModelAndView displayEdit(@PathVariable(name = "id") Integer id) {
		ModelAndView mav = new ModelAndView("edit");
		Optional<Article> result = this.articleRepository.findById(id);
		if (result.isPresent()) {
			mav.addObject("articleToBeEdited", result.get());
		}
		return mav;
	}
```
### Création de la vue  `edit.jsp`
- Contient un formulaire Spring qui permet de charger une vue dynamique portant un objet article, ici **articleToBeEdited**
- La soumission du formulaire génère une requête "/edit.html" en méthode post

```xml
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<!-- c.f. welcome.jsp -->
<jsp:include page="header.jsp">
	<jsp:param value="Edition d'un article" name="title" />
</jsp:include>

<body class="container">
	<h1>Création d'un article</h1>
	<c:url value="/edit.html" var="editUrl" />
	
	<form:form modelAttribute="articleToBeEdited" method="post"
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
```
### Modifications de la classe  `IndexController`
Créer la méthode **validateEdit()** qui traite la requête *'/edit'* en méthode **POST** et renvoi sur la page d'accueil
- Utilisation de l'annotation ***@ModelAttribute***
> Avec les requêtes de type **POST**,  l'objet article est envoyé **dans le body de la reqûete** et non pas uniquement son id, à différence de ce qu'on faisait pour la méthode GET, où l'id était inclut dans le path de l'url

```java
@PostMapping("/edit")
ModelAndView validateEdit(@ModelAttribute Article article) {
	this.articleRepository.save(article);
	return this.displayIndex();
}
```
## Recherche d'un article
La interface Spring `JpaRepository` permet d'utiliser des méthodes de la DAO plus sophistiqués (autres que le CRUD). Ici on va implémenter une nouvelle méthode permettant la recherche des articles par des mots clés dans le titre.

### Modification de l'interface  `ArticleRepository.java`
- Ajout de la méthode **findAllByTitleContaining(String  search)**
```java
public interface ArticleRepository extends JpaRepository<Article, Integer> {
	/**
	 * Définition d'une requête Spring DataJpa permettant de rechercher des
	 * articles par le contenu du titre.
	 *
	 * @param search ce que doit contenir le titre de l'article.
	 * @return List<Article> les articles dont le titre correspond.
	 */
	List<Article> findAllByTitleContaining(String search);

}
```
### Modification de la classe `IndexController.java`
- Création de la méthode pour **afficher** à la vue `search`
- Création de la méthode pour **valider** la vue `search`
	- Renvoi une vue dynamique qui a pour *modèle* l'objet **resultList**
```java
@GetMapping("/search")
String displaySearch() {
	return "search";
}
@PostMapping("/search")
ModelAndView validateSearch(@RequestParam String search) {
	final ModelAndView mav = new ModelAndView("search");
	mav.addObject("resultList",
	this.articleRepository.findAllByTitleContaining(search));
	return mav;
}
```
### Création de lien d'accès à la vue `search.jsp` depuis `welcome.jsp`
```xml
<h2>
	<c:url value="/search.html" var="searchUrl" />
	<a href="${searchUrl}">Rechercher des articles</a>
</h2>
```

### Création de la vue  `search.jsp`
```xml
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<!-- c.f. welcome.jsp -->
<jsp:include page="header.jsp">
	<jsp:param value="Recherche d'articles" name="title" />
</jsp:include>
<body class="container">
	<h1>Entrez une chaîne à rechercher dans le titre des articles</h1>
	
	<form method="post" class="form-inline">
		<div class="form-group">
			<label for="search">Recherche :</label>
			<!-- Insertion d'un champ de saisie "search" qui enregistre 
			le mot clé servant à la recherche -->
			<input id="search" name="search" class="form-control" />
		</div>
		<button>Valider</button>
	</form>
	
	<!-- Section qui s'affiche uniquement si la recherche renvoi des resultats -->
	<c:if test="${not empty resultList}">
		<c:forEach items="${resultList}" var="article">
			<fieldset>
				<legend>${article.title}</legend>
				<p>${article.description}</p>			
			</fieldset>
		</c:forEach>
	</c:if>
	
	<!-- Lien de retour à la page d'accueil -->
	<h2>
		<c:url value="/welcome.xx" var="welcomeUrl" />
		<a href="${welcomeUrl}">Retour à la liste</a>
	</h2>
</body>
</html>
```
> Written with [StackEdit](https://stackedit.io/).
<!--stackedit_data:
eyJoaXN0b3J5IjpbLTgyOTk5ODYzMSwtNzg2NjU3MDIsLTk4OT
k3MjM5MSwxMTU2ODIzMjIzLC0xMTM5NTAwMzQwLDI3NjEzNDAw
MywtMTY5OTQ1MDQ0NywxNTM5MDQ3OTUsMTI4MTc2MTIyOSw4Mj
YzNjE5MSwxMDQ2ODYzODIxLC0xNzY1MjA5MDg4LDIxMTY1ODI1
MjYsLTEwODcwMzM1ODYsLTExNDQ0OTMwMzcsLTE0MjczODUzMj
MsMTg3NTE2NjMwMCwxMjg2MTU2NTg1LC05MTQxMzAyOTMsLTI0
ODM5MjQwN119
-->