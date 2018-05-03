package fr.formation.blog.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	private String description;

	@Id
	@Column(name = "idArticle")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// @Basic
	@Column
	private String title;

	public Article() {
	}

	public Article(String title, String description) {
		this.title = title;
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public Integer getId() {
		return this.id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
