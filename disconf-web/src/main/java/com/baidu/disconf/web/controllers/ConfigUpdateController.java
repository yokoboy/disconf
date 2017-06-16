package com.baidu.disconf.web.controllers;

import com.baidu.disconf.web.constant.AuthMngConstant;
import com.baidu.disconf.web.controllers.validator.ConfigValidator;
import com.baidu.disconf.web.controllers.validator.FileUploadValidator;
import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.service.user.aop.Auth;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.exception.FileUploadException;
import com.baidu.dsp.common.vo.JsonObjectBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * 专用于配置更新、删除
 *
 * @author liaoqiqi
 * @version 2014-6-24
 */
@RestController
@RequestMapping(WebConstants.API_PREFIX + "/web/config")
public class ConfigUpdateController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigUpdateController.class);

    @Autowired
    private ConfigMgr configMgr;

    @Autowired
    private ConfigValidator configValidator;

    @Autowired
    private FileUploadValidator fileUploadValidator;

    /**
     * 配置项的更新
     */
    @Auth(optId = AuthMngConstant.WRITE)
    @RequestMapping(value = "/item/{configId}", method = RequestMethod.PUT)
    public JsonObjectBase updateItem(@RequestParam("env_app") String envApp, @PathVariable long configId, String value) {

        // 业务校验
        configValidator.validateUpdateItem(configId, value);

        LOG.info("start to update config: " + configId);

        //
        // 更新, 并写入数据库
        //
        String emailNotification = "";
        emailNotification = configMgr.updateItemValue(configId, value);

        //
        // 通知ZK
        //
        configMgr.notifyZookeeper(configId);

        return buildSuccess(emailNotification);
    }

    /**
     * 配置文件的更新
     */
    @Auth(optId = AuthMngConstant.WRITE)
    @RequestMapping(value = "/file/{configId}", method = RequestMethod.POST)
    public JsonObjectBase updateFile(@RequestParam("env_app") String envApp, @PathVariable long configId, @RequestParam("myfilerar") MultipartFile file) {

        //
        // 校验
        //
        int fileSize = 1024 * 1024 * 4;
        String[] allowExtName = {".properties", ".xml"};
        fileUploadValidator.validateFile(file, fileSize, allowExtName);

        // 业务校验
        configValidator.validateUpdateFile(configId, file.getOriginalFilename());

        //
        // 更新
        //
        String emailNotification = "";
        try {
            String str = new String(file.getBytes(), "UTF-8");
            LOG.info("receive file: " + str);

            emailNotification = configMgr.updateItemValue(configId, str);
            LOG.info("update " + configId + " ok");

        } catch (Exception e) {

            LOG.error(e.toString());
            throw new FileUploadException("upload file error", e);
        }

        //
        // 通知ZK
        //
        configMgr.notifyZookeeper(configId);

        return buildSuccess(emailNotification);
    }

    /**
     * 配置文件的更新(文本修改)
     */
    @Auth(optId = AuthMngConstant.WRITE)
    @RequestMapping(value = "/filetext/{configId}", method = RequestMethod.PUT)
    public JsonObjectBase updateFileWithText(@RequestParam("env_app") String envApp, @PathVariable long configId, @NotNull String fileContent) {

        //
        // 更新
        //
        String emailNotification = "";
        try {
            emailNotification = configMgr.updateItemValue(configId, fileContent);
            LOG.info("update " + configId + " ok");

        } catch (Exception e) {

            throw new FileUploadException("upload.file.error", e);
        }

        //
        // 通知ZK
        //
        configMgr.notifyZookeeper(configId);

        return buildSuccess(emailNotification);
    }

    /**
     * delete
     */
    @Auth(optId = AuthMngConstant.DELETE)
    @RequestMapping(value = "/{configId}", method = RequestMethod.DELETE)
    public JsonObjectBase delete(@RequestParam("env_app") String envApp, @PathVariable long configId) {

        configValidator.validateDelete(configId);

        configMgr.delete(configId);

        return buildSuccess("删除成功");
    }
}
