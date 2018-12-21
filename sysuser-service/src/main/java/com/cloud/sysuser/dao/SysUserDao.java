package com.cloud.sysuser.dao;

import com.cloud.sysconf.common.basePDSC.BaseMybatisDao;
import com.cloud.sysuser.po.SysUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;

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

    /**
     * 通过登陆名和盘口ID查找用户
     * @param loginName
     * @return
     */
    SysUser findByLoginName(@Param("loginName")String loginName);

    /**
     * 检查工号是否存在
     * @param no
     * @return
     */
    Integer checkExistNo(@Param("no") String no);

    /**
     * 通过token查询
     * @param token
     */
    void findByToken(@Param("token") String token);

    /**
     * 用户注册
     * @param sysUser
     */
    int add(@RequestBody SysUser sysUser);
}
