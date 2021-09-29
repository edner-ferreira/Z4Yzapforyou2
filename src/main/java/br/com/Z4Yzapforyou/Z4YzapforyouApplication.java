package br.com.Z4Yzapforyou;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.Z4Yzapforyou.config.ConfigServer;

@SpringBootApplication
public class Z4YzapforyouApplication {

	@Autowired
	private ConfigServer configServer; 
	
	@Profile("dev")
	@Bean
	public WebClient webClientZ4y(WebClient.Builder builder) {
		return builder
			.baseUrl(configServer.getPathUrl())
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Z4YzapforyouApplication.class, args);
	}

}
