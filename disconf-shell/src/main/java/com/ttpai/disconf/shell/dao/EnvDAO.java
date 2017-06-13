package com.ttpai.disconf.shell.dao;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

/**
 * Created by Kail on 2017/6/12.
 */
@DAO
public interface EnvDAO {

    @SQL("SELECT env_id FROM `env` WHERE `name`=:name") // 这里不 limit 1, 如果返回多个直接报错
    Integer selectEnvIdByName(@SQLParam("name") String name);

}
