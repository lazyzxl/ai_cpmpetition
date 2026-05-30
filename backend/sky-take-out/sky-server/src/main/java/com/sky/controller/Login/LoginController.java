package com.sky.controller.Login;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserForgetPasswordDTO;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.entity.UserRegister;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param userLoginDTO
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);

        User user = userService.login(userLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        UserVO userVO = UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .token(token)
                .build();

        return Result.success(userVO);
    }
    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }
    @PostMapping("login/register")
    @ApiOperation(value = "用户注册")
    public Result register(@RequestBody UserRegister userRegister) {
        log.info("用户注册{}", userRegister);
        userService.save(userRegister);
        return Result.success();
    }
    @PutMapping("/forgetPassword")
    @ApiOperation("忘记密码")
    public Result forgetPassword(@RequestBody UserForgetPasswordDTO userForgetPasswordDTO) {
        log.info("{}忘记密码", userForgetPasswordDTO);
        userService.forgetPassword(userForgetPasswordDTO);
        return Result.success();
    }
}
