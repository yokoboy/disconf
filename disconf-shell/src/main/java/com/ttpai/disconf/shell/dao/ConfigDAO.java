package com.ttpai.disconf.shell.dao;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

import java.util.Map;

/**
 * `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '配置文件0/配置项1',
 * `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：1是正常 0是删除',
 * `name` varchar(255) NOT NULL DEFAULT '' COMMENT '配置文件名/配置项KeY名',
 * `value` text NOT NULL COMMENT '0 配置文件：文件的内容，1 配置项：配置值',
 * <p>
 * Created by Kail on 2017/6/12.
 */
@DAO
public interface ConfigDAO {

    @SQL("SELECT `name`,`value` FROM `config` WHERE app_id=:appId AND env_id=:envId AND type=0 AND `status`=1 ")
    Map<String, String> selectEnvIdByName(@SQLParam("appId") Integer appId, @SQLParam("envId") Integer envId);

}
