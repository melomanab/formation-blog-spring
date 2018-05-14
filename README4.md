# README 4: Intégration Logback, Filtre HTTP et WebServices avec Spring
Ce document décrit l’intégration Logback et WebServices dans le framework Spring
- Projet: BlogSpring
- Auteurs: *Beatriz MORENO*

## 1. Integration Logback

### Description Logging APIs
- Outil pour ***diagnostiquer des problèmes***
  - *Essentiel* pour faire de la maintenance
- Specification Oracle-Java: **JUL**
    - Elle n'utilise que les sorties standard
- Implémentations: **Log4J**, **Logback**
	- Les librairies logging permettent d'extendre cette API pour gerer une sortie fichier
- Objects Java principaux:
  - **Logger**: Production des logs/ Création de messages
  - **Handler/Appender**: Persister les logs
    - L'Appender permet de definir l'origine des logs à mettre dans un fichier de logs
  - **Level/Severity**: Definir l'importance du message
    - FATAL > ERROR > WARNING > INFO > DEBUG > TRACE
  - **Formatter**: Pattern/modèle de format de message
- Documentation:
  - https://docs.oracle.com/javase/8/docs/api/java/util/logging/package-summary.html

### Configuration Logback
#### Configuration du fichier Maven `pom.xml`
```xml
<!-- Logging API -->
<dependency>
	<groupId>ch.qos.logback</groupId>
	<artifactId>logback-classic</artifactId>
	<version>1.2.3</version>
</dependency>
```
#### Configuration du fichier `src/main/resources/logback.xml`
#### Loggers
 - Chaque **Logger** es configuré avec:
	 - un *nom*, qui spécifie le *package Java* à gérer par le Logger
	 > RQ. Un Logger gère les logs associés aux classes du package pour lequel il est configuré. Ici 3 loggers ont été configurés: un pour *les logs de l'application blog*, un autre pour *spring* et un autre pour *hibernate*
	 - un *level* de sévérité des logs
 - Chaque **Logger** est associé à un ou plusieurs **Appender**
	 > RQ. Par exemple ici le *Logger blog* est couplé au une *sortie console par l'appender STOUT* et une *sortie fichier avec l'appender APPLOG*
 - Le **Root Logger** est parent de toutes les instances **Logger**
	> RQ.  Il se charge de récupérer les logs générés par les classes non-gérés par le reste de Loggers
```xml
	<!-- ==== Loggers === -->
	<logger name="fr.formation.blog" level="debug" additivity="false">
		<appender-ref ref="STDOUT" />
		<appender-ref ref="APPLOG" />
	</logger>

	<logger name="org.springframework" level="info" additivity="false">
		<appender-ref ref="STDOUT" />
	</logger>

	<logger name="org.hibernate" level="info" additivity="false">
		<appender-ref ref="STDOUT" />
	</logger>
	<!-- Root logger -->
	<root level="error">
		<appender-ref ref="STDOUT" />
		<appender-ref ref="APPLOG" />
	</root>
```

#### Appenders
- Un **Appender** est configuré avec un *nom*, une *classe d'implementation* (destination des logs), et un *format de date*
	> RQ. On introduit ici deux types d'appenders: les  `ConsoleAppender` et les `FileAppender`

	- 	Le *format de date* peut être prédéfini dans la balise `<property>` du *fichier xml*
	- Les appenders de type **`FileAppender`**  vont générer un **sortie fichier**. En général ce fichier a une extension **.log** et se trouve à l’intérieur d'un répertoire **/logs/** à la racine du projet.
	> ATTENTION  à spécifier le chemin local pour le fichier des logs qu'on veut mettre en place!!!

```xml
	<!-- ==== Format === -->
	<property name="myPattern"
		value="%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level
		%logger{36} - %msg%n" />

	<!-- ==== Appenders ==== -->
	<!-- Appender sortie standard: console -->
	<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
		<layout class="ch.qos.logback.classic.PatternLayout">
			<Pattern>
				${myPattern}
			</Pattern>
		</layout>
	</appender>

	<!-- Appender sortie fichier: blog.log -->
	<appender name="APPLOG" class="ch.qos.logback.core.FileAppender">
		<file>Proyectos/GTM/workspaceSpring/formation-blog-spring/logs/blog.log
		</file>
		<layout class="ch.qos.logback.classic.PatternLayout">
			<Pattern>${myPattern}</Pattern>
		</layout>
	</appender>
```

#### Formatters
-  Les **Formatters** utilisent un format de date 
- SimpleDateFormat 
- GTM/UTC
 ```java
	 formatter = new SimpleDateFormat();
	 formatter.format(new Date());
	 formatter.parse("01/01/10");
  ```
#### Documentation
- Manuel: https://logback.qos.ch/manual/index.html
- En français: http://blog.xebia.fr/2010/07/07/java-en-production-les-fichiers-de-logs/
- Table caractères de format: https://logback.qos.ch/manual/layouts.html

### Test de configuration Logback
> RQ. Un fichier xml a été modifié. On aura besoin de republier l'application dans Tomcat pour que le fichier soit pris en compte!
- Republier l'application dans Tomcat
- Lancer l'application
- Vérifier qu'un dossier `/logs/` a été créé à l’intérieur du répertoire du projet avec un fichier `blog.log` à l’intérieur (pour l'instant vide) 

### Utilisation Logback
On va mettre en place la génération de messages de log dans la classe `IndexController`

- Chaque classe doit utiliser son propre **LOGGER** , declaré comme proprieté de la classe
	- de type ***private static final***
	- de type  `org.slf4j.Logger`
		- s'appui sur l'API **SLF4J** qui centralise toutes les sorties log
		- **SLF4J** est un import transitif de la dependence Maven Log4J
	
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Controller
public class IndexController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);
	...
```
- Ajout de messages
	- avec ou sans parametres
```java
	@RequestMapping(path = "/welcome", method = RequestMethod.GET)
	ModelAndView displayIndex() {
		
		IndexController.LOGGER.debug("Requête HTTP déclenchant la méthode displayIndex().");
		
		ModelAndView mav = new ModelAndView("welcome");
		final List<Article> articles = new ArrayList<>();
		articles.add(this.article);
		articles.addAll(this.articleRepository.findAll());
		mav.getModel().put("articles", articles);
		
		IndexController.LOGGER.debug("{} articles chargés dans le modèle MVC.", articles.size());
		
		return mav;
	}
```
- A l’exécution, ces deux messages de log sont générés (en console et dans le fichier `blog.log`):
>2018-05-04 12:11:32 [http-nio-8080-exec-4] DEBUG   f.f.blog.controller.IndexController - Requête HTTP déclenchant la méthode displayIndex().
>2018-05-04 12:11:32 [http-nio-8080-exec-4] DEBUG   f.f.blog.controller.IndexController - 3 articles chargés dans le modèle MVC.
  
## 2. Intégration Filtre HTTP pour l'encodage

### Contexte/Problématique
- Chrome (client) envoi des requêtes UTF-8
- Si le filtre n'est pas présent, le **décodage est géré par Windows**,  qui ne fait pas du 'UTF-8'
- Le **filtre HTTP** permet de spécifier le décodage de la requête, et le forcer à un encodage 'UTF-8'
	- Il est actif une fois par requête
	- Il agit avant que la requête passe à la servlet Spring

### Configuration 
#### Configuration du fichier `web.xml`
- Met à disposition la classe **CharacterEncodingFilter** du package  `org.springframework.web.filter `
	-  Déclarer un mapping sur toutes les requêtes
	- Le paramètre *encoding* est setté à *UTF-8*
	- Le paramètre *forceEncoding* est setté à *true*
```xml
	<!-- Filter d'encodage HTTP -->
	<filter>
		<filter-name>MyForceEncodeFilter</filter-name>
		<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
		<init-param>
			<param-name>encoding</param-name>
			<param-value>UTF-8</param-value>
		</init-param>
		<init-param>
			<param-name>forceEncoding</param-name>
			<param-value>true</param-value>
		</init-param>
	</filter>
	
	<filter-mapping>
		<filter-name>MyForceEncodeFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
```
- La modification de l'entête XML qui précède la balise `<web-app>` est aussi nécéssaire 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                      http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
	version="4.0">
```
### Execution
- Les nouveaux articles seront enregistrés sans fautes d'encodage
> RQ. L'encodage sera toujours faux pour les articles déjà enregistrées en base avant la mise en place du filtre

## 3. Integration WebServices

### Contexte: Les services web
- **L'architecture orientée services** (Service Oriented Architeture) a pour finalité de détacher/découpler au maximum les modules
	- permet d'optimiser les machines pour l'utilisation d'un module en particulier
	>WebServices permet de faire communiquer ces modules!!
	
- **WebServices** fait référence un protocole standard de communication entre deux applications
	- Standard SOAP: obsolet?
		- Utilise le langage de définition de WS: WSDL
	- Standard **REST**: utilise ***HTTP*** comme techno de transport de données

#### Standard **REST**
- Différents niveaux d'intelligence
	- ***Level 0***: Utilise HTTP
	- ***Level 1***: Defini une correspondance entre une url (*end-point*) et une entité (*resource*)
	- ***Level 2***: Utilise **les 4 verbes HTTP principaux:  Get/Post/Put/Delete**
		Pour Read/Create/Update/Delete		
		>  C'est celui qu'on utilisera
			
	- ***Level 3***: Utiliser l'**HyperMedia**
	La machine qui appelle le WebServices sait recuperer toute seule les infos pertinentes...
				
		>**Jersey** est un framework (implementation) de WebServices
		>**JAX-RS** est l'espec
		>**Jackson** est utilisé pour **convertir un objet Java en objet 				JSON** et inversement.
		>**Postman** joue le rôle du client WS
		
#### Documentation
REST: http://www.restapitutorial.com/lessons/restquicktips.html

### Configuration REST Web-Service
*In fine*, un web service Spring est un simple contrôleur qui renvoi...non pas une vue html mais une réponse HTTP?

####  Configuration du fichier `pom.xml` 
- Ajout dépendances Jackson:  jackson-databind
```xml
<!-- Librairie de serialisation/déserialisation JSON -->
<dependency>
	<groupId>com.fasterxml.jackson.core</groupId>
	<artifactId>jackson-databind</artifactId>
	<version>2.9.5</version>
</dependency>

<dependency>
	<groupId>com.fasterxml.jackson.datatype</groupId>
	<artifactId>jackson-datatype-hibernate5</artifactId>
	<version>2.9.5</version>
</dependency>
```
#### Annotation de la classe web-services `ArticleWS.java` (resource)
- Création de la classe `ArticleWS.java` dans le dans package `fr.formation.blog.controller`
- Annotations à niveau de la ***classe*** pour indiquer qu'il s'agit d'une *resource Rest* accessible à travers l'url  *"/api/article"*
	- @RestController
	- @RequestMapping("/api/article")
```java
package fr.formation.blog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/article")
public class ArticleWS {
...
}
```
- Annotation à niveau de la ***proprieté*** pour cablage avec l'article repository Spring
```java
	@Autowired
	private ArticleRepository articleRepo;
```
- Création et annotations des ***méthodes*** correspondantes au **CRUD**

##### Create (POST)
La méthode POST permet d'ajouter une ressource sur le serveur.
- Les requêtes HTTP de type POST ont un body/corps pour cacher les données. On utilisera alors l'annotation *@RequestBody* pour passer les arguments à cette méthode.
> ATTENTION: retourne un objet de type Article... ???
```java
	// Create
	@PostMapping({ "", "/" })
	Article create(@RequestBody Article article) {
		return this.articleRepo.save(article);
	}
```
##### Read (GET)
La méthode GET est utilisé pour demander une ressource
- Les requêtes HTTP de type GET n'ont pas de 'body' , alors les arguments reçus par cette méthode font forcement partie de l'URL (utilisation de l'annotation *@PathVariable*  pour récupérer l'articleId)
```java
	// Read
	@GetMapping("/{articleId}")
	Article read(@PathVariable Integer articleId) {
		return this.articleRepo.getOne(articleId);
	}
```
##### Update (PUT)
La méthode PUT est utilisée pour modifier une ressource.
- Il est commun de recevoir l'id de la ressource à modifier dans l'*url* (comme pour la méthode *read*); et les nouvelles informations de la ressource dans le *corps* de la requête (comme pour la méthode *create*)
> ATTENTION: retourne un objet de type Article, de même que les autres méthodes qui le précédent
```java
	// Update
	@PutMapping("/{articleId}")
	Article update(@PathVariable Integer articleId,
			@RequestBody Article article) {
		return this.articleRepo.save(article);
	}
```
##### Delete (DELETE)
La méthode DELETE est utilisée pour supprimer une ressource.
> Il est nécessaire spécifier qu'il n'y a pas de réponse attendue avec l'annotation: @ResponseStatus(code = HttpStatus.NO_CONTENT)
```java
	// Delete
	@DeleteMapping("/{articleId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void delete(@PathVariable Integer articleId) {
		this.articleRepo.deleteById(articleId);
	}
```	

##### Read all (GET)
Pour conclure, la méthode qui va nous permettre de lire toutes les ressources...
> RQ. Cette méthode utilise le même verbe HTTP que la méthode **read()**
> > Néanmoins, on la distingue de la méthode **read()** parce qu'il n'y a pas un id particulier spécifié dans la requête GET comme argument d'entrée de la méthode 
```java
	@GetMapping({ "", "/" })
	List<Article> readAll() {
		return this.articleRepo.findAll();
	}
```
## Utilisation
Test du web-service REST avec la méthode **readAll()**
- Taper dans le navigateur:
	- http://localhost:8080/blog/api/article
- Renvoi la liste d'articles!!
>[{"description":"is great! or not?","id":1,"title":"Article n°2"},{"description":"tojours pas...","id":7,"title":"Article n°3"},{"description":"tojours pas...","id":8,"title":"Article n°3"}]

## Erreurs fréquents
### LazyInitializationException
```
**cause mère**
org.hibernate.LazyInitializationException: could not initialize proxy - no Session
	org.hibernate.proxy.AbstractLazyInitializer.initialize(AbstractLazyInitializer.java:155)
```
- en utilisant la requête READ: http://localhost:8080/blog/api/article/1
- Problème de Proxy
- Il faut rajouter l'espace de noms tx 
```xml
<!--Entête comprenant le xmlns:context -->
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:p="http://www.springframework.org/schema/p"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:jpa="http://www.springframework.org/schema/data/jpa" 
	xmlns:mvc="http://www.springframework.org/schema/mvc"
	xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/data/jpa
        http://www.springframework.org/schema/data/jpa/spring-jpa.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd
        http://www.springframework.org/schema/tx
        http://www.springframework.org/schema/tx/spring-tx.xsd">

```
> NE  MARCHE TOUJOURS PAS

### Postman 
Client pour tester un WS
- On peut envoyer la requete GET : http://localhost:8080/blog/api/article et obtenir la même chose qu'avec Chrome
- Requete POST pour creer un article:
	- Selectionner 'POST' sur le menu deroulant
	- Introduire l'url: http://localhost:8080/blog/api/article
	- Selectionner 'Body' --> raw --> JSON (application/json) et copier le texte
```
{
"title": "Article creé par Postman",
"description": "N/A"
}
```

- Envoyer avec Send
- Dans l'ordre: post/get/update/delete
- Save requete
Collection (nom de l'application): blog

### En savoir plus


> Written with [StackEdit](https://stackedit.io/).
<!--stackedit_data:
eyJoaXN0b3J5IjpbNTkzODg5MTIxLC0zNTczNjE2MywxNTcyMT
U1NzExLDIxMjE5MDg2ODQsLTU1NDA5NjQwNiwtMTIzMDY3MjQ4
Nyw2MjczMzM1NTQsLTI3MjA3NjAyNSwtNDYzODY1NDk4LC0xMj
Y3MDYzMDYsODYyODIyMTI2LC03MDIwNjExNiwyMDIxNjIzMDE5
LC0xMzQ3NjkwODMzLC03MTkxNDA4OTYsMzU3MzAzMzU0LC04MD
MxNjI4MDcsMTQ1NTgyMzI4MSwtMTk3OTUxMTkxNSwxNjM1Mjgx
ODQxXX0=
-->