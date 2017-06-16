package com.baidu.disconf.web.service.role.bo;

import com.baidu.dsp.common.dao.DB;
import com.baidu.unbiz.common.genericdao.annotation.Column;
import com.baidu.unbiz.common.genericdao.annotation.Table;
import com.github.knightliao.apollo.db.bo.BaseObject;

/**
 * Created by Kail on 2017/6/15.
 */
@Table(db = DB.DB_NAME, name = "auth")
public class AuthBO extends BaseObject<Integer> {

    private static final long serialVersionUID = 1L;

    @Column(value = "role_id")
    private Integer roleId;

    @Column(value = "auth_info")
    private String authInfo;


    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(String authInfo) {
        this.authInfo = authInfo;
    }
}
