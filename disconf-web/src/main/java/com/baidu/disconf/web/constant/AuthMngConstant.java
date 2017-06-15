package com.baidu.disconf.web.constant;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Kail on 2017/6/15.
 */
public interface AuthMngConstant {


    String READ = "1";
    String WRITE = "2";
    String DOWNLOAD = "3";
    String DOWNLOAD_BATCH = "4";
    String DELETE = "5";
    String ZK = "6";

    Map<String, String> opt = new LinkedHashMap<String, String>() {{
        put(READ, "查看");
        put(WRITE, "修改");
        put(DOWNLOAD, "下载");
        put(DOWNLOAD_BATCH, "批量下载");
        put(DELETE, "删除");
        put(ZK, "查看ZK");
    }};

    String AUTH_MNG = "0-0";
    String CHANGE_APP = "0-1";
    String CHANGE_CONFIG_ITEM = "0-2";
    String CHANGE_CONFIG_FILE = "0-3";


}
