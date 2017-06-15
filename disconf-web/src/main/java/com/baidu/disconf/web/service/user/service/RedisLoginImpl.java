package com.baidu.disconf.web.service.user.service;

import com.baidu.disconf.web.constant.LoginConstant;
import com.baidu.disconf.web.service.user.constant.UserConstant;
import com.baidu.disconf.web.service.user.model.UserBO;
import com.baidu.disconf.web.service.user.model.VisitorDTO;
import com.baidu.ub.common.commons.ThreadContext;
import com.github.knightliao.apollo.redis.RedisCacheManager;
import com.github.knightliao.apollo.utils.web.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author liaoqiqi
 * @version 2014-2-4
 */
public class RedisLoginImpl {

    @Autowired
    private RedisCacheManager redisCacheMgr;
    @Autowired
    private AuthService authService;

    /**
     * 获取Redis上的User Key
     */
    private String getRedisKey(String baiduId) {
        return baiduId + UserConstant.USER_KEY;
    }

    /**
     * 校验Redis是否登录
     */
    public VisitorDTO isLogin(HttpServletRequest request) {
        String xId = CookieUtils.getCookieValue(request, LoginConstant.XONE_COOKIE_NAME_STRING);
        if (xId != null) {
            return (VisitorDTO) redisCacheMgr.get(this.getRedisKey(xId));
        }
        return null;
    }

    /**
     * 登录
     */
    public void loginAndUpdateSession(HttpServletRequest request, UserBO userBO, int expireTime) {
        VisitorDTO visitorDTO = new VisitorDTO();
        //
        visitorDTO.setId(userBO.getId());
        visitorDTO.setLoginUserName(userBO.getName());
        visitorDTO.setRoleStr(userBO.getRoleIds());
        visitorDTO.setAuths(authService.selectAuthListByRoleIds(visitorDTO.getRoleIds()));

        //
        // 更新session
        //
        updateSessionVisitor(request.getSession(), visitorDTO);
        //
        // 更新Redis数据
        //
        updateRedisVisitor(visitorDTO, request, expireTime);
    }

    private void updateRedisVisitor(VisitorDTO visitorDTO, HttpServletRequest request, int expireTime) {
        String xcookieName = CookieUtils.getCookieValue(request, LoginConstant.XONE_COOKIE_NAME_STRING);
        // 更新Redis数据
        if (xcookieName != null) {
            // 更新
            if (visitorDTO != null) {
                redisCacheMgr.put(this.getRedisKey(xcookieName), expireTime, visitorDTO);
            } else {
                // 删除
                redisCacheMgr.remove(this.getRedisKey(xcookieName));
            }
        }
    }

    /**
     * 更新Session中的Userid
     */
    public void updateSessionVisitor(HttpSession session, VisitorDTO visitorDTO) {
        if (visitorDTO != null) {
            // 更新
            session.setAttribute(UserConstant.USER_KEY, visitorDTO);
        } else {
            // 删除
            session.removeAttribute(UserConstant.USER_KEY);
        }
        ThreadContext.putSessionVisitor(visitorDTO);
    }

    /**
     * 登出
     */
    public void logout(HttpServletRequest request) {
        // 更新session
        updateSessionVisitor(request.getSession(), null);
        // 更新redis
        updateRedisVisitor(null, request, 0);
    }

}
