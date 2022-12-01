package com.apxy.courseSystem.service.impl;

import com.alibaba.excel.EasyExcel;
import com.apxy.courseSystem.constant.AuthServerConstant;
import com.apxy.courseSystem.dao.MemberDao;
import com.apxy.courseSystem.entity.*;
import com.apxy.courseSystem.entity.event.MemberEvent;
import com.apxy.courseSystem.entity.excel.Member;
import com.apxy.courseSystem.entity.vo.MemberVo;
import com.apxy.courseSystem.excelLisenner.StudentExcelLisener;
import com.apxy.courseSystem.service.MemberService;
import com.apxy.courseSystem.service.RoleService;
import com.apxy.courseSystem.service.RoleUserService;
import com.apxy.courseSystem.util.Constant;
import com.apxy.courseSystem.util.SpringSecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.apxy.courseSystem.util.PageUtils;
import com.apxy.courseSystem.util.Query;
import org.springframework.transaction.annotation.Transactional;

@Service("studentService")
@Transactional
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    //邮件发送人 从配置文件中读取
    @Value("${spring.mail.username}")
    private String from;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleUserService roleUserService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private StudentTeacherServiceImpl studentTeacherService;
    @Autowired
    private SpringSecurityUtil springSecurityUtil;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void sendCode(String email, String subject, String context) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        //从哪里发？
        simpleMailMessage.setFrom(from);
        //发送给谁
        simpleMailMessage.setTo(email);
        //标题
        simpleMailMessage.setSubject(subject);
        //内容
        simpleMailMessage.setText(context);
        //执行真正的发邮件操作
        mailSender.send(simpleMailMessage);
    }

    /**
     * 根据用户id  获得用户的角色信息
     *
     * @param id
     * @return
     */
    @Override
    public String getRoleById(Integer id) {
        MemberDao baseMapper = this.baseMapper;
        String role = baseMapper.getRoleById(id);
        return role;
    }

    /**
     * 开启事务
     *
     * @param cachedDataList
     */

    @Override
    public void saveStudentByExcel(List<Member> cachedDataList) {
        List<MemberEntity> memberEntityList = cachedDataList.stream().map(o -> {
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setUsreName(o.getUserName());
            memberEntity.setPassword(passwordEncoder.encode(o.getPassword()));
            memberEntity.setSex("男".equals(o.getSex()) ? 1 : 0);
            memberEntity.setMemberName(o.getMemberName());
            memberEntity.setProfession(o.getProfession());
            memberEntity.setAvatar(AuthServerConstant.DEFAULT_AVATAR_URL);
            return memberEntity;
        }).collect(Collectors.toList());
        this.saveBatch(memberEntityList);
        //批量绑定学生与角色的关系
        List<RoleUser> roleUsers = memberEntityList.stream().map(o -> {
            RoleUser roleUser = new RoleUser();
            //获得角色id
            Long roleId = roleService.getOne(new LambdaQueryWrapper<Role>().eq(Role::getRole, AuthServerConstant.STUDENT_ROLE_NAME)).getId();
            roleUser.setRoleId(roleId);
            roleUser.setUserId(o.getId().longValue());
            return roleUser;
        }).collect(Collectors.toList());
        roleUserService.saveBatch(roleUsers);
        /**
         * 批量绑定学生与老师之间的关系
         */
        //查出老师角色的id
        Long id = roleService.getOne(new LambdaQueryWrapper<Role>().eq(Role::getRole, AuthServerConstant.TEACHER_ROLE_NAME)).getId();
        List<StudentTeacherEntity> saveTeachers = new ArrayList<>();
        //查出所有的老师
        List<MemberEntity> teachers = this.list(new LambdaQueryWrapper<MemberEntity>().in(MemberEntity::getId, roleUserService.list(new LambdaQueryWrapper<RoleUser>().eq(RoleUser::getRoleId, id)).stream().map(RoleUser::getUserId).collect(Collectors.toList())));
        for (int i = 0; i < cachedDataList.size(); i++) {
            Member currentMember = cachedDataList.get(i);
            String[] teacherNames = currentMember.getTeacher().split(";");
            System.out.println(Arrays.toString(teacherNames));
            List<String> teacherName = Arrays.asList(teacherNames);
            List<MemberEntity> teacher = teachers.stream().filter(o -> {
                return teacherName.contains(o.getMemberName());
            }).collect(Collectors.toList());
            System.out.println("..........................");
            teacher.forEach(System.out::println);
            Long studentId = memberEntityList.get(i).getId().longValue();
            //获得所有老师id
            List<Integer> teacherIds = teacher.stream().map(MemberEntity::getId).collect(Collectors.toList());
            teacherIds.forEach(o -> {
                StudentTeacherEntity studentTeacherEntity = new StudentTeacherEntity();
                studentTeacherEntity.setTeacherId(o.longValue());
                studentTeacherEntity.setStudentId(studentId);
                saveTeachers.add(studentTeacherEntity);
            });
        }
        //批量插入
        studentTeacherService.saveBatch(saveTeachers);
    }

    @Override
    public void saveTeacherByExcel(List<Member> cachedDataList) {
        List<MemberEntity> memberEntityList = cachedDataList.stream().map(o -> {
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setUsreName(o.getUserName());
            memberEntity.setPassword(passwordEncoder.encode(o.getPassword()));
            memberEntity.setSex("男".equals(o.getSex()) ? 1 : 0);
            memberEntity.setMemberName(o.getMemberName());
            memberEntity.setProfession(o.getProfession());
            memberEntity.setAvatar(AuthServerConstant.DEFAULT_AVATAR_URL);
            return memberEntity;
        }).collect(Collectors.toList());
        this.saveBatch(memberEntityList);
        //批量绑定老师与角色的关系
        List<RoleUser> roleUsers = memberEntityList.stream().map(o -> {
            RoleUser roleUser = new RoleUser();
            //获得角色id
            Long roleId = roleService.getOne(new LambdaQueryWrapper<Role>().eq(Role::getRole, AuthServerConstant.TEACHER_ROLE_NAME)).getId();
            roleUser.setRoleId(roleId);
            roleUser.setUserId(o.getId().longValue());
            return roleUser;
        }).collect(Collectors.toList());
        roleUserService.saveBatch(roleUsers);

    }


    /**
     * 获得当前老师的所有学生
     *
     * @return
     */
    @Override
    public List<MemberEntity> getAllStudents() {
        Long teacherId = Long.valueOf(springSecurityUtil.getUser().getMemberEntity().getId());
        List<StudentTeacherEntity> studentTeacherEntities = studentTeacherService.list(new LambdaQueryWrapper<StudentTeacherEntity>().eq(StudentTeacherEntity::getTeacherId, teacherId));
        //健壮性判断
        if (studentTeacherEntities != null && studentTeacherEntities.size() > 0) {
            //获得所有学生
            return this.list(new LambdaQueryWrapper<MemberEntity>().in(MemberEntity::getId, studentTeacherEntities.stream().map(StudentTeacherEntity::getStudentId).collect(Collectors.toList())));
        } else {
            return null;
        }
    }

    @Override
    public PageUtils queryAllStudents(Map<String, Object> params) {
        Long teacherId = Long.valueOf(springSecurityUtil.getUser().getMemberEntity().getId());
        List<StudentTeacherEntity> studentTeacherEntities = studentTeacherService.list(new LambdaQueryWrapper<StudentTeacherEntity>().eq(StudentTeacherEntity::getTeacherId, teacherId));
        if (studentTeacherEntities != null && studentTeacherEntities.size() > 0) {
            //获得所有学生
            return new PageUtils(this.page(new Query<MemberEntity>().getPage(params), new LambdaQueryWrapper<MemberEntity>().in(MemberEntity::getId, studentTeacherEntities.stream().map(StudentTeacherEntity::getStudentId).collect(Collectors.toList()))));
        } else {
            return null;
        }

    }

    @Override
    public PageUtils queryAllStudentsWithTeacher(Map<String, Object> params) {
//        IPage<MemberEntity> page = this.page(
//                new Query<MemberEntity>().getPage(params),
//                new QueryWrapper<MemberEntity>()
//        );

//        if(params.get(Constant.PAGE) != null){
//            curPage = Long.parseLong((String)params.get(Constant.PAGE));
//        }
//        if(params.get(Constant.LIMIT) != null){
//            limit = Long.parseLong((String)params.get(Constant.LIMIT));
//        }
//        IPage<MemberVo> ret = new Page<>();
//        ret.setCurrent(Long.parseLong((String) params.get(Constant.PAGE)));
//        ret.setSize(Long.parseLong((String) params.get("pageSize")));
//        List<MemberVo> retList = this.baseMapper.getAllStudentsWithTeacher();
//        ret.setRecords(retList);
//        ret.setTotal(retList.size());
//        ret.setPages(retList.size() / ret.getSize() + 1);
//        return new PageUtils(ret);
        Page<MemberVo> ret = new Page<>(Long.parseLong((String) params.get(Constant.PAGE)), Long.parseLong((String) params.get("pageSize")));
        IPage<MemberVo> result = this.baseMapper.getAllStudentsWithTeacher(ret);
        return new PageUtils(result);

    }

    @Override
    public PageUtils getAllTeacher(Map<String, Object> params) {
        //获得所有的老师id
        Page<MemberEntity> ret = new Page<>(Long.parseLong((String) params.get(Constant.PAGE)), Long.parseLong((String) params.get("pageSize")));

        IPage<MemberEntity> result = this.baseMapper.getTeachers(ret);
        return new PageUtils(result);
    }


    /**
     * 监听用户头像上传
     */
    @Async
    @EventListener(MemberEvent.class)
    public void doEvent(MemberEvent memberEvent) {
        this.updateById(memberEvent.getMemberEntity());
    }

}
