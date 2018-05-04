package fr.formation.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.formation.blog.domain.Article;
import fr.formation.blog.repository.ArticleRepository;

@RestController
@RequestMapping("/api/article")
public class ArticleWebService {

	@Autowired
	private ArticleRepository articleRepo;

	@PostMapping({ "", "/" })
	Article create(@RequestBody Article article) {
		return this.articleRepo.save(article);
	}

	@DeleteMapping("/{articleId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void delete(@PathVariable Integer articleId) {
		this.articleRepo.deleteById(articleId);
	}

	@GetMapping({ "", "/" })
	List<Article> list() {
		return this.articleRepo.findAll();
	}

	@GetMapping("/{articleId}")
	Article read(@PathVariable Integer articleId) {
		return this.articleRepo.getOne(articleId);
	}

	@PutMapping("/{articleId}")
	Article update(@PathVariable Integer articleId,
			@RequestBody Article article) {
		return this.articleRepo.save(article);
	}
}
