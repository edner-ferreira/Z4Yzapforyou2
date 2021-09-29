package br.com.Z4Yzapforyou.controller;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.com.Z4Yzapforyou.service.ContatosService;

@Controller
public class ContatosController {
	
	@Autowired
	private ContatosService contatosService;
	
	@GetMapping("/arquivojson")
	public String arquivojson() {
		try {
			return "criararquivojson.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
			return "error-500.html"; 
		}
		
	}
	
	@PostMapping("/criarjson") 
	public String verificanumlista(@RequestParam("filesDiretorio") String filesDiretorio, @RequestParam("filesName") MultipartFile filesName, @RequestParam("filesTel") MultipartFile filesTel) throws IOException, ParseException {		
		try {		
//			System.out.println("File diretorio:" + filesDiretorio + "\n" +
//			"file name: " + filesName.getOriginalFilename() + "\n" + 
//			"file telefone: " + filesTel.getOriginalFilename() + "\n");
			contatosService.initContatos(filesName, filesTel, filesDiretorio);
			return "index.html";
		}catch (Exception e) {
			System.out.println("Error internal 500");
		}
		return "error-500.html";
	}
}