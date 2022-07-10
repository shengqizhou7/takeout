package com.sailfinn.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailfinn.reggie.common.R;
import com.sailfinn.reggie.entity.Employee;
import com.sailfinn.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * employee login
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //md5 encryption
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //find username in SQL
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //check username
        if(emp == null){
            return R.error("Login Failed: wrong username");
        }

        //check password
        if(!emp.getPassword().equals(password)){
            return R.error("Login Failed: wrong password");
        }

        //check employee status
        if(emp.getStatus() == 0){
            return R.error("Login Failed: account frozen");
        }

        //login success
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * employee logout
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("Logout Success!");
    }

    /**
     * add new employee
     * @param employee
     * @return
     */

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("Add new employee. Employee info: {}", employee.toString());

        //set default md5 encrypted password 123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        //get current user's ID
//        Long empID = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empID);
//        employee.setUpdateUser(empID);

        employeeService.save(employee);

        return R.success("New employee added!!");
    }

    /**
     * multiple pages for employee info, 员工信息分页查询
     * based on Mybatis Plus plugin
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件/filter
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件/sort
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }


    /**
     * revise employee info by ID
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());

        //ThreadLocal. 每一个http请求都会被分配一个新的thread，ThreadLocal为每个线程提供单独的存储空间
        long id = Thread.currentThread().getId();
        log.info("Thread id is: {}", id);
//        Long empID = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empID);
        employeeService.updateById(employee);

        return R.success("Employee info updated!");
    }

    /**
     * Search employee info by ID
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("Search employee info by ID");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("No such employee found");
    }
}
