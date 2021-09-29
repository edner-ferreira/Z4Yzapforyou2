package br.com.Z4Yzapforyou.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.Z4Yzapforyou.config.ConfigServer;
import br.com.Z4Yzapforyou.model.Session;
import reactor.core.publisher.Mono;

@Service
public class SessionService {
	
	@Autowired
	private WebClient webClientZ4y;
	
	@Autowired
	private ConfigServer configServer;
	
	public Session startSession(Session session) throws Exception{
		
		Mono<Session> monoSession = this.webClientZ4y
				.get()
				.uri("/start?sessionName=" + session.getSessionName())
				.retrieve()
				.bodyToMono(Session.class);
		
		Session session1 = new Session();
		
		session1 = monoSession.block();
		return session1;
	}
	
	public Session qrCode(Session session) throws Exception{
		Mono<Session> monoSession = this.webClientZ4y
				.get()
				.uri("/qrcode?sessionName=" + session.getSessionName()+"&image=true")
				.retrieve()
				.bodyToMono(Session.class);
		Session session1 = new Session();
		
		session1 = monoSession.block();
		return session1;
	}
	
	public void sendText(Session session) throws Exception{
		//System.out.println(session.toString());
		Mono<Session> monoSession = this.webClientZ4y
				.post()
				.uri("/sendText?number=" + session.getNumber() +"&text=" + session.getMessage() + "&sessionName=" + session.getSessionName())
				.retrieve()
				.bodyToMono(Session.class);
		
		Session session1 = new Session();
		
		session1 = monoSession.block();
//		System.out.println("Number: " + session.getNumber());
//		System.out.println(session1.toString());
		if(session1.getResult().equals("success")) {
			System.out.println("MSG enviada para " + session.getNumber() + " - OK");
	    } else {
	    	System.out.println("MSG enviada para " + session.getNumber() + " - FALHOU PQ NAO TEM ZAP!!!");
	    }		
	}
	
	public void sendTextLista(Session session, MultipartFile file, String intervalo) throws Exception{
		Path path = Paths.get(configServer.pathFile, file.getOriginalFilename());
		file.transferTo(path);
		JSONParser parser = new JSONParser();
		JSONArray jsonObject = (JSONArray) parser.parse(new FileReader(path.toString()));
		Double valorInter;
		
		if(!intervalo.equals("") && !intervalo.equals("0")) {
			valorInter = Double.valueOf(intervalo) * 60000;
			String[] aux = String.valueOf(valorInter).split("\\.");
			intervalo = aux[0];
		}
		
		int i = 1;
		Iterator ite = jsonObject.iterator();	
		while (ite.hasNext()) {
            JSONObject obj = (JSONObject) ite.next();

            LocalDateTime data = LocalDateTime.now();
            String auxName = session.getMessage();
            String backupmessages = session.getMessage();
            if(auxName.contains("$NOME$")) {
            	session.setMessage(auxName.replace("$NOME$", (CharSequence) obj.get("name")));
            }
            
            Mono<Session> monoSession = this.webClientZ4y
    				.post()    
    				.uri("/sendText?number=" + obj.get("number") + "&text=" + session.getMessage() + "\nConvite enviado: " + data.format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")) + "&sessionName=" + session.getSessionName())
    				.retrieve()
    				.bodyToMono(Session.class);
    		Session session1 = new Session();
    		
    		session1 = monoSession.block();
//    		System.out.println("Number: " + obj.get("number"));
    		
    		if(session1.getResult().equals("success")) {
    			System.out.println("MSG -" + i + "- enviada para " + obj.get("number") + " - OK");
    	    } else {
    	    	System.out.println("MSG -" + i + "- enviada para " + obj.get("number") + " - FALHOU PQ NAO TEM ZAP!!!");
    	    }
    		i++;
    		session.setMessage(backupmessages);
    		if(!intervalo.equals("") && !intervalo.equals("0")) {
    			Thread.sleep(Long.valueOf(intervalo));
    		}
        }
		i = 1;
		File arq = new File(path.toString());
		arq.delete();
	}
	
	public Session getSession(Session session) throws Exception{
		Mono<Session> monoSession = this.webClientZ4y
				.get()
				.uri("/getSession?sessionName=" + session.getSessionName())
				.retrieve()
				.bodyToMono(Session.class);
		Session session1 = new Session();
		
		session1 = monoSession.block();

		return session1;
	}

	public boolean verificaNum(Session session) {
		Mono<Session> monoSession = this.webClientZ4y
				.get()
				.uri("/checkNumberStatus?number=" + session.getNumber() + "&sessionName=" + session.getSessionName())
				.retrieve()
				.bodyToMono(Session.class);
		Session session1 = new Session();
		
		session1 = monoSession.block();
		if(session1.getStatus().equals("200"))
			return true;
		return false;
	}
	
	public void verificaNumLista(Session session, MultipartFile file) throws Exception{
		Path path = Paths.get(configServer.pathFile, file.getOriginalFilename());
		file.transferTo(path);
		JSONParser parser = new JSONParser();
		JSONArray jsonObjectArray = (JSONArray) parser.parse(new FileReader(path.toString()));
		LocalDateTime data = LocalDateTime.now();
		BufferedWriter buffWriteCompjson = new BufferedWriter(new FileWriter(configServer.pathFile + data.format(DateTimeFormatter.ofPattern("HH:mm:ss-dd-MM-yyyy")) + "_" + file.getOriginalFilename()));
		JSONObject jsonObject = new JSONObject();
		Iterator ite = jsonObjectArray.iterator();	
		
		buffWriteCompjson.append("["); 
		
		while (ite.hasNext()) {
            JSONObject obj = (JSONObject) ite.next();
            
            Mono<Session> monoSession = this.webClientZ4y
    				.get()
    				.uri("/checkNumberStatus?number=" + obj.get("number") + "&sessionName=" + session.getSessionName())
    				.retrieve()
    				.bodyToMono(Session.class);
    		Session session1 = new Session();
    		
    		session1 = monoSession.block();
    		
    		if(session1.getStatus().equals("200")) {
    			jsonObject.put("name", obj.get("name"));
    			jsonObject.put("number", obj.get("number"));
    			buffWriteCompjson.append(jsonObject.toString());
    		}
        }
		buffWriteCompjson.append("]");
		buffWriteCompjson.close();
		File arq = new File(path.toString());
		arq.delete();
	}

	public Session close(Session session) {
		Mono<Session> monoSession = this.webClientZ4y
				.get()
				.uri("/close?sessionName=" + session.getSessionName())
				.retrieve()
				.bodyToMono(Session.class);
		Session session1 = new Session();
		
		session1 = monoSession.block();
		return session1;
	}
	
	public void sendTextAndPdfLista(Session session, MultipartFile file1, MultipartFile file2, String intervalo) throws Exception{	
		byte[] bytes = file1.getBytes();
		Path path = Paths.get(configServer.pathFile, file1.getOriginalFilename());
		file1.transferTo(path);
		
		Path path2 = Paths.get(configServer.pathFilePdf, file2.getOriginalFilename());
		file2.transferTo(path2);
		
		JSONParser parser = new JSONParser();
		JSONArray jsonObject = (JSONArray) parser.parse(new FileReader(path.toString()));
		Double valorInter;
		
//		no container docker
		String[] cmd = {"/bin/sh", "-c", "cd tmp/pdf; docker cp " + file2.getOriginalFilename() + " " + configServer.dockerContainer};
		
//		Em ambiente localhost
//		String[] cmd = {"/bin/sh", "-c", "cd tmp/pdf; cp " + file2.getOriginalFilename() + " " + configServer.dockerContainer};
		
		if(!intervalo.equals("") && !intervalo.equals("0")) {
			valorInter = Double.valueOf(intervalo) * 60000;
			String[] aux = String.valueOf(valorInter).split("\\.");
			intervalo = aux[0];
		}
		int i = 1;
		Iterator ite = jsonObject.iterator();	
		while (ite.hasNext()) {
			Runtime.getRuntime().exec(cmd);
            JSONObject obj = (JSONObject) ite.next();

            LocalDateTime data = LocalDateTime.now();
            String auxName = session.getMessage();
            String backupmessages = session.getMessage();
            if(auxName.contains("$NOME$")) {
            	session.setMessage(auxName.replace("$NOME$", (CharSequence) obj.get("name")));
            }
            
            Mono<Session> monoSession = this.webClientZ4y
    				.post()    
    				.uri("/sendText?number=" + obj.get("number") + "&text=" + session.getMessage() + "\nConvite enviado: " + data.format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy")) + "&sessionName=" + session.getSessionName())
    				.retrieve()
    				.bodyToMono(Session.class);
    		Session session1 = new Session();
    		
    		session1 = monoSession.block();
    		
    		if(session1.getResult().equals("success")) {
    			
    			Mono<Session> monoSession2 = this.webClientZ4y
        				.post()
        				.uri("/sendFile?number=" + obj.get("number") + "&fileName=" + file2.getOriginalFilename() + "&sessionName=" + session.getSessionName() + "&caption=" + obj.get("number"))
        				.retrieve()
        				.bodyToMono(Session.class);
        		Session session2 = new Session();
        		
        		session2 = monoSession2.block();
        		if(session2.getResult().equals("success")) {
        			System.out.println("MSG -" + i + "- enviada para " + obj.get("number") + " - OK");
        		} else {
        	    	System.out.println("MSG -" + i + "- enviada para " + obj.get("number") + " - FALHOU PQ NAO TEM ZAP!!!");
        	    }
    	    } else {
    	    	System.out.println("MSG -" + i + "- enviada para " + obj.get("number") + " - FALHOU PQ NAO TEM ZAP!!!");
    	    }
    		i++;
    		session.setMessage(backupmessages);
    		if(!intervalo.equals("") && !intervalo.equals("0")) {
    			Thread.sleep(Long.valueOf(intervalo));
    		}
        }

		File arq = new File(path.toString());
		arq.delete();
		
		File arq2 = new File(path2.toString());
		arq2.delete();
	}
}
