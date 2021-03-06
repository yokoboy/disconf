package com.baidu.disconf.web.service.config.service.impl;

import com.baidu.disconf.web.service.config.bo.ConfigHistory;
import com.baidu.disconf.web.service.config.dao.ConfigHistoryDao;
import com.baidu.disconf.web.service.config.service.ConfigHistoryMgr;
import com.baidu.disconf.web.service.user.model.VisitorDTO;
import com.baidu.dsp.common.constant.DataFormatConstants;
import com.baidu.ub.common.commons.ThreadContext;
import com.github.knightliao.apollo.utils.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by knightliao on 15/12/25.
 */
@Service
public class ConfigHistoryMgrImpl implements ConfigHistoryMgr {

    @Autowired
    private ConfigHistoryDao configHistoryDao;

    @Override
    public void createOne(Long configId, String oldValue, String newValue) {

        ConfigHistory configHistory = new ConfigHistory();

        configHistory.setConfigId(configId);
        configHistory.setOldValue(oldValue);
        configHistory.setNewValue(newValue);

        VisitorDTO visitorDTO = ThreadContext.getSessionVisitor();
        if (visitorDTO != null) {
            configHistory.setUpdateBy(visitorDTO.getId());
        }

        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);
        configHistory.setCreateTime(curTime);

        configHistoryDao.create(configHistory);
    }
}
