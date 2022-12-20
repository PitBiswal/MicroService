package com.ms.user.pit.users.model;

public class JWTRefreshResponseModel {
	private String auth_type;
	private String expired_access_token;
	private long expires_in;
	private String refresh_token;
	private String validToken;
	private String jwtValidToken;
	
	
	public String getExpired_access_token() {
		return expired_access_token;
	}
	public void setExpired_access_token(String expired_access_token) {
		this.expired_access_token = expired_access_token;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public long getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(long expires_in) {
		this.expires_in = expires_in;
	}
	public String getAuth_type() {
		return auth_type;
	}
	public void setAuth_type(String auth_type) {
		this.auth_type = auth_type;
	}
	public String getValidToken() {
		return validToken;
	}
	public void setValidToken(String validToken) {
		this.validToken = validToken;
	}
	public String getJwtValidToken() {
		return jwtValidToken;
	}
	public void setJwtValidToken(String jwtValidToken) {
		this.jwtValidToken = jwtValidToken;
	}

	
}
