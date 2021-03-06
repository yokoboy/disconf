package com.baidu.dsp.common.interceptor.login;

import com.baidu.disconf.web.constant.LoginConstant;
import com.baidu.disconf.web.service.user.constant.UserConstant;
import com.baidu.disconf.web.service.user.model.VisitorDTO;
import com.baidu.disconf.web.service.user.service.RedisLoginImpl;
import com.baidu.dsp.common.constant.ErrorCode;
import com.baidu.dsp.common.interceptor.WebCommonInterceptor;
import com.github.knightliao.apollo.utils.tool.TokenUtil;
import com.github.knightliao.apollo.utils.web.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 所有请求（一个Session可能会有多个请求）均会通过此拦截器
 * 只是用来判断Session中保存的用户信息是否存在
 *
 * @author liaoqiqi
 * @version 2013-11-28
 */
public class LoginInterceptor extends WebCommonInterceptor {

    protected static final Logger LOG = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private RedisLoginImpl redisLogin;

    private List<String> notJsonPathList;

    private List<String> notInterceptPathList;

    // Cookie域
    private String XONE_COOKIE_DOMAIN_STRING = "127.0.0.1";


    /**
     * 采用两级缓存。先访问session,<br/>
     * 如果存在，则直接使用，并更新 threadlocal <br/>
     * 如果不存在，则访问 redis，<br/>
     * 如果redis存在，则更新session和threadlocal<br/>
     * 如果redis也不存在，则认为没有登录
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//        if (handler instanceof HandlerMethod) {
//            HandlerMethod handlerMethod = (HandlerMethod) handler;
//            NoAuth noAuth = handlerMethod.getMethod().getAnnotation(NoAuth.class);
//            if (null == noAuth) {
//                return false;
//            }
//        }
        //
        // 判断session中是否有visitor
        //
        HttpSession session = request.getSession();
        VisitorDTO visitorDTO = (VisitorDTO) session.getAttribute(UserConstant.USER_KEY);

        //
        // 去掉不需拦截的path
        //
        String requestPath = request.getRequestURI();

        // 显示所有用户的请求
        LOG.info(request.getRequestURI());

        if (notInterceptPathList != null) {
            // 更精确的定位
            for (String path : notInterceptPathList) {
                if (requestPath.contains(path)) {
                    // 每次都更新session中的登录信息
                    redisLogin.updateSessionVisitor(session, visitorDTO);
                    return true;
                }
            }
        }

        /**
         * 种植Cookie
         */
        plantCookie(request, response);

        //
        // session中没有该信息,则从 redis上获取，并更新session的数据
        //
        if (visitorDTO == null) {
            VisitorDTO redisVisitorDTO = redisLogin.isLogin(request);
            //
            // 有登录信息
            //
            if (redisVisitorDTO == null) {
                // 还是没有登录
                returnJsonSystemError(request, response, "login.error", ErrorCode.LOGIN_ERROR);
                return false;
            }
        }


        // 每次都更新session中的登录信息
        redisLogin.updateSessionVisitor(session, visitorDTO);

        return true;
    }

    /**
     * 种植Cookie
     */
    private void plantCookie(HttpServletRequest request, HttpServletResponse response) {

        String xId = CookieUtils.getCookieValue(request, LoginConstant.XONE_COOKIE_NAME_STRING);

        // 没有Cookie 则生成一个随机的Cookie
        if (xId == null) {
            String cookieString = TokenUtil.generateToken();
            CookieUtils.setCookie(response, LoginConstant.XONE_COOKIE_NAME_STRING, cookieString, XONE_COOKIE_DOMAIN_STRING, LoginConstant.XONE_COOKIE_AGE);
        }
    }

    /**
     * @return the notJsonPathList
     */
    public List<String> getNotJsonPathList() {
        return notJsonPathList;
    }

    /**
     * @param notJsonPathList the notJsonPathList to set
     */
    public void setNotJsonPathList(List<String> notJsonPathList) {
        this.notJsonPathList = notJsonPathList;
    }

    /**
     * @return the notInterceptPathList
     */
    public List<String> getNotInterceptPathList() {
        return notInterceptPathList;
    }

    /**
     * @param notInterceptPathList the notInterceptPathList to set
     */
    public void setNotInterceptPathList(List<String> notInterceptPathList) {
        this.notInterceptPathList = notInterceptPathList;
    }

    public String getXONE_COOKIE_DOMAIN_STRING() {
        return XONE_COOKIE_DOMAIN_STRING;
    }

    public void setXONE_COOKIE_DOMAIN_STRING(String xONE_COOKIE_DOMAIN_STRING) {
        XONE_COOKIE_DOMAIN_STRING = xONE_COOKIE_DOMAIN_STRING;
    }

}
