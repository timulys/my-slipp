package net.slipp.web;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.slipp.domain.Question;
import net.slipp.domain.QuestionRepository;
import net.slipp.domain.User;

@Controller
@RequestMapping("/questions")
public class QuestionController { 
	@Autowired
	private QuestionRepository questionRepository;
	
	@GetMapping("/form")
	public String form(HttpSession session, Model model) {
		if (!HttpSessionUtils.isLoginUser(session)) {
			return "/users/loginForm";
		}
		model.addAttribute("users", HttpSessionUtils.getUserFromSession(session));
		return "/qna/form";
	}
	
	@PostMapping("")
	public String create(String title, String contents, HttpSession session) {
		if (!HttpSessionUtils.isLoginUser(session)) {
			return "/users/loginForm";
		}
		User sessionedUser = HttpSessionUtils.getUserFromSession(session);
		Question newQuestion = new Question(sessionedUser, title, contents);
		questionRepository.save(newQuestion);
		
		return "redirect:/";
	}
	
	@GetMapping("/{id}")
	public String show(@PathVariable Long id, Model model) {
		model.addAttribute("question", questionRepository.findById(id).get());
		return "/qna/show";
	}
	
	@GetMapping("/{id}/form")
	public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
		try {
			Question question = questionRepository.findById(id).get();
			hasPermission(question, session);
			model.addAttribute("question", question);
			return "/qna/updateForm";
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "/user/login";
		}
	}
	
	@PutMapping("/{id}")
	public String update(@PathVariable Long id, String title, String contents, HttpSession session, Model model) {
		try {
			Question question = questionRepository.findById(id).get();
			hasPermission(question, session);
			question.update(title, contents);
			questionRepository.save(question);
			
			return String.format("redirect:/questions/%d", id);
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "/user/login";
		}
	}
	
	@DeleteMapping("/{id}")
	public String delete(@PathVariable Long id, Model model, HttpSession session) {
		try {
			Question question = questionRepository.findById(id).get();
			hasPermission(question, session);
			questionRepository.deleteById(id);
			return "redirect:/";
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "/user/login";
		}
	}

	private boolean hasPermission(Question question, HttpSession session) {
		if (!HttpSessionUtils.isLoginUser(session)) {
			throw new IllegalStateException("로그인이 필요합니다.");
		}
		User loginUser = HttpSessionUtils.getUserFromSession(session);
		if (!question.isSameWriter(loginUser)) {
			throw new IllegalStateException("자신이 쓴 글만 수정, 삭제가 가능합니다.");
		}
		
		return true;
	}
	
}
