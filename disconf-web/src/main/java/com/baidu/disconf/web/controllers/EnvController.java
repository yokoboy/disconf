package com.baidu.disconf.web.controllers;

import com.baidu.disconf.web.service.env.model.EnvVO;
import com.baidu.disconf.web.service.env.service.impl.EnvMgrImpl;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.vo.JsonObjectBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@RestController
@RequestMapping(WebConstants.API_PREFIX + "/env")
public class EnvController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(EnvController.class);

    @Autowired
    private EnvMgrImpl envMgr;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonObjectBase list() {
        List<EnvVO> envVOS = envMgr.getVoList();
        return buildListSuccess(envVOS, envVOS.size());
    }

}
