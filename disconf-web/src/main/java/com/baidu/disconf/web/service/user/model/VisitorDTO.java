package com.baidu.disconf.web.service.user.model;

import com.baidu.disconf.web.constant.CommonConstants;
import com.github.knightliao.apollo.db.bo.BaseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author liaoqiqi
 * @version 2013-11-26
 */
public class VisitorDTO extends BaseObject<Long> implements Serializable {

    protected static final Logger LOG = LoggerFactory.getLogger(VisitorDTO.class);

    private static final long serialVersionUID = 5621993194031128338L;

    private String loginUserName;

    private List<Integer> roleIds = new ArrayList<Integer>();
    private List<String> auths = new ArrayList<String>();

    public void setRoleStr(String roleStr) {
        List<String> roleIdStrs = Arrays.asList(roleStr.split(CommonConstants.AUTH_SPLIT));
        for (String roleIdStr : roleIdStrs) {
            this.roleIds.add(Integer.valueOf(roleIdStr));
        }
    }

    public void setAuthStr(String authStr) {
        this.auths.addAll(Arrays.asList(authStr.split(CommonConstants.AUTH_SPLIT)));
    }

    // ==================================================================================================================================================================================================================

    public String getLoginUserName() {
        return loginUserName;
    }

    public void setLoginUserName(String loginUserName) {
        this.loginUserName = loginUserName;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public List<String> getAuths() {
        return auths;
    }

    public void setAuths(List<String> auths) {
        this.auths = auths;
    }

    @Override
    public String toString() {
        return "VisitorDTO{" +
                "loginUserName='" + loginUserName + '\'' +
                ", roleIds=" + roleIds +
                ", auths=" + auths +
                '}';
    }
}
