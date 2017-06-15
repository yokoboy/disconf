package com.baidu.disconf.web.controllers;

import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;
import com.baidu.disconf.web.service.config.form.ConfNewForm;
import com.baidu.disconf.web.service.config.form.ConfNewItemForm;
import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.controllers.validator.ConfigValidator;
import com.baidu.disconf.web.controllers.validator.FileUploadValidator;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.context.impl.ContextReaderImpl;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.exception.FieldException;
import com.baidu.dsp.common.exception.FileUploadException;
import com.baidu.dsp.common.exception.base.RuntimeGlobalException;
import com.baidu.dsp.common.vo.JsonObjectBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 专用于配置新建
 *
 * @author liaoqiqi
 * @version 2014-6-24
 */
@Controller
@RequestMapping(WebConstants.API_PREFIX + "/web/config")
public class ConfigNewController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigUpdateController.class);

    @Autowired
    private ConfigMgr configMgr;
    @Autowired
    private ConfigValidator configValidator;
    @Autowired
    private FileUploadValidator fileUploadValidator;
    @Autowired
    private ContextReaderImpl contextReader;

    /**
     * 配置项的新建
     *
     * @param confNewForm
     * @return
     */
    @RequestMapping(value = "/item", method = RequestMethod.POST)
    @ResponseBody
    public JsonObjectBase newItem(@Valid ConfNewItemForm confNewForm) {

        // 业务校验
        configValidator.validateNew(confNewForm, DisConfigTypeEnum.ITEM);

        //
        configMgr.newConfig(confNewForm, DisConfigTypeEnum.ITEM);

        return buildSuccess("创建成功");
    }

    /**
     * 配置文件的新建(使用上传配置文件)
     */
    @ResponseBody
    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public JsonObjectBase updateFile(@Valid ConfNewForm confNewForm, @RequestParam("myfilerar") MultipartFile[] files) {

        LOG.info(confNewForm.toString());

        if (files.length <= 0) {
            throw new FieldException("file", "您没有上传文件", null);
        }

        StringBuilder stringBuilder = new StringBuilder("<br>");

        for (MultipartFile file : files) {
            try {
                int fileSize = 1024 * 1024 * 4;
                fileUploadValidator.validateFile(file, fileSize, null);

                // 更新
                String fileContent;
                try {
                    fileContent = new String(file.getBytes(), "UTF-8");
                    LOG.info("receive file: " + fileContent);
                } catch (Exception e) {
                    LOG.error(e.toString());
                    throw new FileUploadException("upload file error", e);
                }

                // 创建配置文件表格
                ConfNewItemForm confNewItemForm = new ConfNewItemForm(confNewForm); // save: appId,version,envId
                confNewItemForm.setKey(file.getOriginalFilename());
                confNewItemForm.setValue(fileContent);

                // 业务校验
                configValidator.validateNew(confNewItemForm, DisConfigTypeEnum.FILE);
                //
                configMgr.newConfig(confNewItemForm, DisConfigTypeEnum.FILE);

                stringBuilder.append(file.getOriginalFilename()).append(" ::  成功 <br>");
            } catch (RuntimeGlobalException ex) {
                String errorMessage = ex.getErrorMessage();
                stringBuilder.append(file.getOriginalFilename()).append(" ::  失败:: 原因:: ").append(errorMessage).append(" :: ").append(contextReader.getMessage(errorMessage)).append("<br>");
            } catch (Exception ex) {
                stringBuilder.append(file.getOriginalFilename()).append(" ::  失败:: 原因:: ").append(ex.getMessage()).append("<br>");
            }

        }

        return buildSuccess(stringBuilder.toString());
    }

    /**
     * 配置文件的新建(使用文本)
     *
     * @param confNewForm
     * @param fileContent
     * @param fileName
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/filetext", method = RequestMethod.POST)
    public JsonObjectBase updateFileWithText(@Valid ConfNewForm confNewForm, @NotNull String fileContent,
                                             @NotNull String fileName) {

        LOG.info(confNewForm.toString());

        // 创建配置文件表格
        ConfNewItemForm confNewItemForm = new ConfNewItemForm(confNewForm);
        confNewItemForm.setKey(fileName);
        confNewItemForm.setValue(fileContent);

        // 业务校验
        configValidator.validateNew(confNewItemForm, DisConfigTypeEnum.FILE);

        //
        configMgr.newConfig(confNewItemForm, DisConfigTypeEnum.FILE);

        return buildSuccess("创建成功");
    }
}
