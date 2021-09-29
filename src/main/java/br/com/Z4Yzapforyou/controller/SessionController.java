package br.com.Z4Yzapforyou.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import br.com.Z4Yzapforyou.config.ConfigServer;
import br.com.Z4Yzapforyou.model.Session;
import br.com.Z4Yzapforyou.service.SessionService;

@Controller
@RequestMapping(value="/")
public class SessionController {
	
	@Autowired
	private SessionService sessionService;
	
	@Autowired
	private ConfigServer configServer;
	
	private Session session = new Session();
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String getPostNew() {
        return "index.html";
    }
	
	@GetMapping("/start")
	public String start() {
		try {
			session.setSessionName(configServer.getSessionName());
//			System.out.println(configServer.toString());
			Session session1 = new Session();
			session1 = sessionService.getSession(session);
			if(session1.getState() == null || session1.getStatus().equals("notLogged") || session1.getState().equals("CLOSED")) {
				
				session1 = sessionService.startSession(session);
//				System.out.println(session1.toString());
//				System.out.println(session.toString());
				return "index.html";
			}
			session.setState(session1.getState());
			session.setStatus(session1.getStatus());
//			System.out.println(session.toString());
//			System.out.println(session1.toString());
			return "index.html";
		}catch (Exception e) {
			System.out.println(e);
		}
		return "error-500.html";
	}
	
	@GetMapping("/qrcode")
	public ModelAndView qrcode() {
		ModelAndView mv; 
		String qrcode;
		try {
			session.setSessionName(configServer.getSessionName());
			Session session1 = new Session();
			session1 = sessionService.getSession(session);
//			System.out.println(session1.toString());
			if(session1.getState() != null && session1.getState().equals("CONNECTED")) {
				System.out.println("Entrou no qrcode");
				qrcode = sessionService.qrCode(session).getQrcode();
				mv = new ModelAndView("qrcode.html");
				mv.addObject("qrcode", qrcode);
				return mv;
			}
			
			mv = new ModelAndView("index.html");
			return mv;
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		mv = new ModelAndView("error-500.html");
		return mv;
	}
	
	@GetMapping("/messages")
	public String messages() {
		try {
			session.setSessionName(configServer.getSessionName());
			Session session1 = new Session();
			session1 = sessionService.getSession(session);	
			//System.out.println(session1.toString());
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				session.setState(session1.getState());
				return "messages.html";
			}
			session.setState(session1.getState());
			session.setStatus(session1.getStatus());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
	
	@GetMapping("/sendText")
	public String sendText(@ModelAttribute("Forms") @Validated Session session2) {
		try {
			session.setSessionName(configServer.getSessionName());
			Session session1 = new Session();
			session1 = sessionService.getSession(session);	
			//System.out.println(session1.toString());
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				session2.setSessionName(session.getSessionName());
				sessionService.sendText(session2);
				return "messages.html";
			}
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
	
	@GetMapping("/enviarmessagelista")
	public String enviarmessagelista() {
		try {
			session.setSessionName(configServer.getSessionName());
			Session session1 = new Session();
			session1 = sessionService.getSession(session);	
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				session.setState(session1.getState());
				return "messageslista.html";
			}
			session.setState(session1.getState());
			session.setStatus(session1.getStatus());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";	
	}
	
	@PostMapping("/enviarlista") 
	public String enviarlista(@ModelAttribute("Forms") @Validated Session session2, @RequestParam("files") MultipartFile file, @RequestParam("intervalo") String intervalo) throws IOException, ParseException {		
		try {
			session.setSessionName(configServer.getSessionName());
			Session session1 = new Session();
			session1 = sessionService.getSession(session);	
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				session2.setSessionName(session.getSessionName());
				sessionService.sendTextLista(session2, file, intervalo);
				return "messageslista.html";
			}
//			session.setState(session1.getState());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
	
	@GetMapping("/verificanumero")
	public String verificanumero() {
		try {
			session.setSessionName(configServer.getSessionName());
			Session session1 = new Session();
			session1 = sessionService.getSession(session);	
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				session.setState(session1.getState());
				return "verificanumero.html";
			}
			session.setState(session1.getState());
			session.setStatus(session1.getStatus());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
	
	@PostMapping("/verificanum")
	public ModelAndView verificaNum(@ModelAttribute("Forms") @Validated Session session2) {
		ModelAndView mv;
		boolean statusNun = false;
		try {
			session.setSessionName(configServer.getSessionName());
			Session session1 = new Session();
			session1 = sessionService.getSession(session);
			session2.setSessionName(session.getSessionName());
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				statusNun = sessionService.verificaNum(session2);
				mv = new ModelAndView("verificanumero.html");
				mv.addObject("verificaNum", session2.getNumber());
				mv.addObject("statusInit", "true");
				mv.addObject("verificaNumStatus", statusNun);
				return mv;
			}
			mv = new ModelAndView("index.html");
			return mv;
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		mv = new ModelAndView("error-500.html");
		return mv;
	}
	
	@GetMapping("/verificanumerolista")
	public String verificanumerolista() {
		try {
			session.setSessionName(configServer.getSessionName());
			Session session1 = new Session();
			session1 = sessionService.getSession(session);	
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				session.setState(session1.getState());
				return "verificanumerolista.html";
			}
			session.setState(session1.getState());
			session.setStatus(session1.getStatus());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";	
	}
	
	@PostMapping("/verificanumlista") 
	public String verificanumlista(@ModelAttribute("Forms") @Validated Session session2, @RequestParam("files") MultipartFile file) throws IOException, ParseException {		
		try {
			session.setSessionName(configServer.getSessionName());
			Session session1 = new Session();
			session1 = sessionService.getSession(session);
			session2.setSessionName(session.getSessionName());
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				sessionService.verificaNumLista(session2, file);
				return "verificanumerolista.html";
			}
//			session.setState(session1.getState());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
	
	@GetMapping("/close")
	public String close() {
		try {
			session.setSessionName(configServer.getSessionName());
			Session session1 = new Session();
			session1 = sessionService.getSession(session);	
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				Session session2 = new Session();
				session2 = sessionService.close(session);
				session.setState(session2.getState());
				session.setStatus(session2.getStatus());
				return "index.html";
			}
			session.setState(session1.getState());
			session.setStatus(session1.getStatus());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
	
	@GetMapping("/sendTextAndPdf")
	public String sendTextAndPdf() {
		try {
			session.setSessionName(configServer.getSessionName());
			Session session1 = new Session();
			session1 = sessionService.getSession(session);	
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				session.setState(session1.getState());
				return "sendtextandpdflista.html";
			}
			session.setState(session1.getState());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
	
	@PostMapping("/sendtextandpdflista") 
	public String sendtextandpdflista(@ModelAttribute("Forms") @Validated Session session2, @RequestParam("files1") MultipartFile file1, @RequestParam("files2") MultipartFile file2, @RequestParam("intervalo") String intervalo) throws IOException, ParseException {		
		try {
			session.setSessionName(configServer.getSessionName());
			Session session1 = new Session();
			session1 = sessionService.getSession(session);	
			session2.setSessionName(session.getSessionName());
			if(session1.getState() != null && !session1.getState().equals("CLOSED") && !session1.getStatus().equals("notLogged")) {
				sessionService.sendTextAndPdfLista(session2, file1, file2, intervalo);
				return "sendtextandpdflista.html";
			}
//			session.setState(session1.getState());
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
}
