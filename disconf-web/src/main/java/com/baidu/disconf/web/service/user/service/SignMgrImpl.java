package com.baidu.disconf.web.service.user.service;

import com.baidu.disconf.web.service.user.model.UserBO;
import com.baidu.disconf.web.service.user.util.SignUtils;
import com.baidu.disconf.web.service.user.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 与登录登出相关的
 *
 * @author liaoqiqi
 * @version 2014-2-6
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SignMgrImpl {

    protected static final Logger LOG = LoggerFactory.getLogger(SignMgrImpl.class);

    @Autowired
    private UserDao userDao;

    /**
     * 根据 用户名获取用户
     */
    public UserBO getUserByName(String name) {

        return userDao.getUserByName(name);
    }

    /**
     * 验证密码是否正确
     *
     * @param userPassword
     * @param passwordToBeValidate
     * @return
     */
    public boolean validate(String userPassword, String passwordToBeValidate) {

        String data = SignUtils.createPassword(passwordToBeValidate);

        if (data.equals(userPassword)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 登录
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public UserBO signin(String username) {
        UserBO userBO = userDao.getUserByName(username);
        userDao.update(userBO);
        return userBO;
    }

}
