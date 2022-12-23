package com.ms.user.pit.service;

import java.util.ArrayList;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.ms.user.pit.data.UserEntity;
import com.ms.user.pit.data.UsersRepository;
import com.ms.user.pit.shared.UserDto;



@Service
public class UsersServiceImpl implements UsersService {
	
	
	UsersRepository usersRepository;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public UsersServiceImpl(UsersRepository usersRepository, 
			BCryptPasswordEncoder bCryptPasswordEncoder
			)
	{
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		
	}
 
	
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@Override
	public int countUser(String email)  {
		int count=0;
		UserEntity userEntity= usersRepository.findByEmail(email);
		if(userEntity!=null)
			count=1;
		else
			count=0;
 
		return count;
	}
	
	@Override
	public UserDto createUser(UserDto userDetails)  {
		
		userDetails.setUserId(UUID.randomUUID().toString());
		userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
		
		ModelMapper modelMapper = new ModelMapper(); 
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);

		usersRepository.save(userEntity);
		
		UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
 
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = usersRepository.findByEmail(username);
		
		if(userEntity == null) throw new UsernameNotFoundException(username);	
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) { 
		UserEntity userEntity = usersRepository.findByEmail(email);
		
		if(userEntity == null) throw new UsernameNotFoundException(email);
		
		
		return new ModelMapper().map(userEntity, UserDto.class);
	}

	@Override
	public UserDto getUserDetailsByEmailCustom(String email, String pwd) {
		// TODO Auto-generated method stub
		UserEntity userEntity = usersRepository.findByEmail(email);
		if(userEntity == null) {
			return null;
		}
		if(bCryptPasswordEncoder.matches(pwd,userEntity.getEncryptedPassword())) {
		
		return new ModelMapper().map(userEntity, UserDto.class);
		
		}else{
			return null;
		}
	}
	
	@Override
	public UserDto getUserDetails(String email) {
		// TODO Auto-generated method stub
		UserEntity userEntity = usersRepository.findByEmail(email);
		if(userEntity == null) {
			return null;
		}
		
		return new ModelMapper().map(userEntity, UserDto.class);
	}
	}
