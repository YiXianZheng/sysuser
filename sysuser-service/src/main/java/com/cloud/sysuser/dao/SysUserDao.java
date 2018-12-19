package com.cloud.sysuser.dao;

import com.cloud.sysconf.common.basePDSC.BaseMybatisDao;
import com.cloud.sysuser.po.SysUser;
import org.apache.ibatis.annotations.Param;

/**
 * @Auther zyx
 * @Date 2018/7/6 10:41
 * @Description:
 */
public interface SysUserDao extends BaseMybatisDao<SysUser, String> {

    /**
     * 通过id和panid查找用户
     * @param id
     * @return
     */
    SysUser findById(@Param("id") String id);

    /**
     * 逻辑删除
     * @param sysUser
     */
    void deleteUser(SysUser sysUser);
}
