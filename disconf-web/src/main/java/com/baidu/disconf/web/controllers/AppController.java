package com.baidu.disconf.web.controllers;

import com.baidu.disconf.web.constant.AuthMngConstant;
import com.baidu.disconf.web.controllers.validator.AppValidator;
import com.baidu.disconf.web.service.app.form.AppNewForm;
import com.baidu.disconf.web.service.app.service.AppMgr;
import com.baidu.disconf.web.service.app.vo.AppVO;
import com.baidu.disconf.web.service.user.aop.Auth;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.vo.JsonObjectBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@RestController
@RequestMapping(WebConstants.API_PREFIX + "/app")
public class AppController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private AppMgr appMgr;
    @Autowired
    private AppValidator appValidator;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonObjectBase list() {
        List<AppVO> appVOS = appMgr.getAppVoList();
        return buildListSuccess(appVOS, appVOS.size());
    }

    @Auth(AuthMngConstant.CHANGE_APP)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public JsonObjectBase create(@Valid AppNewForm appNewForm) {
        LOG.info(appNewForm.toString());
        appValidator.validateCreate(appNewForm);
        appMgr.create(appNewForm);
        return buildSuccess("创建成功");
    }

}
