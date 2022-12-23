package com.ms.user.pit.util;

import java.util.Date;

import org.springframework.core.env.Environment;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTTokenCreation {
	
	public static String createToken(String userId, Environment env,boolean isRefreshToken) {
		String token=null;
		if(isRefreshToken) {
			token = Jwts.builder()
		               .setSubject(userId)
		               .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("token.refresh_expiration_time"))))
		               .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret") )
		               .compact();
			return token;
		}else {
		  token = Jwts.builder()
                 .setSubject(userId)
                 .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("token.expiration_time"))))
                 .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret") )
                 .compact();
		
		return token;
		}
	}

}
