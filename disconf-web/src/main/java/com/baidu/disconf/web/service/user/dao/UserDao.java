package com.baidu.disconf.web.service.user.dao;

import com.baidu.disconf.web.service.user.model.UserBO;
import com.baidu.unbiz.common.genericdao.dao.BaseDao;

/**
 * @author liaoqiqi
 * @version 2013-11-28
 */
public interface UserDao extends BaseDao<Long, UserBO> {

    void executeSql(String sql);

    UserBO getUserByName(String name);

}
