package com.baidu.disconf.web.controllers.validator;

import com.baidu.disconf.web.service.user.model.UserBO;
import com.baidu.disconf.web.service.user.model.SigninForm;
import com.baidu.disconf.web.service.user.service.SignMgrImpl;
import com.baidu.disconf.web.service.user.model.VisitorDTO;
import com.baidu.disconf.web.service.user.model.PasswordModifyForm;
import com.baidu.disconf.web.service.user.service.UserService;
import com.baidu.dsp.common.exception.FieldException;
import com.baidu.ub.common.commons.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 权限验证
 *
 * @author liaoqiqi
 * @version 2014-7-2
 */
@Component
public class AuthValidator {

    @Autowired
    private SignMgrImpl signMgr;

    @Autowired
    private UserService userMgr;

    /**
     * 验证登录
     */
    public void validateLogin(SigninForm signinForm) {
        //
        // 校验用户是否存在
        //
        UserBO userBO = signMgr.getUserByName(signinForm.getName());
        if (userBO == null) {
            throw new FieldException(SigninForm.Name, "user.not.exist", null);
        }
        // 校验密码
        if (!signMgr.validate(userBO.getPassword(), signinForm.getPassword())) {
            throw new FieldException(SigninForm.PASSWORD, "password.not.right", null);
        }
    }

    /**
     * 验证密码更新
     */
    public void validatePasswordModify(PasswordModifyForm passwordModifyForm) {

        VisitorDTO visitorDTO = ThreadContext.getSessionVisitor();

        UserBO userBO = userMgr.getUser(visitorDTO.getId());

        // 校验密码
        if (!signMgr.validate(userBO.getPassword(), passwordModifyForm.getOld_password())) {
            throw new FieldException(PasswordModifyForm.OLD_PASSWORD, "password.not.right", null);
        }

        if (!passwordModifyForm.getNew_password().equals(passwordModifyForm.getNew_password_2())) {
            throw new FieldException(PasswordModifyForm.NEW_PASSWORD, "two.password.not.equal", null);
        }
    }
}
