package com.baidu.disconf.web.service.user.service;

import com.baidu.disconf.web.service.user.dao.UserDao;
import com.baidu.disconf.web.service.user.model.UserBO;
import com.baidu.disconf.web.service.user.model.VisitorDTO;
import com.baidu.disconf.web.service.user.util.SignUtils;
import com.baidu.ub.common.commons.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author liaoqiqi
 * @version 2013-12-5
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserService {

    protected static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    public VisitorDTO getCurVisitor() {
        return ThreadContext.getSessionVisitor();
    }

    /**
     * 创建
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public Long create(UserBO userBO) {

        userBO = userDao.create(userBO);
        return userBO.getId();
    }

    /**
     *
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public void create(List<UserBO> userBOS) {

        userDao.create(userBOS);
    }


    public List<UserBO> getAll() {

        return userDao.findAll();
    }

    /**
     * @param userId
     */

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public String addOneAppForUser(Long userId, int appId) {

        UserBO userBO = getUser(userId);
//        String ownAppIds = userBO.getOwnApps();
//        if (ownAppIds.contains(",")) {
//            ownAppIds = ownAppIds + "," + appId;
//
//        } else {
//            ownAppIds = String.valueOf(appId);
//        }
//        userBO.setOwnApps(ownAppIds);
        userDao.update(userBO);

//        return ownAppIds;
        return null;
    }

    /**
     * @param newPassword
     */

    public void modifyPassword(Long userId, String newPassword) {

        String passwordWithSalt = SignUtils.createPassword(newPassword);

        UserBO userBO = userDao.get(userId);
        userBO.setPassword(passwordWithSalt);

        userDao.update(userBO);
    }

    /**
     * @param userId
     * @return
     */

    public UserBO getUser(Long userId) {

        return userDao.get(userId);
    }

}
