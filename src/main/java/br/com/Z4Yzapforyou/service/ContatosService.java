package br.com.Z4Yzapforyou.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.Z4Yzapforyou.config.ConfigServer;
import br.com.Z4Yzapforyou.model.Contatos;

@Service
public class ContatosService {
	
	@Autowired
	private ConfigServer configServer;
	
//	private static String pathTemp = "/home/richard/Documents/workspace-spring-tool-suite-4-4.10.0.RELEASE/Z4Yzapforyou/tmp/";
//	private static String pathDiretorioBase = "/home/richard/Documents/workspace-spring-tool-suite-4-4.10.0.RELEASE/Z4Yzapforyou/tmp/base/";
	private static String pathArqComp;
	private static String pathArqCompJson;

	
	public void initContatos(MultipartFile fileNome, MultipartFile fileTel, String diretorios) throws Exception {
		File diretorio = new File(configServer.pathFileBase + diretorios);
		
		if(!diretorio.exists()) {
			diretorio.mkdirs();
		}
		
		if(diretorio.exists()) {
			if(!fileTel.isEmpty()) {
				//Arruma entrada de dados
				if(!fileNome.isEmpty()) {
					pathArqComp = configServer.pathFileBase + diretorios + "/NomesTelefones";
					pathArqCompJson = configServer.pathFileBase + diretorios + "/NomesTelefones";
					escritorLinkedComNome(fileNome, fileTel);
				}else {
					pathArqComp = configServer.pathFileBase + diretorios + "/NomesTelefones";
					pathArqCompJson = configServer.pathFileBase + diretorios + "/NomesTelefones";
					escritorArraySemNome(fileTel);
				}
			}
		}
	}
	
	public static ArrayList<String> leitorArraySemNome(String path) throws IOException {
		BufferedReader buffRead = new BufferedReader(new FileReader(path));
		ArrayList<String> linhas = new ArrayList<String>();
		String linha;	
		
		while((linha = buffRead.readLine()) != null) {
			linhas.add(linha);
//			System.out.println(linha);
		}
		Set<String> set = new HashSet<>(linhas);
		
		linhas.clear();
		linhas.addAll(set);
		buffRead.close();
		return linhas;
	}
	
	public void escritorArraySemNome(MultipartFile fileTel) throws IOException {
		Path pathTel = Paths.get(configServer.pathFile, fileTel.getOriginalFilename());
		fileTel.transferTo(pathTel);
		
		BufferedWriter buffWrite1 = new BufferedWriter(new FileWriter(pathArqComp + ".vcf"));
		BufferedWriter buffWrite2 = new BufferedWriter(new FileWriter(pathArqCompJson + ".json"));
		JSONObject jsonObject = new JSONObject();

		ArrayList<String> telefones1 = new ArrayList<String>();
		ArrayList<Contatos> contatos1 = new ArrayList<Contatos>();
		
		telefones1.addAll(leitorArraySemNome(pathTel.toString()));
		Iterator<String> it2 = telefones1.iterator();
		
		while(it2.hasNext()) {
			Contatos contato = new Contatos();
			contato.setTelefone(it2.next());
			contatos1.add(contato);
		}
		int i = 1;
		int j = 1;
		int k = 1;
		buffWrite2.append("[\n");
		String auxvcf ="";
		String auxjson = "";
		
		for (Contatos cont : contatos1) {
			if(!cont.getTelefone().equals("")) {
				System.out.println("Numero: " + k + "Cel: " + cont.getTelefone());
				//Arquivo vcf
				buffWrite1.append("BEGIN:VCARD\n");
				buffWrite1.append("VERSION:2.1\n");
				buffWrite1.append("N:;" + k + ";;;\n");
				buffWrite1.append("FN:" + k + "\n");
				buffWrite1.append("TEL;CELL:" + cont.getTelefone() + "\n");
				buffWrite1.append("END:VCARD\n");
				//Arquivo json
				jsonObject.put("number", "55" + cont.getTelefone());
				jsonObject.put("name", k);
				buffWrite2.append(jsonObject.toJSONString() + (cont.getTelefone().equals(contatos1.get(contatos1.size() -1).getTelefone()) ? "" : ",\n"));
//				(i >= 50 || cont.getTelefone().equals(contatos1.get(contatos1.size() -1).getTelefone()) ? "" : ",\n"));
				auxvcf +="BEGIN:VCARD\n" 
				+ "VERSION:2.1\n" 
				+ "N:;" + k + ";;;\n"
				+ "FN:" + k + "\n"
				+ "TEL;CELL:" + cont.getTelefone() + "\n"
				+ "END:VCARD\n";
				
				auxjson += jsonObject.toJSONString() + (i >= 50 || cont.getTelefone().equals(contatos1.get(contatos1.size() -1).getTelefone()) ? "" : ",");

				if(i >= 50 || cont.getTelefone().equals(contatos1.get(contatos1.size() -1).getTelefone())) {
					BufferedWriter buffWriteComp1 = new BufferedWriter(new FileWriter(pathArqComp + j + ".vcf"));
					BufferedWriter buffWriteCompjson2 = new BufferedWriter(new FileWriter(pathArqCompJson + j + ".json"));
					buffWriteComp1.append(auxvcf.toString());
					buffWriteCompjson2.append("[" + auxjson.toString() + "]");
					buffWriteComp1.close();
					buffWriteCompjson2.close();
					j++;
					i=0;
					auxvcf = "";
					auxjson = "";
				}
				i++;
				k++;
			}
		}
		buffWrite2.append("]");
		buffWrite1.close();
		buffWrite2.close();
		File arq1 = new File(pathTel.toString());
		arq1.delete();
	}
	
	public static LinkedList<String> leitorLinkedComNome(String path) throws IOException {
		BufferedReader buffRead = new BufferedReader(new FileReader(path));
		LinkedList<String> linhas = new LinkedList<String>();
		String linha;
		
		while((linha = buffRead.readLine()) != null) {
			linhas.add(linha);
//			System.out.println(linha);
		}
		buffRead.close();
		return linhas;
	}
	
	public void escritorLinkedComNome(MultipartFile fileNome, MultipartFile fileTel) throws IOException {
		Path pathName = Paths.get(configServer.pathFile, fileNome.getOriginalFilename());
		fileNome.transferTo(pathName);
		
		Path pathTel = Paths.get(configServer.pathFile, fileTel.getOriginalFilename());
		fileTel.transferTo(pathTel);
		
		BufferedWriter buffWrite1 = new BufferedWriter(new FileWriter(pathArqComp + ".vcf"));
		BufferedWriter buffWrite2 = new BufferedWriter(new FileWriter(pathArqCompJson + ".json"));
		JSONObject jsonObject = new JSONObject();

		
		LinkedList<String> nomes1 = new LinkedList<String>();
		LinkedList<String> telefones1 = new LinkedList<String>();
		LinkedList<Contatos> contatos1 = new LinkedList<Contatos>();
	
		nomes1.addAll(leitorLinkedComNome(pathName.toString()));
		telefones1.addAll(leitorLinkedComNome(pathTel.toString()));

		Iterator<String> it1 = nomes1.iterator();
		Iterator<String> it2 = telefones1.iterator();
		
		
		
		while(it2.hasNext()) {
//			System.out.println(it2.next() + " - " + it1.next());
			Contatos contato = new Contatos();
			contato.setNome(it1.next());
			contato.setTelefone(it2.next());
			contatos1.add(contato);
		}
		int i = 1;
		int j = 1;
		int k = 1;
		buffWrite2.append("[\n");
		String auxvcf ="";
		String auxjson = "";
		
		for (Contatos cont : contatos1) {
			
			if(!cont.getTelefone().equals("")) {
//				System.out.println("Numero: " + k);
				//Arquivo vcf
				buffWrite1.append("BEGIN:VCARD\n");
				buffWrite1.append("VERSION:2.1\n");
				buffWrite1.append("N:;" + cont.getNome() + ";;;\n");
				buffWrite1.append("FN:" + cont.getNome() + "\n");
				buffWrite1.append("TEL;CELL:" + cont.getTelefone() + "\n");
				buffWrite1.append("END:VCARD\n");
				//Arquivo json
				jsonObject.put("number", "55" + cont.getTelefone());
				jsonObject.put("name", cont.getNome());
				buffWrite2.append(jsonObject.toJSONString() + (i >= 50 || cont.getTelefone().equals(contatos1.get(contatos1.size() -1).getTelefone()) ? "" : ",\n"));
				
				auxvcf +="BEGIN:VCARD\n" 
				+ "VERSION:2.1\n" 
				+ "N:;" + cont.getNome() + ";;;\n"
				+ "FN:" + cont.getNome() + "\n"
				+ "TEL;CELL:" + cont.getTelefone() + "\n"
				+ "END:VCARD\n";
				
				auxjson += jsonObject.toJSONString() + (i >= 50 || cont.getTelefone().equals(contatos1.get(contatos1.size() -1).getTelefone()) ? "" : ",");
				
				if(i >= 50 || cont.equals(contatos1.getLast())) {
					BufferedWriter buffWriteComp1 = new BufferedWriter(new FileWriter(pathArqComp + j + ".vcf"));
					BufferedWriter buffWriteCompjson2 = new BufferedWriter(new FileWriter(pathArqCompJson + j + ".json"));
					buffWriteComp1.append(auxvcf.toString());
					buffWriteCompjson2.append("[" + auxjson.toString() + "]");
					buffWriteComp1.close();
					buffWriteCompjson2.close();
					j++;
					i=0;
					auxvcf = "";
					auxjson = "";
	//				jsonObject.clear();
				}
				i++;
				k++;
			}
		}
		buffWrite2.append("]");
		buffWrite1.close();
		buffWrite2.close();
		File arq1 = new File(pathTel.toString());
		arq1.delete();
		File arq2 = new File(pathName.toString());
		arq2.delete();
	}
}
