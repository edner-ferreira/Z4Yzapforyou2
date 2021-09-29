package br.com.Z4Yzapforyou.model;

public class Session {
	
	private String name;
	private String message;
	private String number;
	private String sessionName;
	private String qrcode;
	private String client;
    private String status;
    private String state;
    private String file;
    private String result;
    
    public Session() {
	}
    
    public Session(String sessionName) {
		this.sessionName = sessionName;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	public String getQrcode() {
		return qrcode;
	}
	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override	
	public String toString() {
		return "Session [name=" + name + ", message=" + message + ", number=" + number + ", sessionName=" + sessionName
				+ ", qrcode=" + qrcode + ", client=" + client + ", status=" + status + ", state=" + state + ", file="
				+ file + ", result=" + result + "]";
	}
}
