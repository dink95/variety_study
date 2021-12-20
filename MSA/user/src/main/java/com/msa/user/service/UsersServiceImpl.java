package com.msa.user.service;

import com.msa.user.DTO.UserDto;
import com.msa.user.jpa.UserEntity;
import com.msa.user.jpa.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsersServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
         userDto.setUserId(UUID.randomUUID().toString());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = mapper.map(userDto,UserEntity.class);
        userEntity.setEnPwd("enrypted");

        userRepository.save(userEntity);

        UserDto userDto1 = mapper.map(userEntity,UserDto.class);
        return userDto1;
    }
}
