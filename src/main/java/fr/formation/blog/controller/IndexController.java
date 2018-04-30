package fr.formation.blog.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import fr.formation.blog.domain.Article;

@Controller
public class IndexController {

	@Autowired
	private Article article;

	@RequestMapping("/form")
	ModelAndView displayForm() {
		ModelAndView mav = new ModelAndView("form");
		// Préparer un nouvel article à remplir.
		return mav;
	}

	/**
	 * Définition du lien avec les URLs qui déclenchent cette méthode.
	 *
	 * @return ModelAndView la vue welcome.
	 */
	@RequestMapping(path = "/welcome", method = RequestMethod.GET)
	ModelAndView displayIndex() {
		ModelAndView mav = new ModelAndView("welcome");
		final List<Article> articles = new ArrayList<>();
		articles.add(this.article);
		mav.getModel().put("articles", articles);
		return mav;
	}

	public Article getArticle() {
		return this.article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}
}
