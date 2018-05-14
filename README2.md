# README 2: Accès aux données avec Spring - I
Ce document décrit comment faire l'integration du framework de persistence de données *Hibernate/JPA* dans une application J2EE Spring

- Projet: BlogSpring
- Auteurs: *Beatriz MORENO*

## Architecture en couches
### Création de l'interface `ArticleRepository.java`
- Créer une interface appelée `ArticleRepository.java` dans le package `fr.formation.blog.repository`
	- implémentant l'**interface  Spring `JpaRepository`**: 
	`org.springframework.data.jpa.repository.JpaRepository`
	- 	bien spécifier les **arguments de l'interface**: `<T, ID>`
		- <T> correspond au **type de l'objet java** géré par JPA
		- <ID> correspond au **type d'id** de l'objet java géré par JPA
> Cette classe jouera un rôle important dans la persistance des objets java 
```java
package fr.formation.blog.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.formation.blog.domain.Article;

/**
* Interface permettant de définir un Repository Spring. Nous utilisons ici
* l'interface JpaRepository de Spring pour demander au Framework de fabriquer
* un DAO évolué qui manipule une entité JPA/Hibernate.
*/
public interface ArticleRepository extends JpaRepository<Article, Integer> {

/**
* Définition d'une requête Spring DataJpa permettant de rechercher des
* articles par le contenu du titre.
* @param search ce que doit contenir le titre de l'article.
* @return List<Article> les articles dont le titre correspond.
*/

List<Article> findAllByTitleContaining(String search);

}
```
## Configuration
###  Fichier de configuration Maven`pom.xml`:
Ajout dépendances suivantes:
- a. Module `spring-data-jpa`
- b. JAXB-API
- c. Hibernate core
- d. Connecteur JDBC
```xml
		<!--=========Integration JPA/Hibernate ============= -->
		<!-- Module Spring data-jpa -->
		<!-- https://mvnrepository.com/artifact/org.springframework.data/spring-data-jpa -->
		<dependency>
			<groupId>org.springframework.data</groupId>
			<artifactId>spring-data-jpa</artifactId>
			<version>2.0.6.RELEASE</version>
		</dependency>
		
		<!-- Driver mySQL JDBC (Java Database Connectivity) -->
		<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>5.1.6</version>
		</dependency>

		<!-- JPA/Hibernate -->
		<dependency>
			<groupId>org.hibernate</groupId>
			<artifactId>hibernate-core</artifactId>
			<version>5.2.16.Final</version>
		</dependency>

		<dependency>
			<groupId>javax.xml.bind</groupId>
			<artifactId>jaxb-api</artifactId>
			<version>2.3.0</version>
		</dependency>
```

### Fichier de configuration JPA `persistence.xml` 
Sert à déclarer l'**Unité de Persistence**, ici ***blog***
- Créer le fichier `persistence.xml` à l'interieur du repertoire `src/main/resources/META-INF` du projet
	- Déclarer l'*unité de persistence* `blog`
	- Déclarer les *propriétés* de cette unité
	C'est ici qu'il faut déclarer le détails de la connexion à la **base de données**, ici ***blogbdd***
		> IMPORTANT: le password pour l'accès à la base de données ne peu être une chaîne vide!!
			
		-  Les proprietés `<property name="hibernate.connection.url" value="jdbc:mysql://localhost:3306/blogbdd?createDatabaseIfNotExist=true" />` et  `<property name="hibernate.hbm2ddl.auto" value="update" />` permettent de **créer une bdd automatiquement** à partir des classes java qui portent des **annotations ORM**
   
      > Attention à ne pas specifier de 'mapping-file' : il n'y a pas de orm.xml puisqu'on utilise des annotations!!
		
	```xml
	<!-- Declaration de l'Unité de Persistence -->
	<persistence-unit name="blog">
		<provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
		<properties>
			<property name="hibernate.connection.driver_class" value="com.mysql.jdbc.Driver" />
			<property name="hibernate.connection.url" 
			value="jdbc:mysql://localhost:3306/blogbdd?createDatabaseIfNotExist=true" />
			<property name="hibernate.connection.user" value="root" />
			<property name="hibernate.connection.password" value="root" />
			<property name="hibernate.dialect" value="org.hibernate.dialect.MySQL57Dialect" />
			<!-- Generation de la base de données à partir des classes Java -->
			<property name="hibernate.hbm2ddl.auto" value="update"/>			
		</properties>
	</persistence-unit>
	```
###  Annotations JPA `javax.persistence`
Ces annotation servent à définir la correspondance (*mapping*) entre les **objects Java** et les **tables mySQL** à travers des **entités JPA**, un processus  connu comme ***Object-Relational-Mapping (ORM)*** 

- Etapes du **mapping ORM**:
	- Definir les *entités*
		- Classe Java  <=> Table SQL 
		 *@Entity  <=> @Table(name="article")*
	- Definir les proprietés
		- Attribut Java <=> Colonne SQL
		*@Column*	
	- Définir les relations entre les entités
		- OneToOne, OneToMany, ManyToOne <=> FK
		- ManyToMany <=> Table intermédiaire + 2 FK
		
- *Exemple d'annotation*: classe **Article.java**

```java
package fr.formation.blog.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
* Classe représentant une entité JPA décrite avec des annotations.
*/
// @Component : Annotation permettant de déclarer un bean Spring.
@Entity
@Table(name = "article")
public class Article {

/**
* Les propriétés Java sont automatiquement détéctée et liée à une colonne
* SQL sauf si le contraire est indiqué (mot-clé java 'transient').
*/

	@Id
	@Column(name = "idArticle")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// @Basic
	@Column
	private String title;

	// @Lob: poour proprietées avec un chaîne de caractères très longue
	@Lob
	private String description;

	...
}
```

### Fichier de configuration Spring: `applicationSpring-servlet.xml` 
Il sert à configurer le **contexte de persistence** via des nouveaux beans, ainsi qu'à déclarer les **repository Spring** de l'application

- Tout d'abord il faut modifier la balise `<beans>` du fichier xml pour **rajouter l'espace de noms `xmlns:jpa`**:
```xml
<!--Entête comprenant le xmlns:context -->
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:p="http://www.springframework.org/schema/p"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:jpa="http://www.springframework.org/schema/data/jpa" 
	xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/data/jpa
        http://www.springframework.org/schema/data/jpa/spring-jpa.xsd">
```
- Le **bean Spring  `entityManagerFactory`** gère le contexte de persistance
	- La proprieté `<property name="persistenceUnitName" value="blog" />` sert à déclarer **le nom de l'unité de persistence à gerer (blog)**
> Avec cette proprieté on donne accès de l’unité de persistance ciblée à l’EntityManager 
- Le **bean Spring `transactionManager`**, pour gerer les transactions automatiquement

```xml
	<!-- === Integration Hibernate/JPA=== -->
	<bean id="entityManagerFactory"
		class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
		<property name="persistenceUnitName" value="blog" />
	</bean>

	<bean id="transactionManager"
		class="org.springframework.orm.jpa.JpaTransactionManager">
		<property name="entityManagerFactory" ref="entityManagerFactory"/>
	</bean>
```
 - Déclarer le package à scanner pour retrouver **repository Spring**
```xml
	<!-- Activation du scan du package repository pour analyser les interfaces 
		et générer leurs implémentations CRUD++ -->
	<jpa:repositories
		base-package="fr.formation.blog.repository" />
```
> La configuration Spring-JPA est finie :) !!

## Utilisation JPA: premiers pas
###  Modification de la vue `form.jsp`
Cette vue contient un **formulaire** permettant rajouter une entrée 'article' en **base de données**
- Situé dans le répertoire `src/main/webapp/WEB-INF/views`
- La balise HTML `<form  method="post">` permettra de rajouter les valeurs introduites dans les champs du formulaire à la requête sous forme de **paramètres de la requête**
	-  La balise `<button>Valider</button>` doir se retrouver à l'interieur du formulaire pour **déclencher la requête**
	
> A NOTER: En clickant sur 'Valider'; une requête est générée ***avec l'url courante*** (/formulaire) pour rentrer dans la méthode **validateForm()** de la classe **IndexController**

```xml
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Création d'un article</title>
</head>
<body>

	<h1>Création d'un article</h1>
	<form method="post">
		<div class="form-group">
			<label for="title">Titre :</label> <input id="title" name="title"
				class="form-control" />
		</div>
		<div class="form-group">
			<label for="descr">Description :</label>
			<textarea id="descr" name="descr" class="form-control"></textarea>
		</div>
		<!-- Envoi d'une requête avec l'url courante avec la méthode post -->
		<button>Valider</button>
	</form>
	<h2>
		<c:url value="/welcome.xx" var="welcomeUrl" />
		<a href="${welcomeUrl}">Retour à la liste</a>
	</h2>
</body>
</html>
```
###  Modification de la classe  `IndexController.java`
- Ajouter méthode **validateForm()** 
Pour interagir avec la bdd, cette méthode a besoin d'utiliser le **bean articleRepository**. On va alors **injecter ce bean** en le déclarant comme propriété de la classe avec l'annotation ***@Autowired***
```java
@Autowired
private ArticleRepository articleRepository;
```
```java
/**
* Methode qui traite la requête avec l'url '/formulaire' (appelée dans la vue 'formulaire.jsp') de type POST
* et sauvegarde l'article rentré dans le formulaire en BDD
* puis affiche la liste d'articles (vue 'welcome')
* Les paramètres d'entrée correspondent aux balises <input> dans le formulaire
* @return ModelAndView
*/
	@RequestMapping(path = "/form", method = RequestMethod.POST)
	ModelAndView validateForm(@RequestParam String title,
			@RequestParam(name = "descr") String description) {
		// Sauvegarder l'article en BDD.
		final Article newArticle = new Article(title, description);
		this.articleRepository.save(newArticle);
		// Renvoyer vers la page de liste des articles.
		return this.displayIndex();
	}
```
- Modifier la méthode **displayIndex()** pour qu'elle affiche **la liste d'articles en base de données** en plus du **bean Spring**
```java
/**
* Méthode liée à l'URL "/welcome" pour une requête GET. Le client contacte
* cette page lorsqu'il veut afficher la page d'accueil avec la liste des
* articles.
*
* @return ModelAndView l'objet de Spring contenant les données du modèle
* (la liste des articles) et le nom de la vue à afficher (welcome).
*/

@RequestMapping(path = "/welcome", method = RequestMethod.GET)

ModelAndView displayIndex() {

	ModelAndView mav = new ModelAndView("welcome");
	
	final List<Article> articles = new ArrayList<>();
	articles.add(this.article);
	// ajout liste d'article en bdd
	articles.addAll(this.articleRepository.findAll());
	mav.getModel().put("articles", articles);

	return mav;
}
```

###  Lancement de l'application
- Allumer WAMP 
- Click droit à la racine du projet>Run as>Run on server
- Sur la page de bienvenue, clicker sur le lien *créer un article*
- Sur le formulaire, remplir les champs et clicker sur *Valider*
- Aller dans le gestionnaire **phpMyAdmin** et vérifier qu'une base de données nommé **blogbdd**  a été créé avec une table **article** contenant les informations du formulaire!

> Félicitations! L'intégration JPA-Spring a réussi :D

### Erreurs fréquents
#### Le serveur Tomcat n'arrive pas à afficher la page de bienvenue?
Cela vient peut être du serveur de la bdd, même si elle n'est pas sollicitée dès la première page, puisque on a demandé a Spring de generer la base de données au démarrage de l'application!!
- **Solution**: Lancer **WAMP**, pour activer le serveur de bdd mySQL 
	- Sous Linux: 
		- Taper sur cmd.exe: `sudo opt/lampp/lampp/start`

## Intégration bootstrap et images
Pour ne pas répéter les import des librairies bootstrap à chaque page, on va créer une page `header.jsp`  qui contient le code HTML nécessaire pour importer les librairies bootstrap rajoutées et qu'on inclura dans le reste de pages.
Un icone sera associé au lien 'Créer un nouveau article'

### Configuration
#### Ajouter librairies *CSS* et *JS* de **Bootstrap 4.0**  au projet Eclipse
-  Copier les librairies *CSS* et *JS* de **bootstrap 4.0** dans le repertoire `src/main/webapp/` du projet
-  Ajouter la librairie  **JQuery** car nécessaire por bootstrap 
	- copier le fichier `jquery-3.3.1.min.js` dans `/main/webapp/js/`
#### Ajouter un répertoire  `images` dans `src/main/webapp/images`
- Trouver un icone et le rajouter dans ce repertoire
	- Icone `add.png`
- Source: https://www.iconfinder.com/icons/314281/can_trash_icon#size=24
>  La taille 24x24 pixels convient bien à des icones

#### Créer la page `header.jsp` qui importe les libraires bootstrap
- Dans `src/main/webapp/WEB-INF/views`
> RQ. Cette page ne contient pas de body
```xml
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
<!-- Pas de body -->

</html>
```
#### Modification du fichier `applicationSpring-servlet.xml` 
Pour éviter que les **url des librairies bootstrap** soient prises en charge par Spring et qu'elle soient redirigées vers un Controller, il faut déclarer ces url comme **resources statiques**. Le repertoire **/images/** doit être declaré également.
- Utilisation balises `<mvc:annotation-driven>` et `<mvc:resources>`
```xml
<!-- Cette configuration permet de mettre en place automatiquement plusieurs bean Spring importants (plus d'infos : https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-config-enable) -->
<mvc:annotation-driven/>

<!-- Configuration de pattern d'URL qui ne doivent pas activer DispatcherServlet de Spring. Ces URL correspondent à un accès à des ressources statiques. -->
	<mvc:resources location="/js/" mapping="/js/**" />
	<mvc:resources location="/css/" mapping="/css/**" />
	<mvc:resources location="/images/" mapping="/images/**" />
```	
- Pour que cette valise soit disponible, il faut modifier les imports de la balise `<beans>`  pour rajouter **l'espace de noms `xmlns:mvc`**


```xml
<?xml version="1.0" encoding="UTF-8"?>

<!--Entête comprenant le xmlns:context -->
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:p="http://www.springframework.org/schema/p"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:jpa="http://www.springframework.org/schema/data/jpa" 
	xmlns:mvc="http://www.springframework.org/schema/mvc" 
	xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/data/jpa
        http://www.springframework.org/schema/data/jpa/spring-jpa.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd"
        >

	<!-- Déclaration resources statiques (bootstrap) -->
	<mvc:annotation-driven/>
	<mvc:resources location="/js" mapping="/js/**" />
	<mvc:resources location="/css" mapping="/css/**" />
```

### Utilisation dans la page
#### Bootstrap
- Remplacer la balise `<head>` dans les vues de l'application par : 
```xml
<html>
<!-- Inclusion d'une page JSP dans la page actuelle. Attention aux éléments HTML uniques (balises head, body principalement). -->
<!-- Utilisation de l'objet HttpServletRequest pour stocker un paramètre "title" -->
<jsp:include page="header.jsp">
	<jsp:param value="Welcome !" name="title" />
</jsp:include>

<body>
	<h1>Liste des articles</h1>
	...
```
#### Icones 
- Utiliser la balise JSTL `<c:url>`pour definir la variable **imgUrl** avec comme valeur de l'url le repertoire ***/images*** 
- Utiliser balise `img` pour **concatener l'url precedante au nom de l'image**
```xml
		<c:url value="/images" var="imgUrl" />
		<img src="${imgUrl}/add.png">
		
		<c:url value="/form.zzz" var="createUrl" />
		<a href="${createUrl}">Créer un article</a>
```
#### Lancement de l'application
- **Republier** le projet dans Tomcat
- **Lancer** l'application 
- **Vérifier** l'apparition d'un icône pour la création d'un article sur la page et du nouveau style bootstrap

> Written with [StackEdit](https://stackedit.io/).
<!--stackedit_data:
eyJoaXN0b3J5IjpbMTk1OTczNDk4NCwxMTgzOTUwNzY3LDIwND
I0MDg2ODEsMTUxMDE4NjYyNiwzMTk5MzMzMDksLTEyNjA2NjE2
MDgsLTE3ODI4NjEyNTMsLTM1MjM1NTgxMSwyOTA4MTE1MjEsMT
EwOTU3MDA2MywxNzc4NTMzMjk0LDEzMjc2NzM1OTgsLTY4MjQ3
MzIxNywyNTk4MzkxMTAsLTEwOTIwMTM5NzYsMTYxNzIxNTYzMi
wtMTE5MzkyNTQ4LDgyMDQ4Mjg4NSwtMTEyMzY2NjE4OSwtMjY2
ODczMzczXX0=
-->