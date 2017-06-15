package com.baidu.disconf.web.web.auth.controller;

import com.baidu.disconf.web.service.app.service.impl.AppMgrImpl;
import com.baidu.disconf.web.service.app.vo.AppListVo;
import com.baidu.disconf.web.service.env.service.impl.EnvMgrImpl;
import com.baidu.disconf.web.service.env.vo.EnvListVo;
import com.baidu.disconf.web.service.role.bo.Auth;
import com.baidu.disconf.web.service.role.dao.impl.AuthDaoImpl;
import com.baidu.disconf.web.service.role.service.impl.RoleMgrImpl;
import com.baidu.disconf.web.service.sign.service.SignMgr;
import com.baidu.disconf.web.service.user.service.UserMgr;
import com.baidu.disconf.web.web.auth.constant.AuthMngConstant;
import com.baidu.disconf.web.web.auth.login.RedisLogin;
import com.baidu.disconf.web.web.auth.validator.AuthValidator;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.exception.RemoteException;
import com.baidu.dsp.common.vo.JsonObjectBase;
import com.baidu.unbiz.common.genericdao.operator.Match;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
    private EnvMgrImpl envMgr;
    @Autowired
    private AppMgrImpl appMgr;

    @Autowired
    private RoleMgrImpl roleMgr;
    @Autowired
    private AuthDaoImpl authDao;

    /**
     * 获取所有权限
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonObjectBase authList() {
        List<EnvListVo> envListVo = envMgr.getVoList(); // 环境
        List<AppListVo> authAppVoList = appMgr.getAuthAppVoList(); // APP

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        data.add(new HashMap<String, Object>() {{
            put("id", "0");
            put("name", "全选");
            put("open", true);
        }});
        data.add(new HashMap<String, Object>() {{
            put("id", "0-0");
            put("name", "权限管理");
            put("pId", "0");
        }});

        for (final EnvListVo env : envListVo) {
            data.add(new HashMap<String, Object>() {{
                put("id", env.getId() + "");
                put("name", env.getName());
                put("pId", "0");
            }});
            for (final AppListVo app : authAppVoList) {
                data.add(new HashMap<String, Object>() {{
                    put("id", env.getId() + "-" + app.getId());
                    put("name", app.getName());
                    put("pId", env.getId());
                }});

                Set<Map.Entry<String, String>> entries = AuthMngConstant.opt.entrySet();
                for (final Map.Entry<String, String> entry : entries) {
                    data.add(new HashMap<String, Object>() {{
                        put("id", env.getId() + "-" + app.getId() + "-" + entry.getKey());
                        put("name", entry.getValue());
                        put("pId", env.getId() + "-" + app.getId());
                    }});
                }
            }
        }


        return buildSuccess("allAuths", data);

    }

    /**
     * 获取所有角色
     */
    @RequestMapping(value = "/role/list", method = RequestMethod.GET)
    public JsonObjectBase roleList() {
        // TODO 判断该用户是否可以修改角色  return buildGlobalError("syserror.inner", ErrorCode.GLOBAL_ERROR);

        return buildSuccess("allRoles", roleMgr.findAll());

    }

    /**
     * 获取角色权限
     */
    @RequestMapping(value = "/role/{roleId}", method = RequestMethod.GET)
    public JsonObjectBase role(@PathVariable("roleId") Integer roleId) {
        if (roleId == null) {
            throw new RemoteException("roleId不能为空", null);
        }
        return buildSuccess("authInfos", authDao.find(new Match("role_id", roleId)));

    }

    /**
     * 保存角色权限
     */
    @RequestMapping(value = "/role/{roleId}", method = RequestMethod.POST)
    public JsonObjectBase role(@PathVariable("roleId") Integer roleId, String auth) {

        if (roleId == null) {
            throw new RemoteException("roleId不能为空", null);
        }
        // 删除所有的角色
        authDao.executeSql(AuthDaoImpl.SQL_DELETE_BY_ROLE_ID, roleId);

        if (StringUtils.isNotBlank(auth)) {
            String[] split = auth.split(AuthMngConstant.AUTH_SPLIT);

            List<Auth> authList = new ArrayList<Auth>();
            for (String authIds : split) {
                Auth authVO = new Auth();
                authVO.setRoleId(roleId);
                authVO.setAuthInfo(authIds);
                authList.add(authVO);
            }
            authDao.insert(authList);
        }
        return buildSuccess("ok");

    }


}
