package fr.formation.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.formation.blog.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

}
