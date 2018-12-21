package com.cloud.sysuser.controller;

import com.cloud.sysconf.common.basePDSC.BaseController;
import com.cloud.sysconf.common.dto.HeaderInfoDto;
import com.cloud.sysconf.common.utils.ResponseCode;
import com.cloud.sysconf.common.utils.StringUtil;
import com.cloud.sysconf.common.utils.page.PageQuery;
import com.cloud.sysconf.common.vo.ApiResponse;
import com.cloud.sysconf.common.vo.ReturnVo;
import com.cloud.sysuser.common.DTO.LoginFormDto;
import com.cloud.sysuser.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController {

    @Value("${sys.default.password}")
    private String DEFAULT_PASSWORD;

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/register")
    public ApiResponse userRegister(@RequestBody LoginFormDto loginFormDto, @RequestHeader HttpHeaders headers) {

        HeaderInfoDto headerInfoDto = this.getHeaderInfo(headers);

        try {
            ReturnVo returnVo = sysUserService.addNewUser(loginFormDto, headerInfoDto);
            return toApiResponse(returnVo);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.creatFail(ResponseCode.Base.SYSTEM_ERR);
        }
    }

    /**
     * 用户登录接口
     * @param loginForm
     * @param headers
     * @return
     */
    @PostMapping(value = "/login")
    public ApiResponse userLogin(@RequestBody LoginFormDto loginForm, @RequestHeader HttpHeaders headers) {

        HeaderInfoDto headerInfoDto = this.getHeaderInfo(headers);

        if(StringUtils.isBlank(loginForm.getLoginName())){
            return ApiResponse.creatFail(ResponseCode.Parameter.MISSINGUSERNAME);
        }
        if(StringUtils.isBlank(loginForm.getPassword())){
            return ApiResponse.creatFail(ResponseCode.Parameter.MISSINGPASSWORD);
        }

        try{
            ReturnVo returnVo = sysUserService.userLogin(loginForm, headerInfoDto);
            return this.toApiResponse(returnVo);
        } catch (Exception e){
            e.printStackTrace();
            return ApiResponse.creatFail(ResponseCode.Base.SYSTEM_ERR);
        }
    }

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    @PostMapping("/infoForProvider")
    public ApiResponse infoForProvider(@RequestParam(required = true) String userId){
        try{
            if(StringUtil.isNotBlank(userId)){
                ReturnVo returnVo = sysUserService.getUserInfo(userId);
                return this.toApiResponse(returnVo);
            }else{
                return ApiResponse.creatFail(ResponseCode.Parameter.LACK);
            }
        } catch (Exception e){
            e.printStackTrace();
            return ApiResponse.creatFail(ResponseCode.Base.SYSTEM_ERR);
        }
    }

    /**
     * 更新系统账号登录权限
     * @param userId
     * @param loginFlag
     * @param curUserId
     * @return
     */
    @PostMapping("/updateLoginFlagForProvider")
    public ApiResponse updateLoginFlagForProvider(@RequestParam String userId, @RequestParam Integer loginFlag,
                                                  @RequestParam String curUserId){

        ReturnVo returnVo = sysUserService.updateLoginFlag(userId, loginFlag, curUserId);
        return this.toApiResponse(returnVo);
    }

    /**
     * 删除系统账号
     * @param userId
     * @param curUserId
     * @return
     */
    @PostMapping("/deleteForProvider")
    public ApiResponse deleteForProvider(@RequestParam String userId, @RequestParam String curUserId){

        ReturnVo returnVo = sysUserService.deleteUesr(userId, curUserId);
        return this.toApiResponse(returnVo);
    }

    /**
     * 分页搜索系统用户 适用于列表分页
     * @param pageQuery
     * @param headers
     * @return
     */
    @PostMapping("/tablePage")
    public ApiResponse tablePageForAgent(@RequestBody PageQuery pageQuery, @RequestHeader HttpHeaders headers){
        HeaderInfoDto headerInfoDto = this.getHeaderInfo(headers);
        try{
            ReturnVo returnVo = sysUserService.listForTablePage(pageQuery, headerInfoDto);
            return this.toApiResponse(returnVo);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.creatFail(ResponseCode.Base.SYSTEM_ERR);
        }
    }
}
