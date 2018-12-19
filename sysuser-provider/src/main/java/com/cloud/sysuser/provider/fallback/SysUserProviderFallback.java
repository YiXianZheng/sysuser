package com.cloud.sysuser.provider.fallback;

import com.cloud.sysconf.common.utils.ResponseCode;
import com.cloud.sysconf.common.vo.ApiResponse;
import com.cloud.sysuser.common.DTO.SysUserProviderDto;
import com.cloud.sysuser.provider.SysUserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Auther zyx
 * @Date 2018/7/17 14:53
 * @Description:
 */
@Component
public class SysUserProviderFallback implements SysUserProvider {

    private final Logger log = LoggerFactory.getLogger(SysUserProviderFallback.class);

    @Override
    public ApiResponse updateToken() {
        log.error("========= >> sys/user/updateToken 接口调用异常");

        return ApiResponse.creatFail(ResponseCode.Base.SYSTEM_ERR);
    }
}
