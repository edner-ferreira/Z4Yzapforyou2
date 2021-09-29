package br.com.Z4Yzapforyou.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "configuration")
public class ConfigServer {

	@Value("${configuration.path.file}")
	public String pathFile;
	
	@Value("${configuration.path.file.base}")
	public String pathFileBase;
	
	@Value("${configuration.path.file.pdf}")
	public String pathFilePdf;

	@Value("${configuration.path.url}")
	public String pathUrl;
	
	@Value("${configuration.session.name}")
	public String sessionName;
	
	@Value("${configuration.path.dockerContainer}")
	public String dockerContainer;
	
	public String getPathFile() {
		return pathFile;
	}

	public void setPathFile(String pathFile) {
		this.pathFile = pathFile;
	}

	public String getPathUrl() {
		return pathUrl;
	}

	public void setPathUrl(String pathUrl) {
		this.pathUrl = pathUrl;
	}

	public String getPathFileBase() {
		return pathFileBase;
	}

	public void setPathFileBase(String pathFileBase) {
		this.pathFileBase = pathFileBase;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	public String getPathFilePdf() {
		return pathFilePdf;
	}

	public void setPathFilePdf(String pathFilePdf) {
		this.pathFilePdf = pathFilePdf;
	}

	public String getDockerContainer() {
		return dockerContainer;
	}

	public void setDockerContainer(String dockerContainer) {
		this.dockerContainer = dockerContainer;
	}
}
