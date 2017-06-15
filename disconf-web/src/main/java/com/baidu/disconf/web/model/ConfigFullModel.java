package com.baidu.disconf.web.model;

import com.baidu.disconf.web.service.app.bo.App;
import com.baidu.disconf.web.service.env.model.EnvBO;

/**
 * @author liaoqiqi
 * @version 2014-9-11
 */
public class ConfigFullModel {

    private App app;
    private EnvBO envBO;
    private String version;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public EnvBO getEnvBO() {
        return envBO;
    }

    public void setEnvBO(EnvBO envBO) {
        this.envBO = envBO;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ConfigFullModel(App app, EnvBO envBO, String version, String key) {
        super();
        this.app = app;
        this.envBO = envBO;
        this.version = version;
        this.key = key;
    }
}
