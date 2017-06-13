package com.baidu.disconf.web.service.user.service.impl;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baidu.disconf.web.service.user.service.AuthMgr;
import com.baidu.disconf.web.service.user.service.UserInnerMgr;

/**
 * @author knightliao
 */
@Service
public class AuthMgrImpl implements AuthMgr {

    @Autowired
    private UserInnerMgr userInnerMgr;

    /**
     * 根据appId 判断是否有操作权限
     */
    @Override
    public boolean verifyApp4CurrentUser(Long appId) {
        Set<Long> idsLongs = userInnerMgr.getVisitorAppIds();
        return CollectionUtils.isEmpty(idsLongs) || idsLongs.contains(appId);
    }

}
