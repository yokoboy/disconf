package com.baidu.disconf.web.web.auth.controller;

import com.baidu.disconf.web.service.role.bo.Role;
import com.baidu.disconf.web.service.role.service.impl.RoleMgrImpl;
import com.baidu.disconf.web.service.sign.service.SignMgr;
import com.baidu.disconf.web.service.user.service.UserMgr;
import com.baidu.disconf.web.web.auth.login.RedisLogin;
import com.baidu.disconf.web.web.auth.validator.AuthValidator;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.vo.JsonObjectBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author liaoqiqi
 * @version 2014-1-20
 */
@RestController
@RequestMapping(WebConstants.API_PREFIX + "/auth_mng")
public class AuthMngController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(AuthMngController.class);

    @Autowired
    private UserMgr userMgr;

    @Autowired
    private AuthValidator authValidator;

    @Autowired
    private SignMgr signMgr;

    @Autowired
    private RedisLogin redisLogin;

    @Autowired
    private RoleMgrImpl roleMgr;

    /**
     * 获取所有角色
     */
    @RequestMapping(value = "/role/list", method = RequestMethod.GET)
    public JsonObjectBase roleList() {
        // TODO 判断该用户是否可以修改角色  return buildGlobalError("syserror.inner", ErrorCode.GLOBAL_ERROR);

        return buildSuccess("allRoles", roleMgr.findAll());

    }

}
