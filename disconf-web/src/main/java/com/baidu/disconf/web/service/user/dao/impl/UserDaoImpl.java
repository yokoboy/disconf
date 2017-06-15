package com.baidu.disconf.web.service.user.dao.impl;

import com.baidu.disconf.web.service.user.model.UserBO;
import com.baidu.disconf.web.service.user.dao.UserDao;
import com.baidu.dsp.common.dao.AbstractDao;
import com.baidu.dsp.common.dao.Columns;
import org.springframework.stereotype.Repository;

/**
 * @author liaoqiqi
 * @version 2013-11-28
 */
@Repository
public class UserDaoImpl extends AbstractDao<Long, UserBO> implements UserDao {

    /**
     * 执行SQL
     */
    public void executeSql(String sql) {
        executeSQL(sql, null);
    }

    /**
     */
    @Override
    public UserBO getUserByName(String name) {
        return findOne(match(Columns.NAME, name));
    }

}