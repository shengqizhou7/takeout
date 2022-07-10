package com.sailfinn.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sailfinn.reggie.common.R;
import com.sailfinn.reggie.entity.User;
import com.sailfinn.reggie.service.UserService;
import com.sailfinn.reggie.utils.SMSUtils;
import com.sailfinn.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * send message verification code
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> senMsg(@RequestBody User user, HttpSession session){
        //get phone number
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            //generate 4 digits code
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code = {}", code);

            //call ali cloud API to send message
            //SMSUtils.sendMessage("瑞吉外卖","", phone, code);

            //save verification code in session
            session.setAttribute(phone, code);

            return R.success("Verification code sent!");
        }

        return R.error("Message not sent!");
    }

    /**
     * user login on mobile end
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        //get phone from map
        String phone = map.get("phone").toString();

        //get code from map
        String code = map.get("code").toString();

        //get saved verification code from session
        Object codeInSession = session.getAttribute(phone);

        //compare codes (saved in session == submitted in page)
        if(codeInSession != null && codeInSession.equals(code)){
            //if codes are same, login success
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(queryWrapper);
            if(user == null){
                //check if new user by phone. if so, auto-register
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("Log in failed!");
    }

    /**
     * user logout
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("Logout Success!");
    }

}
