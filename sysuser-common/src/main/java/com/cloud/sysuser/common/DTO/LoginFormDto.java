package com.cloud.sysuser.common.DTO;

import com.cloud.sysconf.common.basePDSC.BaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther zyx
 * @Date 2018/7/6 14:03
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class LoginFormDto extends BaseDto {

    private String loginName;

    private String password;
}
