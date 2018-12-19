package com.cloud.sysuser.service;

import com.cloud.sysconf.common.basePDSC.BaseMybatisService;
import com.cloud.sysconf.common.dto.HeaderInfoDto;
import com.cloud.sysconf.common.utils.page.PageQuery;
import com.cloud.sysconf.common.vo.ReturnVo;
import com.cloud.sysuser.common.DTO.LoginFormDto;
import com.cloud.sysuser.po.SysUser;

/**
 * @Auther zyx
 * @Date 2018/7/5 16:13
 * @Description:
 */
public interface SysUserService extends BaseMybatisService<SysUser, String> {

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    ReturnVo getUserInfo(String userId);

    /**
     * 更新系统账号登录权限
     * @param id
     * @param loginFlag
     * @param curUserId
     * @return
     */
    ReturnVo updateLoginFlag(String id, Integer loginFlag, String curUserId);

    /**
     * 逻辑删除管理账号
     * @param userId
     * @param curUserId
     * @return
     */
    ReturnVo deleteUesr(String userId, String curUserId);

    /**
     * 分页获取系统用户   适用于列表型的分页
     * @param pageQuery  分页条件
     * @param headerInfoDto
     * @return
     */
    ReturnVo listForTablePage(PageQuery pageQuery, HeaderInfoDto headerInfoDto);
}

