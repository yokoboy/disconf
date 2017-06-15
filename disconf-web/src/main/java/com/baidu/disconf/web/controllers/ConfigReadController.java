package com.baidu.disconf.web.controllers;

import com.baidu.disconf.web.controllers.validator.ConfigValidator;
import com.baidu.disconf.web.service.config.form.ConfListForm;
import com.baidu.disconf.web.service.config.form.VersionListForm;
import com.baidu.disconf.web.service.config.service.impl.ConfigMgrImpl;
import com.baidu.disconf.web.service.config.vo.ConfListVo;
import com.baidu.disconf.web.service.config.vo.MachineListVo;
import com.baidu.disconf.web.utils.MyStringUtils;
import com.baidu.disconf.web.utils.TarUtils;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.exception.DocumentNotFoundException;
import com.baidu.dsp.common.vo.JsonObjectBase;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 专用于配置读取
 *
 * @author liaoqiqi
 * @version 2014-6-22
 */
@RestController
@RequestMapping(WebConstants.API_PREFIX + "/web/config")
public class ConfigReadController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigReadController.class);

    @Autowired
    private ConfigMgrImpl configMgr;

    @Autowired
    private ConfigValidator configValidator;

    /**
     * 获取列表,有分页的
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JsonObjectBase getConfigList(@RequestParam("env_app") String envApp) {
        List<ConfListVo> confListVos = configMgr.selectConfigList(envApp, true, false);
        return buildSuccess(confListVos);
    }

    /**
     * 获取列表,有分页的, 没有ZK信息
     */
    @RequestMapping(value = "/simple/list", method = RequestMethod.GET)
    public JsonObjectBase getSimpleConfigList(@RequestParam("env_app") String envApp) {
        List<ConfListVo> confListVos = configMgr.selectConfigList(envApp, false, false);
        return buildSuccess(confListVos);
    }


    /**
     * 获取版本的List
     *
     * @return
     */
    @RequestMapping(value = "/versionlist", method = RequestMethod.GET)
    public JsonObjectBase getVersionList(@Valid VersionListForm versionListForm) {
        LOG.info(versionListForm.toString());
        List<String> versionList = configMgr.getVersionListByAppEnv(versionListForm.getAppId(), versionListForm.getEnvId());
        return buildListSuccess(versionList, versionList.size());
    }


    /**
     * 获取某个
     */
    @RequestMapping(value = "/{configId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonObjectBase getConfig(@PathVariable long configId) {

        // 业务校验
        configValidator.valideConfigExist(configId);

        ConfListVo config = configMgr.getConfVo(configId);

        if (null != config && config.getKey().endsWith(".properties")) {
            config.setValue(MyStringUtils.clearPassword(config.getValue()));
        }

        return buildSuccess(config);
    }

    /**
     * 获取 zk
     *
     * @param configId
     * @return
     */
    @RequestMapping(value = "/zk/{configId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonObjectBase getZkInfo(@PathVariable long configId) {

        // 业务校验
        configValidator.valideConfigExist(configId);

        MachineListVo machineListVo = configMgr.getConfVoWithZk(configId);

        return buildSuccess(machineListVo);
    }

    /**
     * 下载
     *
     * @param configId
     * @return
     */
    @RequestMapping(value = "/download/{configId}", method = RequestMethod.GET)
    public HttpEntity<byte[]> downloadDspBill(@PathVariable long configId) {

        // 业务校验
        configValidator.valideConfigExist(configId);

        ConfListVo config = configMgr.getConfVo(configId);

        HttpHeaders header = new HttpHeaders();
        byte[] res = config.getValue().getBytes();
        if (res == null) {
            throw new DocumentNotFoundException(config.getKey());
        }

        String name = null;

        try {
            name = URLEncoder.encode(config.getKey(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        header.set("Content-Disposition", "attachment; filename=" + name);
        header.setContentLength(res.length);
        return new HttpEntity<byte[]>(res, header);
    }

    /**
     * 批量下载配置文件
     *
     * @param confListForm
     * @return
     */
    @RequestMapping(value = "/downloadfilebatch", method = RequestMethod.GET)
    public HttpEntity<byte[]> download2(@Valid ConfListForm confListForm) {

        LOG.info(confListForm.toString());

        //
        // get files
        //
        List<File> fileList = configMgr.getDisconfFileList(confListForm);

        //
        // prefix
        //
        String prefixString =
                "APP" + confListForm.getAppId() + "_" + "ENV" + confListForm.getEnvId() + "_" + "VERSION" +
                        confListForm.getVersion();

        HttpHeaders header = new HttpHeaders();

        String targetFileString = "";
        File targetFile = null;
        byte[] res = null;
        try {
            targetFileString = TarUtils.tarFiles("tmp", prefixString, fileList);
            targetFile = new File(targetFileString);
            res = IOUtils.toByteArray(new FileInputStream(targetFile));
        } catch (Exception e) {
            throw new DocumentNotFoundException("");
        }

        header.set("Content-Disposition", "attachment; filename=" + targetFile.getName());
        header.setContentLength(res.length);
        return new HttpEntity<byte[]>(res, header);
    }
}
