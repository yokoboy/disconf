package com.baidu.disconf.web.controllers;

import com.baidu.disconf.web.constant.LoginConstant;
import com.baidu.disconf.web.controllers.validator.AuthValidator;
import com.baidu.disconf.web.service.user.model.*;
import com.baidu.disconf.web.service.user.service.RedisLoginImpl;
import com.baidu.disconf.web.service.user.service.SignMgrImpl;
import com.baidu.disconf.web.service.user.service.UserService;
import com.baidu.dsp.common.annotation.NoAuth;
import com.baidu.dsp.common.constant.ErrorCode;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.vo.JsonObjectBase;
import com.baidu.ub.common.commons.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author liaoqiqi
 * @version 2014-1-20
 */
@RestController
@RequestMapping(WebConstants.API_PREFIX + "/account")
public class UserController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userMgr;
    @Autowired
    private AuthValidator authValidator;
    @Autowired
    private SignMgrImpl signMgr;
    @Autowired
    private RedisLoginImpl redisLogin;

    @NoAuth
    @RequestMapping(value = "/session", method = RequestMethod.GET)
    public JsonObjectBase get() {
        VisitorDTO curVisitor = userMgr.getCurVisitor();
        if (curVisitor != null) {
            return buildSuccess("visitor", curVisitor);
        } else {
            return buildGlobalError("login.error", ErrorCode.GLOBAL_ERROR); // 没有登录啊
        }
    }

    /**
     * 登录
     */
    @NoAuth
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public JsonObjectBase signin(@Valid SigninForm signin, HttpServletRequest request) {
        LOG.info(signin.toString());
        // 验证
        authValidator.validateLogin(signin);
        // 数据库登录
        UserBO userBO = signMgr.signin(signin.getName());
        // 过期时间
        int expireTime = LoginConstant.SESSION_EXPIRE_TIME;
        if (signin.getRemember().equals(1)) {
            expireTime = LoginConstant.SESSION_EXPIRE_TIME2;
        }

        // redis loginAndUpdateSession
        redisLogin.loginAndUpdateSession(request, userBO, expireTime);

        VisitorDTO curVisitor = userMgr.getCurVisitor();

        return buildSuccess("visitor", curVisitor);
    }

    /**
     * 登出
     */
    @NoAuth
    @RequestMapping(value = "/signout", method = RequestMethod.GET)
    public JsonObjectBase signout(HttpServletRequest request) {
        redisLogin.logout(request);
        return buildSuccess("ok", "ok");
    }

    /**
     * 修改密码
     */
    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public JsonObjectBase password(@Valid PasswordModifyForm passwordModifyForm, HttpServletRequest request) {
        // 校验
        authValidator.validatePasswordModify(passwordModifyForm);
        // 修改
        VisitorDTO visitorDTO = ThreadContext.getSessionVisitor();
        userMgr.modifyPassword(visitorDTO.getId(), passwordModifyForm.getNew_password());
        // re loginAndUpdateSession
        redisLogin.logout(request);
        return buildSuccess("修改成功，请重新登录");
    }
}
