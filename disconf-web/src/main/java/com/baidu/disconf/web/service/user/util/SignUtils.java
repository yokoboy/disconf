package com.baidu.disconf.web.service.user.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

public class SignUtils {

    /**
     * 生成密码， 使用shaHex加密
     */
    public static String createPassword(String password) {
        return DigestUtils.shaHex(password);
    }

    /**
     * 生成token ,使用 UUID + 手机生成
     */
    public static String createToken(String username) {
        String uuid = UUID.randomUUID().toString();
        return DigestUtils.shaHex(uuid + username);
    }
}
