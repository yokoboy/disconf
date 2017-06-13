package com.baidu.dsp.common.interceptor;

import com.baidu.dsp.common.constant.ErrorCode;
import com.baidu.dsp.common.vo.JsonObjectBase;
import com.baidu.dsp.common.vo.JsonObjectUtils;
import com.github.knightliao.apollo.utils.data.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author liaoqiqi
 * @version 2013-11-26
 */
public class WebCommonInterceptor extends HandlerInterceptorAdapter {

    protected static final Logger LOG = LoggerFactory.getLogger(WebCommonInterceptor.class);

    /**
     * 自定义的全局错误
     */
    protected void returnJsonSystemError(HttpServletRequest request, HttpServletResponse response, String message, ErrorCode errorCode) throws IOException {

        JsonObjectBase jsonObjectBase = JsonObjectUtils.buildGlobalError(message, errorCode);

        response.setHeader("Cache-Control", "no-cache");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(JsonUtils.toJson(jsonObjectBase));
    }
}
