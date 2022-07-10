package com.sailfinn.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailfinn.reggie.entity.User;
import com.sailfinn.reggie.mapper.UserMapper;
import com.sailfinn.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService{
}
