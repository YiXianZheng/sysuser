package com.cloud.sysuser.provider;

import com.cloud.sysconf.common.vo.ApiResponse;
import com.cloud.sysuser.common.DTO.SysUserProviderDto;
import com.cloud.sysuser.provider.config.FeignConfigure;
import com.cloud.sysuser.provider.fallback.SysUserProviderFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * 提供供其它服务调用的接口
 * @Auther zyx
 * @Date 2018/7/9 15:23
 * @Description:
 */

//@EnableAspectJAutoProxy(proxyTargetClass = true)  //若需要AOP  此注解可能需要
@FeignClient(name = "sysuser", fallback = SysUserProviderFallback.class, decode404 = true,
                configuration = FeignConfigure.class)
public interface SysUserProvider {

    @RequestMapping(value = "/sys/user/updateToken", method = RequestMethod.POST)
    ApiResponse updateToken();
}
