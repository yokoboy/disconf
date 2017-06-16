package com.baidu.disconf.web.controllers;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.json.ValueVo;
import com.baidu.disconf.web.controllers.validator.ZooValidator;
import com.baidu.disconf.web.model.ConfigFullModel;
import com.baidu.disconf.web.service.zookeeper.config.ZooConfig;
import com.baidu.disconf.web.service.zookeeper.form.ZkDeployForm;
import com.baidu.disconf.web.service.zookeeper.service.ZkDeployMgr;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.vo.JsonObjectBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Zoo API
 *
 * @author liaoqiqi
 * @version 2014-1-20
 */
@RestController
@RequestMapping(WebConstants.API_PREFIX + "/zoo")
public class ZooController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(ZooController.class);

    @Autowired
    private ZooConfig zooConfig;
    @Autowired
    private ZooValidator zooValidator;
    @Autowired
    private ZkDeployMgr zkDeployMgr;

    /**
     * 获取Zookeeper地址
     */
    @RequestMapping(value = "/hosts", method = RequestMethod.GET)
    public ValueVo getHosts() {

        ValueVo confItemVo = new ValueVo();
        confItemVo.setStatus(Constants.OK);
        confItemVo.setValue(zooConfig.getZooHosts());

        return confItemVo;
    }

    /**
     * 获取ZK prefix
     */
    @RequestMapping(value = "/prefix", method = RequestMethod.GET)
    public ValueVo getPrefixUrl() {

        ValueVo confItemVo = new ValueVo();
        confItemVo.setStatus(Constants.OK);
        confItemVo.setValue(zooConfig.getZookeeperUrlPrefix());

        return confItemVo;
    }

    /**
     * 获取ZK 部署情况
     */
    @RequestMapping(value = "/zkdeploy", method = RequestMethod.GET)
    public JsonObjectBase getZkDeployInfo(@Valid ZkDeployForm zkDeployForm) {

        LOG.info(zkDeployForm.toString());

        ConfigFullModel configFullModel = zooValidator.verify(zkDeployForm);
        String data = zkDeployMgr.getDeployInfo(configFullModel.getApp().getName(), configFullModel.getEnvBO().getName(), zkDeployForm.getVersion());

        return buildSuccess("hostInfo", data);
    }
}