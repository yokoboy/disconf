package com.ttpai.disconf.shell.dao;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;
import net.paoding.rose.jade.annotation.SQLParam;

/**
 * Created by Kail on 2017/6/12.
 */
@DAO
public interface AppDAO {

    @SQL("SELECT app_id FROM `app` WHERE `name`=:name") // 这里不 limit 1, 如果返回多个直接报错
    Integer selectAppIdByName(@SQLParam("name") String name);

}
