package com.baidu.disconf.web.service.env.service.impl;

import com.baidu.disconf.web.service.env.dao.EnvDao;
import com.baidu.disconf.web.service.env.model.EnvBO;
import com.baidu.disconf.web.service.env.model.EnvVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@Service
public class EnvMgrImpl {

    @Autowired
    private EnvDao envDao;

    public EnvBO getByName(String name) {

        return envDao.getByName(name);
    }

    public List<EnvVO> getVoList() {
        List<EnvBO> envBOS = envDao.findAll();
        List<EnvVO> envVOS = new ArrayList<EnvVO>();
        for (EnvBO envBO : envBOS) {
            EnvVO envVO = new EnvVO();
            envVO.setId(envBO.getId());
            envVO.setName(envBO.getName());
            envVOS.add(envVO);
        }
        return envVOS;
    }

    public Map<Long, EnvBO> getByIds(Set<Long> ids) {

        if (ids.size() == 0) {
            return new HashMap<Long, EnvBO>();
        }

        List<EnvBO> envBOS = envDao.get(ids);

        Map<Long, EnvBO> map = new HashMap<Long, EnvBO>();
        for (EnvBO envBO : envBOS) {
            map.put(envBO.getId(), envBO);
        }

        return map;
    }

    public EnvBO getById(Long id) {
        return envDao.get(id);
    }

    /**
     *
     */
    public List<EnvBO> getList() {
        return envDao.findAll();
    }

}
