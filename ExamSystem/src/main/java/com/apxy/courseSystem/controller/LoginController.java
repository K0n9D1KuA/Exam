package com.apxy.courseSystem.controller;


import com.alibaba.fastjson.JSON;
import com.apxy.courseSystem.annotation.SystemLog;
import com.apxy.courseSystem.constant.AuthServerConstant;
import com.apxy.courseSystem.entity.MemberEntity;
import com.apxy.courseSystem.entity.security.LoginUser;
import com.apxy.courseSystem.entity.vo.MenuVo;
import com.apxy.courseSystem.entity.vo.UserInfoVo;
import com.apxy.courseSystem.entity.vo.UserVo;
import com.apxy.courseSystem.enuem.LoginEnuem;
import com.apxy.courseSystem.exception.LoginException;
import com.apxy.courseSystem.service.MemberService;


import com.apxy.courseSystem.service.MenuService;
import com.apxy.courseSystem.util.usingUtil.JwtUtil;
import com.apxy.courseSystem.util.usingUtil.R;
import com.apxy.courseSystem.util.usingUtil.SpringSecurityUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 权限相关接口
 */
@RestController
@RequestMapping("courseSystem/auth")
public class LoginController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MenuService menuService;
    @Autowired
    private SpringSecurityUtil springSecurityUtil;


    /**
     * 登录接口
     *
     * @param userVo 用户信息 包括账号密码
     * @return
     */
    @SystemLog(businessName = "登录")
    @PostMapping("/login")
    public R login(@RequestBody @Valid UserVo userVo, BindingResult bindingResult) {
        System.out.println(passwordEncoder.encode("123456"));
        //校验用户名和密码是否有错误
        if (bindingResult.hasErrors()) {
            //讲出错的字段返回  数据格式有问题
            Map<String, String> map = bindingResult.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return R.error(LoginEnuem.VALID_ERROR.getCode(), LoginEnuem.VALID_ERROR.getMsg()).put("data", map);
        }
        //springSecurity进行账户密码的校验  同时查询其权限信息
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userVo.getUsername(), userVo.getPassword());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        //认证失败
        if (Objects.isNull(authenticate)) {
            //抛出密码错误
            throw new LoginException(LoginEnuem.WRONG_PASSWORD.getCode(), LoginEnuem.WRONG_PASSWORD.getMsg());
        }
        //认证成功
        //生成一个随机的uuid 然后存入redis中  key是user-login-uuid value是用户的信息
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        System.out.println("本次生成出来的uuid：" + uuid);
        String token = JwtUtil.createJWT(uuid);
        String key = AuthServerConstant.LOGIN_USER + uuid;
        String jsonString = JSON.toJSONString(loginUser);
        stringRedisTemplate.opsForValue().set(key, jsonString);
        stringRedisTemplate.expire(key, 1000, TimeUnit.MINUTES);//设置过期时间  100 min
        UserInfoVo userInfoVo = new UserInfoVo();
        MemberEntity memberEntity = loginUser.getMemberEntity();
        //根据用户id查询其对应的角色
        String role = memberService.getRoleById(memberEntity.getId());
        userInfoVo.setRole(role);
        //属性拷贝
        BeanUtils.copyProperties(memberEntity, userInfoVo);
        if ("student".equals(role)) {
            //说明登录用户是学生  那么不需要返回菜单信息
            return R.ok().put("token", token).put("userInfo", userInfoVo);
        } else {
            //说明登录用户是老师或者管理员 那么需要返回后台菜单
            List<MenuVo> treeMenu = menuService.createTreeMenu(loginUser.getMemberEntity().getMenuEntities());
            System.out.println(passwordEncoder.encode("admin"));
            System.out.println("该用户的菜单是" + treeMenu);
            return R.ok().put("menu", treeMenu).put("token", token).put("userInfo", userInfoVo);
        }
    }

    /**
     * 注销接口
     */
    @SystemLog(businessName = "退出登录")
    @GetMapping("/logout")
    public R logout(HttpServletRequest httpServletRequest) throws Exception {
        String token = httpServletRequest.getHeader("token");
        //解析uuid
        Claims claims = JwtUtil.parseJWT(token);
        String uuid = claims.getSubject();
        System.out.println("本次解析出来的uuid是");
        //删除redis中的用户信息
        String key = AuthServerConstant.LOGIN_USER + uuid;
        stringRedisTemplate.delete(key);
        UserInfoVo userInfoVo = new UserInfoVo();
        LoginUser loginUser = springSecurityUtil.getUser();
        MemberEntity memberEntity = loginUser.getMemberEntity();
        //根据用户id查询其对应的角色
        //属性拷贝
        BeanUtils.copyProperties(memberEntity, userInfoVo);
        return R.ok().put("userInfo", userInfoVo);
    }
}
