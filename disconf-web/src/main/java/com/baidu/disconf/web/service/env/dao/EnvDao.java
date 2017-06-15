package com.baidu.disconf.web.service.env.dao;

import com.baidu.disconf.web.service.env.model.EnvBO;
import com.baidu.unbiz.common.genericdao.dao.BaseDao;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
public interface EnvDao extends BaseDao<Long, EnvBO> {

    /**
     * @param name
     *
     * @return
     */
    EnvBO getByName(String name);
}
