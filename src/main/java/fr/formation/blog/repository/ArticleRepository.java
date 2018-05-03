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
	 *
	 * @param search ce que doit contenir le titre de l'article.
	 * @return List<Article> les articles dont le titre correspond.
	 */
	List<Article> findAllByTitleContaining(String search);
}
