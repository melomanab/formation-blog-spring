package fr.formation.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.formation.blog.domain.Article;

/**
 * Interface permettant de définir un Repository Spring. Nous utilisons ici
 * l'interface JpaRepository de Spring pour demander au Framework de fabriquer
 * un DAO évolué qui manipule une entité JPA/Hibernate.
 */
public interface ArticleRepository extends JpaRepository<Article, Integer> {

}
