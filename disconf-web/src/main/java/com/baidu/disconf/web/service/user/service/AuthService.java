package com.baidu.disconf.web.service.user.service;

import com.baidu.disconf.web.service.role.dao.impl.AuthDaoImpl;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Kail on 2017/6/15.
 */
@Service
public class AuthService {

    @Autowired
    private AuthDaoImpl authDao;

    public List<String> selectAuthListByRoleIds(List<Integer> roleIds) {
        return authDao.findBySQL("select auth_info from auth where role_id in (?)", roleIds, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("auth_info");
            }
        });
    }

    public static Boolean hasAuth(List<String> auths, String auth) {
        for (String cur : auths) {
            if ((cur + "-").startsWith(auth + "-")) {
                return true;
            }
        }
        return false;
    }

}
