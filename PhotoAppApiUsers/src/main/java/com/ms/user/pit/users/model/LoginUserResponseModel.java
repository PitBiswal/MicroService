package com.ms.user.pit.users.model;

public class LoginUserResponseModel {

		private String email;
		private boolean authenticatedUser;
		private String token;
		private String account;


		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public boolean isAuthenticatedUser() {
			return authenticatedUser;
		}

		public void setAuthenticatedUser(boolean authenticatedUser) {
			this.authenticatedUser = authenticatedUser;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getAccount() {
			return account;
		}

		public void setAccount(String account) {
			this.account = account;
		}
		

		

	}
