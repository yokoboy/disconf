package com.baidu.disconf.web.tasks.impl;

import com.baidu.disconf.web.config.ApplicationPropertyConfigVO;
import com.baidu.disconf.web.service.app.bo.App;
import com.baidu.disconf.web.service.app.service.AppMgr;
import com.baidu.disconf.web.service.config.form.ConfListForm;
import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.service.config.vo.ConfListVo;
import com.baidu.disconf.web.service.env.model.EnvBO;
import com.baidu.disconf.web.service.env.service.impl.EnvMgrImpl;
import com.baidu.disconf.web.service.zookeeper.dto.ZkDisconfData.ZkDisconfDataItem;
import com.baidu.disconf.web.service.zookeeper.service.ZkDeployMgr;
import com.baidu.disconf.web.tasks.IConfigConsistencyMonitorService;
import com.baidu.dsp.common.interceptor.session.SessionInterceptor;
import com.baidu.dsp.common.utils.email.LogMailBean;
import com.baidu.ub.common.db.DaoPageResult;
import com.github.knightliao.apollo.utils.tool.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * http://blog.csdn.net/sd4000784/article/details/7745947 <br/>
 * http://blog.sina.com.cn/s/blog_6925c03c0101d1hi.html
 *
 * @author knightliao
 */
@Component
public class ConfigConsistencyMonitorServiceImpl implements IConfigConsistencyMonitorService {

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigConsistencyMonitorServiceImpl.class);

    @Autowired
    private ApplicationPropertyConfigVO applicationPropertyConfig;

    @Autowired
    private ZkDeployMgr zkDeployMgr;

    @Autowired
    private AppMgr appMgr;

    @Autowired
    private EnvMgrImpl envMgr;

    @Autowired
    private ConfigMgr configMgr;

    @Autowired
    private LogMailBean logMailBean;

    // 每3分钟执行一次自动化校验
    //@Scheduled(fixedDelay = 3 * 60 * 1000)
    @Override
    public void myTest() {
        LOG.info("task schedule just testing, every 1 min");
    }

    /**
     *
     */
    // 每30分钟执行一次自动化校验
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    @Override
    public void check() {

        MDC.put(SessionInterceptor.SESSION_KEY, TokenUtil.generateToken());

        /**
         *
         */
        if (!applicationPropertyConfig.isCheckConsistencyOn()) {
            return;
        }

        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        checkMgr();

        return;
    }

    /**
     * 主check MGR
     */
    private void checkMgr() {

        List<App> apps = appMgr.getAppList();
        List<EnvBO> envBOS = envMgr.getList();

        // app
        for (App app : apps) {

            checkAppConfigConsistency(app, envBOS);
        }
    }

    /**
     * 校验APP 一致性
     */
    private void checkAppConfigConsistency(App app, List<EnvBO> envBOS) {

        // env
        for (EnvBO envBO : envBOS) {

            // version
            List<String> versionList = configMgr.getVersionListByAppEnv(app.getId(), envBO.getId());

            for (String version : versionList) {

                checkAppEnvVersionConfigConsistency(app, envBO, version);
            }
        }
    }

    /**
     * 校验APP/ENV/VERSION 一致性
     */
    private void checkAppEnvVersionConfigConsistency(App app, EnvBO envBO, String version) {

        String monitorInfo = "monitor " + app.getName() + "\t" + envBO.getName() + "\t" + version;
        LOG.info(monitorInfo);

        //
        //
        //
        ConfListForm confiConfListForm = new ConfListForm();
        confiConfListForm.setAppId(app.getId());
        confiConfListForm.setEnvId(envBO.getId());
        confiConfListForm.setVersion(version);

        //
        //
        //
        DaoPageResult<ConfListVo> daoPageResult = configMgr.getConfigList(confiConfListForm, true, true);

        // 准备发送邮件通知
        String toEmails = appMgr.getEmails(app.getId());

        List<ConfListVo> confListVos = daoPageResult.getResult();

        List<String> errorList = new ArrayList<String>();
        for (ConfListVo confListVo : confListVos) {

            if (confListVo.getErrorNum() != 0) {

                List<ZkDisconfDataItem> zkDisconfDataItems = confListVo.getMachineList();
                for (ZkDisconfDataItem zkDisconfDataItem : zkDisconfDataItems) {

                    if (zkDisconfDataItem.getErrorList().size() != 0) {

                        String data = zkDisconfDataItem.toString() + "<br/><br/><br/><br/><br/><br/>original:" +
                                confListVo.getValue();

                        LOG.warn(data);

                        errorList.add(data + "<br/><br/><br/>");

                    }
                }
            }
        }

        if (errorList.size() != 0) {

            logMailBean.sendHtmlEmail(toEmails, " monitor ConfigConsistency ",
                    monitorInfo + "<br/><br/><br/>" + errorList.toString());
        }
    }
}
