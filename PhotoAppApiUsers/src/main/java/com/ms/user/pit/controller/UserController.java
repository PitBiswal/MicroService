package com.ms.user.pit.controller;


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
import com.ms.user.pit.users.model.LoginRequestModel;
import com.ms.user.pit.users.model.LoginUserResponseModel;
import com.ms.user.pit.users.model.UserDetailsResponseModel;
import com.ms.user.pit.users.model.UserRequestDetailsModel;
import com.ms.user.pit.util.JWTTokenCreation;
import com.ms.user.pit.shared.LoginDto;
import org.springframework.context.annotation.Bean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
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
	
	
	@Bean
	public LoginUserResponseModel getLoginUserResponseModelBean() {
	    return new LoginUserResponseModel();
	}
	

	/*
	 * Method To Check User Status  
	 * Input:Need to provide values in body Section 
	 * Need to Provide a valid JWT token to know the current status of User
	 */
	
	@GetMapping("/port/check")
	public String status() {
		return "Users Web Service (PhotoAppApiUsers)working on port ::"+env.getProperty("local.server.port");
	}
	
	
	
	/*
	 * Method To Create New User  
	 * Input:Need to provide values in body Section 
	 * Support both XML and JSON Type
	 */
	
	@PostMapping(path = "/createUser", 
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
			)
	public ResponseEntity<CreateUserResponseModel> createUser(@RequestBody CreateUserRequestModel userDetails)
	{
		CreateUserResponseModel returnValue=null;
		UserDto createdUser=null;
		ModelMapper modelMapper = new ModelMapper(); 
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		try {
			
			int count=usersService.countUser(userDto.getEmail());
			
			if(count==0) {
				    createdUser = usersService.createUser(userDto);
				    returnValue = modelMapper.map(createdUser, CreateUserResponseModel.class);
				    returnValue.setUserStaus("New User Created");
			}else {
				   returnValue=new CreateUserResponseModel();
				   returnValue=new CreateUserResponseModel();
		    	   returnValue.setEmail(userDto.getEmail());
		    	   returnValue.setFirstName(userDto.getFirstName());
		    	   returnValue.setLastName(userDto.getLastName());
		    	   returnValue.setUserStaus("User Acount Is Already Exists ");	
			      }
		
          }catch(Exception ex) {
        	  
		    	   returnValue=new CreateUserResponseModel();
		    	   returnValue.setEmail(userDto.getEmail());
		    	   returnValue.setFirstName(userDto.getFirstName());
		    	   returnValue.setLastName(userDto.getLastName());
		    	   returnValue.setUserStaus(ex.getMessage());
		    	   return  ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(returnValue);
	}
		
		return  ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
	}
	
	/*
	 * Method To Refresh Expired Token 
	 * Input:Expired JWT Token 
	 * Support both XML and JSON Type
	 */
	@PostMapping(path = "/refresh/token", 
	        consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
	        produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<JWTRefreshResponseModel> refreshToken() {
		JWTRefreshResponseModel returnValue=new JWTRefreshResponseModel();
		boolean isRefreshToken=false;
		String tokenHeader=request.getHeader(HttpHeaders.AUTHORIZATION);
		String jwttoken = tokenHeader.replace("Bearer", "");
		Claims claims=null;
		try {
		 claims = Jwts.parser().setSigningKey(env.getProperty("token.secret")).parseClaimsJws(jwttoken).getBody();
		}catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
			
			 return  null;
		}
		catch (ExpiredJwtException ex) {
			
				 isRefreshToken=true;
				 claims=ex.getClaims();
				 String refreshToken=JWTTokenCreation.createToken(claims.getSubject(),env,isRefreshToken);
			
				 returnValue.setAuth_type("Bearer");
				 returnValue.setExpired_access_token(jwttoken);
				 returnValue.setExpires_in(Long.parseLong(env.getProperty("token.refresh_expiration_time")));
				 returnValue.setRefresh_token(refreshToken);
				 
			 return  ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
		}
		
		 return  null;
		
		
		
	}
	
	
	/*
	 * Method To Authenticate User   
	 * Input:Need to Provide password and mail id in the body section
	 * After Successfully Checks creates a JWT token 
	 */
	@PostMapping(path = "/authenticateUser", 
    consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
    produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	
	public ResponseEntity<LoginUserResponseModel> authenticateUser(@RequestBody LoginRequestModel loginDetails) {
		
		ModelMapper modelMapper = new ModelMapper(); 
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		boolean flag=false;
		String token=null;
		String account=null;
		LoginDto loginDto = modelMapper.map(loginDetails, LoginDto.class);
		
		UserDto userDetails = usersService.getUserDetailsByEmailCustom(loginDto.getEmail(),loginDto.getPassword());
		 if(userDetails!=null) {
			 flag=true;
			 account=env.getProperty("user.found");
		 }else {
			 account=env.getProperty("user.not.found");
		 }
		 
		if(flag) {
			
			token=JWTTokenCreation.createToken(userDetails.getUserId(),env,false);
		 }
		LoginUserResponseModel loginUserResponseModel=getLoginUserResponseModelBean();
		loginUserResponseModel.setEmail(loginDto.getEmail());
		loginUserResponseModel.setAuthenticatedUser(flag);
		loginUserResponseModel.setAccount(account);
		loginUserResponseModel.setToken(token);
		 
		return  ResponseEntity.status(HttpStatus.CREATED).body(loginUserResponseModel);
	}
	
	/*
	 * Method To get User Details  
	 * Input:Need to provide email in body Section 
	 * Support both XML and JSON Type
	 * Require JWT Token
	 */
	
	@PostMapping(path = "/userDetails", 
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
			)
	public ResponseEntity<UserDetailsResponseModel> getUserDetails(@RequestBody UserRequestDetailsModel userDetails)
	{
		UserDetailsResponseModel returnValue=null;
		UserDto userDetailsInfo=null;
		ModelMapper modelMapper = new ModelMapper(); 
		try {
			
			userDetailsInfo =usersService.getUserDetails(userDetails.getEmail());
		    returnValue = modelMapper.map(userDetailsInfo, UserDetailsResponseModel.class);
          }
		
	      catch(Exception ex) {
        	  
	     }
		
		return  ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
	}


}
