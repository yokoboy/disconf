package com.baidu.disconf.web.service.user.dao.impl;

import org.springframework.stereotype.Repository;

import com.baidu.disconf.web.service.user.dao.SignDao;
import com.baidu.disconf.web.service.user.model.UserBO;
import com.baidu.dsp.common.dao.AbstractDao;

/**
 * @author liaoqiqi
 * @version 2013-11-28
 */
@Repository
public class SignDaoImpl extends AbstractDao<Long, UserBO> implements SignDao {

}