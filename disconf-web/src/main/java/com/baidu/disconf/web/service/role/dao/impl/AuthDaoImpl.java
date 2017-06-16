package com.baidu.disconf.web.service.role.dao.impl;

import com.baidu.disconf.web.service.role.bo.AuthBO;
import com.baidu.disconf.web.service.role.dao.AuthDao;
import com.baidu.dsp.common.dao.AbstractDao;
import org.springframework.stereotype.Repository;

import java.util.Arrays;

/**
 * Created by Kail on 2017/6/15.
 */
@Repository
public class AuthDaoImpl extends AbstractDao<Integer, AuthBO> implements AuthDao {

    public void executeSql(String sql, Object... params) {
        executeSQL(sql, Arrays.asList(params));
    }

}
