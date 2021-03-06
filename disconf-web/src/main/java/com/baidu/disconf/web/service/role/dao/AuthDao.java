package com.baidu.disconf.web.service.role.dao;

import com.baidu.disconf.web.service.role.bo.AuthBO;
import com.baidu.unbiz.common.genericdao.dao.BaseDao;

/**
 * Created by Kail on 2017/6/15.
 */
public interface AuthDao extends BaseDao<Integer, AuthBO> {

    String SQL_DELETE_BY_ROLE_ID = "DELETE FROM `auth` WHERE role_id= ? ";

}
