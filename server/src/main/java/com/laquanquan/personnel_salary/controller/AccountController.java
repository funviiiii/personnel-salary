package com.laquanquan.personnel_salary.controller;

import com.laquanquan.personnel_salary.service.AccountService;
import com.laquanquan.personnel_salary.utils.WebResponseBody;
import com.laquanquan.personnel_salary.vo.SignUpVO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.SQLDataException;

/**
 * @author lqq
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Resource
    private AccountService accountService;

    /**
     * 根据id查询一个用户
     *
     * @param uid uid
     * @return 返回一个uid，若没有查询到则返回空
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/uid/{uid}")
    public String getAccountByUid(@PathVariable String uid) {
        return accountService.getAccountById(uid);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/username/{username}")
    public String getAccountByUsername(@PathVariable String username) {
        return accountService.getAccountByUsername(username);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/email/{email}")
    public String getAccountByEmail(@PathVariable String email) {
        return accountService.getAccountByEmail(email);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/phone/{phone}")
    public String getAccountByPhone(@PathVariable String phone) {
        return accountService.getAccountByPhone(phone);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponseBody<String> signUp(@RequestBody SignUpVO signUpVO) throws SQLDataException {
        accountService.signUp(signUpVO.getAccount(), signUpVO.getUser());
        return new WebResponseBody<>("注册成功");
    }
}