package com.baidu.disconf.web.controllers.validator;

import com.baidu.disconf.web.model.ConfigFullModel;
import com.baidu.disconf.web.service.app.bo.App;
import com.baidu.disconf.web.service.app.service.AppMgr;
import com.baidu.disconf.web.service.env.model.EnvBO;
import com.baidu.disconf.web.service.env.service.impl.EnvMgrImpl;
import com.baidu.disconf.web.service.zookeeper.form.ZkDeployForm;
import com.baidu.dsp.common.exception.FieldException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liaoqiqi
 * @version 2014-9-11
 */
@Service
public class ZooValidator {

    @Autowired
    private AppMgr appMgr;
    @Autowired
    private EnvMgrImpl envMgr;


    public ConfigFullModel verify(ZkDeployForm zkDeployForm) {

        //
        // app
        //
        if (zkDeployForm.getAppId() == null) {
            throw new FieldException("app is empty", null);
        }

        App app = appMgr.getById(zkDeployForm.getAppId());
        if (app == null) {
            throw new FieldException("app " + zkDeployForm.getAppId() + " doesn't exist in db.", null);
        }

        //
        // envBO
        //
        if (zkDeployForm.getEnvId() == null) {
            throw new FieldException("app is empty", null);
        }

        EnvBO envBO = envMgr.getById(zkDeployForm.getEnvId());
        if (envBO == null) {
            throw new FieldException("envBO " + zkDeployForm.getEnvId() + " doesn't exist in db.", null);
        }

        //
        // version
        //
        if (StringUtils.isEmpty(zkDeployForm.getVersion())) {
            throw new FieldException("version is empty", null);
        }

        return new ConfigFullModel(app, envBO, zkDeployForm.getVersion(), "");
    }
}
