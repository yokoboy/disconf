package com.baidu.disconf.web.service.user.aop;

import com.baidu.disconf.web.service.user.model.SigninForm;
import com.baidu.disconf.web.service.user.model.VisitorDTO;
import com.baidu.dsp.common.exception.FieldException;
import com.baidu.ub.common.commons.ThreadContext;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 判断用户是否具有请求方法的访问权
 */
@Aspect
public class AuthAspect {

    protected static final Logger LOG = LoggerFactory.getLogger(AuthAspect.class);

    @Pointcut(value = "execution(public * *(..))")
    public void anyPublicMethod() {
    }

    /**
     * 判断当前用户对访问的方法是否有权限
     */
    @Around("anyPublicMethod() && @annotation(auth)")
    public Object decideAccess(ProceedingJoinPoint pjp, Auth auth) throws Throwable {


        VisitorDTO visitorDTO = ThreadContext.getSessionVisitor();
        if (null == visitorDTO) {
            throw new FieldException(SigninForm.Name, "auth.not.login", null);
        }
        List<String> auths = visitorDTO.getAuths();

        String authValue = auth.value();
        if (StringUtils.isNotBlank(authValue)) {
            if (null == auths || auths.isEmpty() || !auths.contains(auth.value())) {
                throw new FieldException(SigninForm.Name, "auth.cant.opt", null);
            }
        } else {
            String envApp = pjp.getArgs()[0].toString(); // 这里写代码的时候必须符合决定(第一个参数必须是环境和appId)
            String optId = auth.optId();
            if (!auths.contains(envApp + "-" + optId)) {
                throw new FieldException(SigninForm.Name, "auth.cant.opt", null);
            }
        }

        try {
            // 执行方法
            return pjp.proceed();
        } catch (Throwable t) {
            LOG.info(t.getMessage());
            throw t;
        }
    }

}
