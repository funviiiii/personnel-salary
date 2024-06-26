package com.laquanquan.personnel_salary.service.impl;

import com.laquanquan.personnel_salary.domain.Role;
import com.laquanquan.personnel_salary.domain.Salary;
import com.laquanquan.personnel_salary.domain.User;
import com.laquanquan.personnel_salary.exception.DataNotFoundException;
import com.laquanquan.personnel_salary.mapper.RoleMapper;
import com.laquanquan.personnel_salary.mapper.SalaryMapper;
import com.laquanquan.personnel_salary.mapper.UserMapper;
import com.laquanquan.personnel_salary.service.SalaryService;
import com.laquanquan.personnel_salary.utils.TokenBuilder;
import com.laquanquan.personnel_salary.utils.WebResponseBody;
import com.laquanquan.personnel_salary.vo.SalaryVO;
import com.laquanquan.personnel_salary.vo.UserDataVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * @author lqq
 */
@Service
public class SalaryServiceImpl implements SalaryService {
    @Resource
    private SalaryMapper salaryMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Override
    public WebResponseBody<List<SalaryVO>> getList(String token) throws AccessDeniedException {
        // 获取uid
        String uid = (String) TokenBuilder.parse(token).get("uid");
        User user = userMapper.selectByUid(uid);
        if (user == null) {
            throw new DataNotFoundException("token无效，不允许访问");
        }

        List<UserDataVO> users;

        List<SalaryVO> salaryList;

        Role role = roleMapper.selectByRid(user.getRole());
        if (role.getRid().equals("rid_super")) {
            // 超级管理员
            users = userMapper.selectAll();
            salaryList = salaryMapper.selectAll();
        } else if (role.getSalaryRight()) {
            // 普通管理员
            users = userMapper.selectByDepartment(user.getDepartment());

            salaryList = salaryMapper.selectListByDepartment(users);

        } else {
            throw new AccessDeniedException("该用户权限不足，无法获取工资列表");
        }

        // 给工资列表的名字赋值
        salaryList.forEach(salary -> {
            users.forEach(userVO -> {
                if (userVO.getUid().equals(salary.getUid())) {
                    salary.setName(userVO.getName());
                }
            });
        });
        return new WebResponseBody<>("获取用户工资列表成功", salaryList);
    }

    @Override
    public WebResponseBody<SalaryVO> getOne(String token) {
        // 获取uid
        String uid = (String) TokenBuilder.parse(token).get("uid");
        User user = userMapper.selectByUid(uid);
        if (user == null) {
            throw new DataNotFoundException("token无效，不允许访问");
        }

        // 获取自己的工资
        SalaryVO salary = salaryMapper.selectOneByUid(user.getUid());
        salary.setName(user.getName());
        return new WebResponseBody<>("获取工资成功", salary);
    }

    @Override
    public void updateSalary(SalaryVO salary) throws AccessDeniedException {
        User user = userMapper.selectByUid(salary.getUid());
        if (salary.getUid() == null || user == null) {
            throw new DataNotFoundException("uid不存在，更新工资数据失败");
        }

        // 对应付款和实际工资进行计算
        salary.setShouldPay(String.format("%.2f", new BigDecimal(salary.getBasicSalary()).add(
                new BigDecimal(salary.getAllowance()).add(
                        new BigDecimal(salary.getReward())))));
        salary.setRealSalary(String.format("%.2f", new BigDecimal(salary.getShouldPay()).add(
                new BigDecimal("-" + salary.getCost())).add(new BigDecimal("-" + salary.getInsurance()))));

        salaryMapper.updateOne(salary);
    }
}
