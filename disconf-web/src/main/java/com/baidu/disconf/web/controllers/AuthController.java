package com.baidu.disconf.web.controllers;

import com.baidu.disconf.web.constant.AuthMngConstant;
import com.baidu.disconf.web.constant.CommonConstants;
import com.baidu.disconf.web.service.app.service.impl.AppMgrImpl;
import com.baidu.disconf.web.service.app.vo.AppVO;
import com.baidu.disconf.web.service.env.model.EnvVO;
import com.baidu.disconf.web.service.env.service.impl.EnvMgrImpl;
import com.baidu.disconf.web.service.role.bo.Auth;
import com.baidu.disconf.web.service.role.dao.impl.AuthDaoImpl;
import com.baidu.disconf.web.service.role.service.impl.RoleMgrImpl;
import com.baidu.disconf.web.service.user.model.VisitorDTO;
import com.baidu.disconf.web.service.user.service.AuthService;
import com.baidu.disconf.web.service.user.service.UserService;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.exception.RemoteException;
import com.baidu.dsp.common.vo.JsonObjectBase;
import com.baidu.unbiz.common.genericdao.operator.Match;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by Kail on 2017/6/15.
 */
@RestController
@RequestMapping(WebConstants.API_PREFIX + "/auth_mng")
public class AuthController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private EnvMgrImpl envMgr;
    @Autowired
    private AppMgrImpl appMgr;

    @Autowired
    private RoleMgrImpl roleMgr;
    @Autowired
    private AuthDaoImpl authDao;
    @Autowired
    private UserService userService;

    /**
     * 公共权限
     */
    @RequestMapping(value = "/header", method = RequestMethod.GET)
    public JsonObjectBase headerAuth() {
        VisitorDTO curVisitor = userService.getCurVisitor();
        if (null != curVisitor) {
            Set<String> optId = new HashSet<String>();
            List<String> auths = curVisitor.getAuths();
            for (String auth : auths) {
                if (auth.startsWith("0-")) {
                    optId.add(auth);
                }
            }
            return buildSuccess("opt", optId);
        }
        return buildSuccess("opt", new HashMap());
    }

    /**
     * 获取左侧菜单
     */
    @RequestMapping(value = "/env_app_opt", method = RequestMethod.GET)
    public JsonObjectBase envAppOpt(@RequestParam("env_app") String envApp) {
        VisitorDTO curVisitor = userService.getCurVisitor();
        if (null != curVisitor) {
            Set<String> optId = new HashSet<String>();
            List<String> auths = curVisitor.getAuths();
            for (String auth : auths) {
                if (auth.startsWith(envApp)) {
                    optId.add(auth.replace(envApp + "-", ""));
                }
            }
            return buildSuccess("opt", optId);
        }
        return buildSuccess("opt", new HashMap());
    }

    /**
     * 获取左侧菜单
     */
    @RequestMapping(value = "/env_app", method = RequestMethod.GET)
    public JsonObjectBase envApp() {

        VisitorDTO curVisitor = userService.getCurVisitor();
        if (null != curVisitor) {
            List<String> auths = curVisitor.getAuths();

            List<EnvVO> envVO = envMgr.getVoList(); // 环境
            List<AppVO> authAppVoList = appMgr.getAppVoList(); // APP

            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            for (final EnvVO env : envVO) {
                final String envId = env.getId() + "";
                if (AuthService.hasAuth(auths, envId)) {
                    data.add(new HashMap<String, Object>() {{
                        put("id", envId);
                        put("name", env.getName());
                        put("pId", "0");
                    }});

                    for (final AppVO app : authAppVoList) {
                        final String envAppId = envId + "-" + app.getId();
                        if (AuthService.hasAuth(auths, envAppId)) {
                            data.add(new HashMap<String, Object>() {{
                                put("id", envAppId);
                                put("name", app.getName());
                                put("pId", env.getId());
                            }});
                        }
                    }
                }
            }
            return buildSuccess("envApp", data);
        }
        return buildSuccess("envApp", new HashMap());
    }

    /**
     * 获取所有权限
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonObjectBase authList() {
        List<EnvVO> envVO = envMgr.getVoList(); // 环境
        List<AppVO> authAppVoList = appMgr.getAppVoList(); // APP

        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        data.add(new HashMap<String, Object>() {{
            put("id", "0");
            put("name", "全选");
            put("open", true);
        }});
        data.add(new HashMap<String, Object>() {{
            put("id", AuthMngConstant.AUTH_MNG);
            put("name", "权限管理");
            put("pId", "0");
        }});
        data.add(new HashMap<String, Object>() {{
            put("id", AuthMngConstant.CHANGE_APP);
            put("name", "新建APP");
            put("pId", "0");
        }});
        data.add(new HashMap<String, Object>() {{
            put("id", AuthMngConstant.CHANGE_CONFIG_ITEM);
            put("name", "新建配置项");
            put("pId", "0");
        }});

        data.add(new HashMap<String, Object>() {{
            put("id", AuthMngConstant.CHANGE_CONFIG_ITEM);
            put("name", "新建配置文件");
            put("pId", "0");
        }});

        for (final EnvVO env : envVO) {
            data.add(new HashMap<String, Object>() {{
                put("id", env.getId() + "");
                put("name", env.getName());
                put("pId", "0");
            }});
            for (final AppVO app : authAppVoList) {
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

    /************************************************角色管理*********************************************************************/

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
            String[] split = auth.split(CommonConstants.AUTH_SPLIT);

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
