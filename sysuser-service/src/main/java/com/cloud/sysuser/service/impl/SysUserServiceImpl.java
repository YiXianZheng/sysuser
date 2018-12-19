package com.cloud.sysuser.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cloud.sysconf.common.basePDSC.BaseMybatisServiceImpl;
import com.cloud.sysconf.common.dto.HeaderInfoDto;
import com.cloud.sysconf.common.utils.*;
import com.cloud.sysconf.common.utils.page.PageQuery;
import com.cloud.sysconf.common.utils.page.PageResult;
import com.cloud.sysconf.common.vo.ReturnVo;
import com.cloud.sysuser.common.DTO.*;
import com.cloud.sysuser.dao.SysUserDao;
import com.cloud.sysuser.po.SysUser;
import com.cloud.sysuser.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @Auther zyx
 * @Date 2018/7/5 16:15
 * @Description:
 */
@Service
public class SysUserServiceImpl extends BaseMybatisServiceImpl<SysUser, String, SysUserDao> implements SysUserService {

    @Autowired
    private SysUserDao sysUserDao;

    @Override
    public ReturnVo getUserInfo(String userId) {
        ReturnVo returnVo = new ReturnVo();

        SysUser sysUser = sysUserDao.findById(userId);

        if (sysUser == null) {
            returnVo.code = ReturnVo.FAIL;
            returnVo.responseCode = ResponseCode.LoginRegister.NOLOGIN;
        } else {
            returnVo.code = ReturnVo.SUCCESS;
            SysUserInfoDto sysUserInfoDto = new SysUserInfoDto();
            BeanUtils.copyProperties(sysUser, sysUserInfoDto);
            returnVo.object = sysUserInfoDto;
        }
        return returnVo;
    }

    @Override
    public ReturnVo updateLoginFlag(String id, Integer loginFlag, String curUserId) {
        ReturnVo returnVo = new ReturnVo();
        returnVo.code = ReturnVo.FAIL;
        SysUser sysUser = sysUserDao.findById(id);

        if(sysUser != null){
            sysUser.setLoginFlag(loginFlag);
            sysUser.preUpdate(curUserId);
            sysUserDao.update(sysUser);
            returnVo.code = ReturnVo.SUCCESS;
        }
        return returnVo;
    }

    @Override
    public ReturnVo deleteUesr(String userId, String curUserId) {
        ReturnVo returnVo = new ReturnVo();
        returnVo.code = ReturnVo.FAIL;
        SysUser sysUser = sysUserDao.findById(userId);

        if(sysUser != null){
            sysUser.setDelFlag(SysUser.DEL_FLAG_ALREADY);
            sysUser.preUpdate(curUserId);
            sysUserDao.deleteUser(sysUser);
            returnVo.code = ReturnVo.SUCCESS;
        }
        return returnVo;
    }

    @Override
    public ReturnVo listForTablePage(PageQuery pageQuery, HeaderInfoDto headerInfoDto) {

        ReturnVo returnVo = new ReturnVo();
        try {
            PageResult pageResult = this.queryForTablePage(pageQuery.getPageIndex(), pageQuery.getPageSize(), pageQuery.getParams());
            List<SysUserListDto> merchantList = initSysUserInfo(pageResult.getData());

            pageResult.setData(merchantList);
            returnVo.code = ReturnVo.SUCCESS;
            returnVo.object = JSONObject.toJSON(pageResult);
        }catch (Exception e){
            returnVo.code = ReturnVo.ERROR;
            returnVo.responseCode = ResponseCode.Base.ERROR;
        }
        return returnVo;
    }

    /**
     * 初始化系统账号信息
     * @param userList
     * @return
     */
    private List<SysUserListDto> initSysUserInfo(List<SysUser> userList){
        List<SysUserListDto> agentList = new ArrayList<>();
        for (SysUser sysUser : userList) {
            SysUserListDto sysUserListDto = new SysUserListDto();
            BeanUtils.copyProperties(sysUser, sysUserListDto);

            agentList.add(sysUserListDto);
        }
        return agentList;
    }
}
