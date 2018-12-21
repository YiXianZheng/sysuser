package com.cloud.sysuser.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cloud.sysconf.common.basePDSC.BaseMybatisServiceImpl;
import com.cloud.sysconf.common.dto.HeaderInfoDto;
import com.cloud.sysconf.common.redis.RedisClient;
import com.cloud.sysconf.common.redis.RedisConfig;
import com.cloud.sysconf.common.utils.*;
import com.cloud.sysconf.common.utils.page.PageQuery;
import com.cloud.sysconf.common.utils.page.PageResult;
import com.cloud.sysconf.common.vo.ReturnVo;
import com.cloud.sysuser.common.DTO.*;
import com.cloud.sysuser.dao.SysUserDao;
import com.cloud.sysuser.po.SysUser;
import com.cloud.sysuser.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.jwt.express}")
    private String jwtexpress;

    @Value("${spring.jwt.id}")
    private String jwtid;

    @Autowired
    private RedisClient redisClient;

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

    @Override
    public ReturnVo userLogin(LoginFormDto loginForm, HeaderInfoDto headerInfoDto) {

        ReturnVo returnVo = new ReturnVo();

        SysUser user = sysUserDao.findByLoginName(loginForm.getLoginName());

        if (user == null) {
            returnVo.code = ReturnVo.FAIL;
            returnVo.responseCode = ResponseCode.LoginRegister.USER_NO_EXISTS;
        } else {
            String password = loginForm.getPassword() != null ? loginForm.getPassword() : "";
            if (!PassWordUtil.validatePassword(password, user.getPassword())) {
                returnVo.code = ReturnVo.FAIL;
                returnVo.responseCode = ResponseCode.LoginRegister.PWD_INPUT_ERROR;
            } else {
                String token = user.getToken();
                String newToken = JwtUtil.createToken(token, jwtexpress, jwtid);

                String name = user.getLoginName();

                Map<String, String> map = new HashMap<>();
                map.put("newToken", newToken);
                map.put("userId", user.getId());
                map.put("roleId", user.getRoleId());
                map.put("tokenUpdateTime", DateUtil.DateToString(new Date(), DateUtil.DATE_PATTERN_01));
                map.put("userName", StringUtils.isNotBlank(user.getName()) ? user.getName() : "");

                redisClient.SetHsetJedis(RedisConfig.USER_TOKEN_DB, token, map);

                logger.info("init login info to redis in db" + RedisConfig.USER_TOKEN_DB);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("t", newToken);
                jsonObject.put("userName",name);
                returnVo.code = ReturnVo.SUCCESS;
                returnVo.object = jsonObject;
            }
        }

        return returnVo;
    }

    @Override
    public ReturnVo addNewUser(LoginFormDto loginForm, HeaderInfoDto headerInfoDto) {

        ReturnVo returnVo = new ReturnVo();
        if (StringUtils.isBlank(loginForm.getLoginName())) {
            returnVo.code = ReturnVo.FAIL;
            returnVo.responseCode = ResponseCode.Parameter.MISSINGUSERNAME;
            return returnVo;
        }
        if(sysUserDao.findByLoginName(loginForm.getLoginName()) != null){
            returnVo.code = ReturnVo.FAIL;
            returnVo.responseCode = ResponseCode.LoginRegister.USER_EXIST;
            return returnVo;
        }
        SysUser sysUser = new SysUser();
        sysUser.setLoginName(loginForm.getLoginName());
        String password = loginForm.getPassword() != null ? loginForm.getPassword() : "123456";
        sysUser.setPassword(PassWordUtil.entryptPassword(password));
        // 设置创建时间和创建者
        sysUser.preUpdate(headerInfoDto.getCurUserId());
        //生成token
        String token = StringUtil.getToken();
        sysUser.setToken(token);
        sysUser.preInsert(headerInfoDto.getCurUserId(), headerInfoDto.getPanId());
        sysUserDao.add(sysUser);

        Map<String, String> map = new HashMap<>();
        map.put("id", sysUser.getId());
        map.put("loginName", sysUser.getLoginName());
        returnVo.code = ReturnVo.SUCCESS;
        returnVo.object = JSONObject.toJSON(map);
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
