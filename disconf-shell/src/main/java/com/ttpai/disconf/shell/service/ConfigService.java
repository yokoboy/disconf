package com.ttpai.disconf.shell.service;

import com.ttpai.disconf.shell.dao.AppDAO;
import com.ttpai.disconf.shell.dao.ConfigDAO;
import com.ttpai.disconf.shell.dao.EnvDAO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Kail on 2017/6/12.
 */
@Service
public class ConfigService {

    @Autowired
    private AppDAO appDAO;
    @Autowired
    private EnvDAO envDAO;
    @Autowired
    private ConfigDAO configDAO;

    /**
     * 不支持二进制的配置文件
     *
     * @param appName 应用名必须唯一
     * @param envName 应用下的环境名必须唯一
     */
    public Map<String, String> selectConfigFileByAppAndEnv(String appName, String envName) {
        if (StringUtils.isBlank(appName) && StringUtils.isBlank(envName)) {
            throw new RuntimeException("--app 和 --env 不能为空");
        }

        Integer appId = appDAO.selectAppIdByName(appName);
        if (null == appId) {
            throw new RuntimeException("App信息 “" + appName + "” 不存在");
        }

        Integer envId = envDAO.selectEnvIdByName(envName);
        if (null == envId) {
            throw new RuntimeException("Env信息 “" + envName + "” 不存在");
        }

        return configDAO.selectEnvIdByName(appId, envId); // 文件名和文件内容
    }

}
