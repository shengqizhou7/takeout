package com.sailfinn.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailfinn.reggie.entity.AddressBook;
import com.sailfinn.reggie.mapper.AddressBookMapper;
import com.sailfinn.reggie.service.AddressBookService;
import com.sailfinn.reggie.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
