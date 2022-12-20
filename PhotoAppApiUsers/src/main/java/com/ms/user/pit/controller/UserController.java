package com.ms.user.pit.controller;


import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.net.HttpHeaders;
import com.ms.user.pit.service.UsersService;
import com.ms.user.pit.shared.UserDto;
import com.ms.user.pit.users.model.CreateUserRequestModel;
import com.ms.user.pit.users.model.CreateUserResponseModel;
import com.ms.user.pit.users.model.JWTRefreshResponseModel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@RestController
@RequestMapping("/users")
public class UserController {
	@Autowired
	HttpServletRequest request;
	@Autowired
	private Environment env;
	@Autowired
	UsersService usersService;
	
	@GetMapping("/status/check")
	public String status() {
		return "Users Web Service (PhotoAppApiUsers)working on port ::"+env.getProperty("local.server.port");
	}
	
	@PostMapping(
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
			)
	public ResponseEntity<CreateUserResponseModel> createUser(@RequestBody CreateUserRequestModel userDetails)
	{
		ModelMapper modelMapper = new ModelMapper(); 
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		UserDto createdUser = usersService.createUser(userDto);
		
		CreateUserResponseModel returnValue = modelMapper.map(createdUser, CreateUserResponseModel.class);
		
		return  ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
	}

	@PostMapping(path = "/refresh/token", 
	        consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
	        produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<JWTRefreshResponseModel> refreshToken() {
		JWTRefreshResponseModel returnValue=new JWTRefreshResponseModel();
		
		String tokenHeader=request.getHeader(HttpHeaders.AUTHORIZATION);
		String jwttoken = tokenHeader.replace("Bearer", "");
		Claims claims=null;
		try {
		 claims = Jwts.parser().setSigningKey(env.getProperty("token.secret")).parseClaimsJws(jwttoken).getBody();
		}catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
			
			 returnValue.setValidToken("Not A Valid Token");
			 return  ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
		}
		catch (ExpiredJwtException ex) {
			claims=ex.getClaims();
			 String refreshToken = Jwts.builder()
		               .setSubject(claims.getSubject())
		               .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("token.refresh_expiration_time"))))
		               .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret") )
		               .compact();
			 returnValue.setAuth_type("Bearer");
			 returnValue.setExpired_access_token(jwttoken);
			 returnValue.setExpires_in(Long.parseLong(env.getProperty("token.refresh_expiration_time")));
			 returnValue.setRefresh_token(refreshToken);
			 return  ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
		}
		 returnValue.setAuth_type("Bearer");
		 returnValue.setJwtValidToken(jwttoken);
		 returnValue.setValidToken("Token Has Not Expired");
		 return  ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
		
		
		
	}
	

}
