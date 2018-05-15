# README 1: Création d'un projet Spring
Ce document décrit comment créer une application J2EE utilisant les bases du framework Spring
- Formateur: *Jeremy* 
- Auteur: *Beatriz MORENO*

## Resumé de l'architecture du project
### src/main/java
- Classe **Article.java** du package `gtm.formation.blog.domaine`;
	- avec 3 propriétés:
```java
	private Integer id;
	private String title;
	private String description;
```
- Classe **IndexController.java** du package `gtm.formation.blog.presentation`;
	- contenant des méthodes qui se chargent de renvoyer des vues dynamiques au client
	- et une propriété 'article' injectée par Spring	
```java
@Controller
public class IndexController {
	@Autowired
	private Article article;
}
```
### src/main/webapp
- Fichiers de configuration `web.xml` et `application-servlet.xml`
- Vue **index.jsp**
- Repertoire `views` avec les vues Spring 
	- **welcome.jsp** affichant la liste d'articles sur le blog
	- **formulaire.jsp** affichant un formulaire pour la création d'un nouveau article


## Configuration Spring
### Configuration fichier`pom.xml` (Maven)
- Ajouter les dépendances suivantes:
	- **Spring** pour les modules `spring-context` et `spring-webmvc`
	- **JSTL** (expressions EL dans jsp)
	- **Logback** (gestion des logs)
	
```xml
		<!-- Dependance Spring 5.0.5 -->
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-context</artifactId>
			<version>5.0.5.RELEASE</version>
		</dependency>

		<!-- Dependance Servlet Spring 5.0.5 -->
		<!-- https://mvnrepository.com/artifact/org.springframework/spring-webmvc -->
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-webmvc</artifactId>
			<version>5.0.5.RELEASE</version>
		</dependency>

		<!-- Logback pour gestion des logs -->
		<!-- https://mvnrepository.com/artifact/ch.qos.logback/logback-classic -->
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-classic</artifactId>
			<version>1.2.3</version>
		</dependency>

		<!-- Dépendence JSTL 1.2 -->
		<!-- https://mvnrepository.com/artifact/javax.servlet/jstl -->
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>jstl</artifactId>
			<version>1.2</version>
		</dependency>
```

- Ajout **plugin** pour configurer le **compilateur Maven**:

```xml
	<build>
		<finalName>BlogSpring</finalName>
		<!-- Configuration compilateur Maven -->
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.7.0</version>

				<configuration>
					<!-- Encodage -->
					<encoding>UTF-8</encoding>
					<!-- Version Java du compilateur 1.8 -->
					<source>1.8</source>
					<!-- Version Java à l'execution 1.8 -->
					<target>1.8</target>
				</configuration>
			</plugin>
		</plugins>
	</build>
``` 
## Configuration Eclipse
- Ajout de l'**implémentation JSF** présente dans les les **librairies Tomcat (Jasper)** dans le projet Eclipse
	- Click droit sur le projet
	- Selectionner 'Properties/Java Build Path'
	- Click sur 'Add library' et selectionner repertoire d'installation Tomcat 
	- Verifier que 'Apache Tomcat v9.0' a été rajouté au build path
- Config de l'**encodage JSP** sur Eclipse
	- Window/Preferences/JSP Files
	- Choisir l'encoding: Unicode (UTF-8)

## Configuration du fichier `web.xml`
- Créer un fichier `web.xml` dans le repertoire `src/main/webapp/WEB-INF/`
- Déclaration de la **servlet Spring `DispatcherServlet`** chargée d'écouter toutes les requêtes entrantes
	- Classiquement, le **nom** déclaré est : `<servlet-name>`application`</servlet-name>`
	- Declarer l'**url pattern**: **`/`** qui sous-entend qu'elle s'occupe de l'ensemble de requêtes

```xml 
<web-app>
	<display-name>Première application Spring</display-name>

	<servlet>
		<servlet-name>application</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
	</servlet>
	
	<servlet-mapping>
		<servlet-name>application</servlet-name>
		<url-pattern>/</url-pattern>
	</servlet-mapping>
</web-app>
``` 
## Configuration du fichier `application-servlet.xml`
- Créer un fichier `application-servlet.xml` dans `src/main/webapp/WEB-INF/`
- Balise`<beans>`:
	- sert à configurer l'**IoC Container** ou **conteneur de beans**
	- déclare les **espaces de noms** contenant les libraires dans lesquelles  sont définies les balises qui figureront à l’intérieur du conteneur 
		-  xmlns:xsi, xmlns:p et xmlns:context

- Balise `<context:component-scan>`:
	- sert à déclarer la **package à scanner** pour retrouver les classes *@Controller* ou d'autres composants Spring: 
		- ici `fr.formation.blog.controller`, où se trouve la classe **IndexControlleur.java** 
		
- Balise `<bean>`:
Sert à déclarer les **beans Spring**. Les beans sont des classes gérées par Spring et qu'il va instancier au démarrage de l'application. Ces beans peuvent être de type métier ou de configuration Spring!
	- **Bean métier** de la classe **Article.java**:
		- paramètre *id*: porte souvent le nom de la classe en minuscule
		 > Sert à repérer le bean lors d'un injection!!
		- paramètre *class*:  référence cette classe dans l'application 
		- la balise `<constructor-arg>` sert à déclarer les **paramètres du constructeur** et la valeur au moment de l'instanciation du bean 
	- 	**Bean de configuration** de du **resolveur de vues Spring**:
		- Fait reference à la classe `org.springframework.web.servlet.view.InternalResourceViewResolver` fournie par Spring
	> Nécessaire pour que les **vues jsp** du dossier `/WEB-INF/views/` soient prises en compte par Spring et non par Tomcat!!!

```xml
<?xml version="1.0" encoding="UTF-8"?>
	
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">
   
    <!-- Activation du scan du package controller pour analyse des classes annotées -->
	<context:component-scan
		base-package="fr.formation.blog.controller" />

	<!-- Bean métier basique -->
	<bean id="article" class="fr.formation.blog.domain.Article">
		<constructor-arg type="Integer" value="1" />
		<constructor-arg type="String" value="Article n°1" />
		<constructor-arg type="String"
			value="Super description..." />
	</bean>

	<!-- Bean de configuration Spring : Objet permettant de résoudre les noms 
		de vues (ModelAndView) en page JSP -->
	<bean id="viewResolver"
		class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<property name="prefix" value="/WEB-INF/views/" />
		<property name="suffix" value=".jsp" />
		<property name="viewClass"
			value="org.springframework.web.servlet.view.JstlView" />
	</bean>
</beans>
```
## Utilisation
### Afficher la vue `welcome.jsp` à l'entrée de l'application
- Créer la vue `welcome.jsp` dans le dossier `webapp/WEB-INF/views/`
> A noter qu'elle est un vue dynamique qui utilise la variable "articles" dans une balise JSTL !! 
```xml
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>Liste d'articles</h1>
	<c:forEach items="${articles}" var="article">
		<div title="${article.id}">
			<h2> ${article.title}</h2>
			<p> ${article.description}</p>		
		</div>
	</c:forEach>
</body>
</html>
```
- Dans la vue `index.jsp`, ajouter une redirection à url="/welcome.html" avec la balise JSTL `<c:redirect>`
> Cette redirection sera prise en charge par un contrôleur avant d’afficher la page!
```xml
	<!-- Le tag redirect rajoute le contexte (ie /blog/) à la racine de la vue -->
	<c:redirect url="/welcome.html" />
```
- Dans le controlleur `IndexController`, ajouter la méthode **displayIndex()** 
	- Cette méthode recupera les *requêtes GET* portant l'url */welcome* de n'importequelle extension
		- annotation `@RequestMapping(path = "/welcome", method = RequestMethod.GET)`
	-  Et renvoi au un object **ModelAndView** côté client:
		- avec la **Vue** :`welcome`
		- et le **Modèle**: la liste d'articles 'articles' à laquelle on rajoute l'instance d'article injecté par Spring comme proprieté de la classe
```java
@Controller
public class IndexController {

	@Autowired
	private Article article;

	/**
	 * Methode qui traite l'url '/welcome' (appelée dans la vue 'index.jsp') et affiche la vue 'welcome' 
	 * @return ModelAndView
	 */
	@RequestMapping(path = "/welcome", method = RequestMethod.GET)
	ModelAndView displayIndex() {
		ModelAndView mav = new ModelAndView("welcome");

		// modele
		final List<Article> articles = new ArrayList<Article>();
		
		// rajout article inject� � la liste
		articles.add(this.article);
		
		// rajout liste � la vue
		mav.getModel().put("articles",  articles);
		
		return mav;
	}
```
### Insérer un lien dans la vue `welcome.jsp` pour acceder à la vue `formulaire.jsp` 

- Dans la vue **welcome.jsp**, ajouter une redirection à la vue  **formulaire.jsp **
	- Création d'une **url valide** pour le matching avec la méthode *displayFormulaire()* du controleur
Pour que l'url soit prise en compte par Spring, il est nécessaire de rajouter **le contexte de l'application** au nom de l'url avec la **balise JSTL  `<c:url>`** .
		> IMPORTANT:	Structure de l'URL
		CONTEXTE + SLASH + URL + EXTENSION
	
		- Ne pas oublier le **slash** avant le nom de l'url!
		- Le **nom de l'url** ne doit pas nécessairement coïncider avec le nom de la vue, mais uniquement avec le path de l'annotation @RequestMapping()
		- L'**extension de l'url** peut être *n'importe lequelle sauf .jsp*!!
			- On indique souvent '.html' 
		 > L'url complète est stockée dans la variable `formulaireUrl`

	- Redirection
	Utiliser la balise HTML `<a>` utilisant comme paramètre href la variable `formulaireUrl`: `href="${formulaireUrl}"`

```xml
	<h2>
	<c:url value="/formulaire.zzz" var="formulaireUrl"/> 
	<a href="${formulaireUrl}">Creer un article</a>	
	</h2>
```

- Pour qu'elle soit traitée par Spring, rajouter une méthode **displayFormulaire()** au controleur **IndexControleur**
```java
	/**
	 * Methode qui traite l'url '/formulaire' (appelée dans la vue 'welcome.jsp')  et affiche la vue 'formulaire'
	 * @return ModelAndView
	 */

	@RequestMapping(path = "/formulaire")
	ModelAndView displayForm() {
		ModelAndView mav = new ModelAndView("formulaire");	
		return mav;
	}
```
## Erreurs fréquents
### Les expressions EL de JSTL marchent pas?
- Verifier que dans la **taglib jstl** à l'entête de la page, le paramètre isELIgnored est "false" 

```xml
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
```
## Documentation
- https://spring.io/
- https://projects.spring.io/spring-framework/

## Suite du projet
* [Accès aux données avec Spring - I ](https://github.com/melomanab/formation-blog-spring/blob/master/README2.md)​ 

* [Accès aux données avec Spring - II ](https://github.com/melomanab/formation-blog-spring/blob/master/README3.md)​ 

* [ Intégration Logback, Filtre HTTP et WebServices avec Spring ](https://github.com/melomanab/formation-blog-spring/blob/master/README4.md)​  

> Written with [StackEdit](https://stackedit.io/).
<!--stackedit_data:
eyJoaXN0b3J5IjpbLTU5Nzg5NjczNyw5NTM3OTQ5NSwxMTQwOT
MzNDcsLTEzNzc5NjkwMzYsLTEwMzY2NTQ0MjIsMTYxNDkxNTA1
OSwxNjc4MjI3MjEyLC02OTQ2NDc0MjcsNzE4OTc5NzQyLDE2OT
gyOTU1MiwyNjgwOTEyNDgsLTEwODgzMTQ1OTQsODMyNjEwNDcy
LC0xMTEzMDA4NjUyLDE0NTQ4ODMyNDUsLTExMDE0ODcwODYsLT
EzMTIwOTI2NzUsNDU4MTU0Njg3LDIwMjEwODY0NDcsLTE3OTcw
OTY1NDRdfQ==
-->
