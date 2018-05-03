package fr.formation.blog.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.formation.blog.domain.Article;
import fr.formation.blog.repository.ArticleRepository;

@Controller
public class IndexController {

	/**
	 * Injection du bean id="article".
	 */
	@Autowired
	private Article article;

	/**
	 * Injection du bean d'implémentation d'ArticleRepository généré par Spring.
	 */
	@Autowired
	private ArticleRepository articleRepository;

	// @GetMapping("/delete")
	// ModelAndView deleteArticle(@RequestParam Integer articleId) {
	// this.articleRepository.deleteById(articleId);
	// return this.displayIndex();
	// }

	@GetMapping("/delete/{articleId}")
	ModelAndView delete(@PathVariable(name = "articleId") Integer id) {
		// Supprimer l'article.
		this.articleRepository.deleteById(id);
		// Renvoyer vers la vue welcome.
		return this.displayIndex();
	}

	/**
	 * Méthode liée à l'URL "/form" pour une requête GET. Le client contacte
	 * l'application sur cette URL lorsqu'il veut afficher la page contenant le
	 * formulaire pour éditer des articles.
	 *
	 * @return ModelAndView l'objet de Spring contenant les données du modèle et
	 *         le nom de la vue à afficher.
	 */
	@RequestMapping("/form")
	ModelAndView displayForm() {
		ModelAndView mav = new ModelAndView("form");
		// Préparer un nouvel article à remplir.
		return mav;
	}

	/**
	 * Méthode liée à l'URL "/welcome" pour une requête GET. Le client contacte
	 * cette page lorsqu'il veut afficher la page d'accueil avec la liste des
	 * articles.
	 *
	 * @return ModelAndView l'objet de Spring contenant les données du modèle
	 *         (la liste des articles) et le nom de la vue à afficher (welcome).
	 */
	@RequestMapping(path = "/welcome", method = RequestMethod.GET)
	ModelAndView displayIndex() {
		ModelAndView mav = new ModelAndView("welcome");
		final List<Article> articles = new ArrayList<>();
		articles.add(this.article);
		articles.addAll(this.articleRepository.findAll());
		mav.getModel().put("articles", articles);
		return mav;
	}

	/**
	 * Getter pour la propriété "article".
	 *
	 * @return Article l'article bean singleton défini dans le contexte Spring.
	 */
	public Article getArticle() {
		return this.article;
	}

	/**
	 * Setter pour la propriété "article".
	 *
	 * @param article le bean à injecter depuis le contexte Spring.
	 */
	public void setArticle(Article article) {
		this.article = article;
	}

	/**
	 * Méthode liée à l'URL "/form" pour une requête POST. Les paramètres
	 * définis avec l'annotation "@RequestParam" permettent de récupérer
	 * automatiquement les paramètres HTTP envoyés dans la requête.
	 *
	 * @param title le titre envoyé depuis le formulaire.
	 * @param description la description envoyé depuis le formulaire.
	 * @return ModelAndView le résultat de la méthode displayIndex() pour
	 *         afficher l'accueil.
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
}
