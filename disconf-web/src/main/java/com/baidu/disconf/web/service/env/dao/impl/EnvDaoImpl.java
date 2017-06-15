package com.baidu.disconf.web.service.env.dao.impl;

import com.baidu.disconf.web.service.env.model.EnvBO;
import org.springframework.stereotype.Service;

import com.baidu.disconf.web.service.env.dao.EnvDao;
import com.baidu.dsp.common.dao.AbstractDao;
import com.baidu.dsp.common.dao.Columns;
import com.baidu.unbiz.common.genericdao.operator.Match;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@Service
public class EnvDaoImpl extends AbstractDao<Long, EnvBO> implements EnvDao {

    @Override
    public EnvBO getByName(String name) {

        return findOne(new Match(Columns.NAME, name));
    }

}
