package com.apxy.courseSystem.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.apxy.courseSystem.constant.AuthServerConstant;
import com.apxy.courseSystem.constant.PaperConstant;
import com.apxy.courseSystem.constant.SubjectConstant;
import com.apxy.courseSystem.constant.WrongSubjectConstant;
import com.apxy.courseSystem.entity.*;
import com.apxy.courseSystem.entity.redis.WorseSubject;
import com.apxy.courseSystem.entity.vo.DonePaperVo;
import com.apxy.courseSystem.entity.vo.PaperVo;
import com.apxy.courseSystem.entity.vo.PaperVoEntity;
import com.apxy.courseSystem.entity.vo.SubjectVoEntity;
import com.apxy.courseSystem.service.*;
import com.apxy.courseSystem.util.R;
import com.apxy.courseSystem.util.SpringSecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.apxy.courseSystem.dao.PaperDao;

import com.apxy.courseSystem.util.PageUtils;
import com.apxy.courseSystem.util.Query;

@Service("paperService")
public class PaperServiceImpl extends ServiceImpl<PaperDao, PaperEntity> implements PaperService {

    @Autowired
    private SubjectPaperService subjectPaperService;
    @Autowired
    private SpringSecurityUtil springSecurityUtil;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DonePaperService donePaperService;
    @Autowired
    private DoneSubjectService doneSubjectService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private WrongSubjectService wrongSubjectService;
    @Autowired
    private StudentTeacherService studentTeacherService;


    /**
     * 获得当前试卷
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PaperEntity> page = this.page(
                new Query<PaperEntity>().getPage(params),
                new QueryWrapper<PaperEntity>()
        );
        List<PaperEntity> records = page.getRecords();
        records = records.stream().filter(o -> {
            return o.getTeacherId().intValue() == springSecurityUtil.getUser().getMemberEntity().getId();
        }).collect(Collectors.toList());
        page.setRecords(records);
        return new PageUtils(page);
    }

    /**
     * 添加试卷
     *
     * @param paperVoEntity 要添加的试卷
     */
    @Override
    public void addPaper(PaperVoEntity paperVoEntity) {
        //添加试卷
        //首先插入试卷
        PaperEntity paperEntity = new PaperEntity();
        BeanUtils.copyProperties(paperVoEntity, paperEntity);
        //出题老师id
        paperEntity.setTeacherId(this.getMemberEntity().getId().longValue());
        this.save(paperEntity);
        //更新试卷和题目之间的关系
        //获得试卷id
        Long id = paperEntity.getId().longValue();
        List<SubjectPaper> subjectPaperList = Arrays.stream(paperVoEntity.getSelectedSubjects()).map(o -> {
            SubjectPaper subjectPaper = new SubjectPaper();
            subjectPaper.setSubjectId(o);
            subjectPaper.setPaperId(id);
            return subjectPaper;
        }).collect(Collectors.toList());
        subjectPaperService.saveBatch(subjectPaperList);
    }


    /**
     * 根据试卷id 获得该试卷下的所有题目
     *
     * @param id 试卷id
     * @return 该试卷下的所有题目
     */
    @Override
    public List<SubjectVoEntity> doPaper(Long id) {

        //获取当前试卷
        PaperEntity paperEntity = this.getById(id);
        //首先根据试卷id 获得所有的试卷题目关联类(SubjectPaper）
        List<SubjectPaper> subjectPapers = subjectPaperService.list(new LambdaQueryWrapper<SubjectPaper>().eq(SubjectPaper::getPaperId, id));
        //健壮性判断
        if (subjectPapers != null && subjectPapers.size() > 0) {
            //获得所有的题目id
            List<Long> subjectIds = subjectPapers.stream().map(SubjectPaper::getSubjectId).collect(Collectors.toList());
            //健壮性判断
            if (subjectIds.size() > 0) {
                //获得所有的题目
                List<SubjectEntity> subjectEntities = subjectService.list(new LambdaQueryWrapper<SubjectEntity>().in(SubjectEntity::getId, subjectIds));
                //改变题目的顺序 题目顺序： 判断题 单选题 多选题 填空题 大题
                List<SubjectVoEntity> subjectVoEntities = this.changeSubjectOrder(subjectEntities);
                //将所有的题目存储到redis中  采用的数据结构为list
                String key = this.getKey();
                subjectVoEntities.forEach(o -> {
                    stringRedisTemplate.opsForList().rightPush(key, JSON.toJSONString(o));
                });
                //设置过期时间
                stringRedisTemplate.expire(key, paperEntity.getTotalTime() + 1, TimeUnit.MINUTES);
                //返回所有题目 并且告知前端可以答题
                return subjectVoEntities;
            }
        }
        return null;
    }

    /**
     * 修改题目 并且获得下一题
     *
     * @param subjectVoEntity
     * @param index
     * @return
     */
    @Override
    public SubjectVoEntity getNextSubject(SubjectVoEntity subjectVoEntity, Long index) {
        //如果该题目是多选题 那么需要将其答案['A','B','C'] 转化为ABC
        if (subjectVoEntity.getSelectedChoice() != null && subjectVoEntity.getSelectedChoice().length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < subjectVoEntity.getSelectedChoice().length; i++) {
                sb.append(subjectVoEntity.getSelectedChoice()[i]);
            }
            String ret = sb.toString();
            subjectVoEntity.setSelectAnswer(new String(ret));
        }
        String key = this.getKey();
        this.changeSubject(index - 1, subjectVoEntity);
        Long size = stringRedisTemplate.opsForList().size(key);
        if (index.intValue() == size.intValue()) {
            //说明已经是最后一道题了 那么就返回当前对象即可
            return subjectVoEntity;
        }
        return JSON.parseObject(stringRedisTemplate.opsForList().index(key, index), SubjectVoEntity.class);
    }

    /**
     * 修改题目 并且获得上一题
     *
     * @param subjectVoEntity 当前题号所对应的题目
     * @param index           当前题号（题号与索引的关系是   题号=索引+1）
     * @return 当前题号的下一题
     */
    @Override
    public SubjectVoEntity getLastSubject(SubjectVoEntity subjectVoEntity, Long index) {
        //如果该题目是多选题 那么需要将其答案['A','B','C'] 转化为ABC
        if (subjectVoEntity.getSelectedChoice() != null && subjectVoEntity.getSelectedChoice().length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < subjectVoEntity.getSelectedChoice().length; i++) {
                sb.append(subjectVoEntity.getSelectedChoice()[i]);
            }
            String ret = sb.toString();
            subjectVoEntity.setSelectAnswer(new String(ret));
        }
        String key = AuthServerConstant.PAPER_PREFIX_KEY + this.getUserName();
        this.changeSubject(index - 1, subjectVoEntity);
        return this.getOneSubjectByIndex(index - 2);
    }

    /**
     * 根据索引修改某个题
     *
     * @param index           要修改的索引
     * @param subjectVoEntity 要修改的题目
     */
    private void changeSubject(Long index, SubjectVoEntity subjectVoEntity) {

        stringRedisTemplate.opsForList().set(this.getKey(), index, JSON.toJSONString(subjectVoEntity));
    }


    /**
     * 根据索引获得某一个题,当时将当前索引所对应的题目修改
     *
     * @param index           跳转到的索引
     * @param currentIndex    当前索引
     * @param subjectVoEntity 当前索引题目
     * @return 跳转到的索引所对应的题目
     */
    @Override
    public SubjectVoEntity getOneSubject(Long index, Long currentIndex, SubjectVoEntity subjectVoEntity) {
        if (subjectVoEntity.getSubjectType() != SubjectConstant.SHORT_ANSWER_SUBJECT) {
            //如果该题目是多选题 那么需要将其答案['A','B','C'] 转化为ABC
            if (subjectVoEntity.getSelectedChoice() != null && subjectVoEntity.getSelectedChoice().length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < subjectVoEntity.getSelectedChoice().length; i++) {
                    sb.append(subjectVoEntity.getSelectedChoice()[i]);
                }
                String ret = sb.toString();
                subjectVoEntity.setSelectAnswer(new String(ret));
            }
            stringRedisTemplate.opsForList().set(AuthServerConstant.PAPER_PREFIX_KEY + this.getUserName(), currentIndex - 1, JSON.toJSONString(subjectVoEntity));
        }
        return this.getOneSubjectByIndex(index);
    }

    /**
     * 根据索引获得某一个题
     *
     * @param index 索引
     * @return 某一个题
     */
    private SubjectVoEntity getOneSubjectByIndex(Long index) {
        return JSON.parseObject(stringRedisTemplate.opsForList().index(this.getKey(), index), SubjectVoEntity.class);
    }

    /**
     * 算出该张试卷的得分 注意 此试卷是不包含大题的试卷 可以直接算出分数 同时标记试卷已经完成 不需要再交给老师批改
     *
     * @param teacherId 出题老师id
     * @param paperId   此试卷的id
     * @return 最后试卷得了多少分
     */
    @Override
    public Map<String, Integer> getScoreRetWithNoShortAnswer(Long teacherId, Long paperId) {
        String key = this.getKey();
        //获得当前试卷
        PaperEntity paperEntity = this.getById(paperId);
        DonePaperEntity donePaperEntity = new DonePaperEntity();
        BeanUtils.copyProperties(paperEntity, donePaperEntity);
        donePaperEntity.setStudentId(Long.valueOf(this.getMemberEntity().getId()));
        donePaperEntity.setPaperId(paperId);
        donePaperService.save(donePaperEntity);
        //设置试卷的完成情况 为已完成
        donePaperEntity.setPaperType(PaperConstant.COMPLETED);
        //获得学生所有题目
        List<String> allSubjectsJson = this.getAllSubjectsJson();
        //删除redis中的所有题目
        this.deleteAllSubjects();
        //同时删除屌redis中所有的题目集
        Set<String> keys = stringRedisTemplate.keys(WrongSubjectConstant.WRONG_SUBJECT_PREFIX_KEY);

        if (CollectionUtils.isNotEmpty(keys)) {
            stringRedisTemplate.delete(keys);
        }

        if (this.isValid(allSubjectsJson)) {
            allSubjectsJson.forEach(o -> {
                SubjectVoEntity subjectVoEntity = JSON.parseObject(o, SubjectVoEntity.class);
                //题目统计
                this.addWrongAnswerToMysql(subjectVoEntity);
                DoneSubject doneSubject = new DoneSubject();
                BeanUtils.copyProperties(subjectVoEntity, doneSubject);
                doneSubject.setDonePaperId(donePaperEntity.getId().longValue());
                doneSubject.setActualScore(subjectVoEntity.getSubjectAnwser().equals(subjectVoEntity.getSelectAnswer()) ? Integer.valueOf(subjectVoEntity.getScore().intValue()) : 0);
                //如果不是大题 那么可以直接算分
                if (subjectVoEntity.getSubjectType().intValue() != SubjectConstant.SHORT_ANSWER_SUBJECT.intValue()) {
                    donePaperEntity.setActualScore(donePaperEntity.getActualScore() + doneSubject.getActualScore());
                    donePaperService.updateById(donePaperEntity);
                }
                doneSubjectService.save(doneSubject);
            });
        }
        //返回分数和试卷id
        Map<String, Integer> ret = new HashMap<>();
        ret.put("score", donePaperEntity.getActualScore());
        ret.put("paperId", donePaperEntity.getId().intValue());
        return ret;
    }

    /**
     * 获得我们要操作的错题集
     *
     * @return 错题集
     */
    private BoundHashOperations<String, Object, Object> getWrongAnswerOps() {
        String keyPrefix = SubjectConstant.WRONG_ANSWER_FREQUENCY_PREFIX_KEY;
        String key = keyPrefix + this.getUserName();
        //绑定哈希操作
        return stringRedisTemplate.boundHashOps(key);
    }

    @Override
    public void changeSubjectByIndex(Long index, SubjectVoEntity subjectVoEntity) {
        this.changeSubject(index, subjectVoEntity);
    }

    /**
     * 获得当前老师下的所有已做试卷
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageDonePaperPage(Map<String, Object> params) {
//        IPage<PaperEntity> page = this.page(
//                new Query<PaperEntity>().getPage(params),
//                new QueryWrapper<PaperEntity>()
//        );
        IPage<DonePaperEntity> page = donePaperService.page(new Query<DonePaperEntity>().getPage(params), new LambdaQueryWrapper<DonePaperEntity>().eq(DonePaperEntity::getTeacherId, this.getMemberEntity().getId()));
        //需要再封装一下数据

        IPage<DonePaperVo> retPage = new Page<>();
        BeanUtils.copyProperties(page, retPage, "records");
        List<DonePaperEntity> records = page.getRecords();
        List<DonePaperVo> donePaperVoList = records.stream().map(o -> {
            DonePaperVo donePaperVo = new DonePaperVo();
            BeanUtils.copyProperties(o, donePaperVo);
            //查询学生姓名
            Long studentId = donePaperVo.getStudentId();
            MemberEntity memberEntity = memberService.getById(studentId);
            donePaperVo.setStudentName(memberEntity.getMemberName());
            return donePaperVo;
        }).collect(Collectors.toList());
        retPage.setRecords(donePaperVoList);
        return new PageUtils(retPage);
    }

    /**
     * 获得学生能做的 试卷 1，时间包含当前时间 2，没有做过的试卷 3，该学生所属老师的试卷
     *
     * @return
     */
    @Override
    public R getValidPaper() {

        Boolean aBoolean = stringRedisTemplate.hasKey(this.getKey());
        //获得当前学生id
        Integer id = springSecurityUtil.getUser().getMemberEntity().getId();
        System.out.println(id);
        //获得当前学生所有做过的试卷
        List<DonePaperEntity> donePaperEntities = donePaperService.list(new LambdaQueryWrapper<DonePaperEntity>().eq(DonePaperEntity::getStudentId, id));
        //获得当前学生所有老师id
        List<Long> teacherIds = studentTeacherService.list(new LambdaQueryWrapper<StudentTeacherEntity>().eq(StudentTeacherEntity::getStudentId, id)).stream().map(StudentTeacherEntity::getTeacherId).collect(Collectors.toList());
        teacherIds.forEach(System.out::println);
        //健壮性判断
        if (teacherIds != null && teacherIds.size() > 0) {
            //查询所有该老师出的试卷
            Date currentTime = new Date();
            LambdaQueryWrapper<PaperEntity> paperEntityLambdaQueryWrapper = new LambdaQueryWrapper<PaperEntity>(

            ).in(PaperEntity::getTeacherId, teacherIds
            ).le(PaperEntity::getBeginTime, currentTime
            ).ge(PaperEntity::getEndTime, currentTime
            );
            if (donePaperEntities != null && donePaperEntities.size() > 0) {
                //获得所有试卷id
                paperEntityLambdaQueryWrapper.notIn(PaperEntity::getId, donePaperEntities.stream().map(DonePaperEntity::getPaperId).collect(Collectors.toList()));
            }
            List<PaperEntity> paperEntities = this.list(paperEntityLambdaQueryWrapper);
            if (paperEntities != null && paperEntities.size() > 0) {
                List<PaperVo> paperVos = paperEntities.stream().map(o -> {
                    Long teacherId = o.getTeacherId();
                    MemberEntity memberEntity = memberService.getById(teacherId);
                    PaperVo paperVo = new PaperVo();
                    BeanUtils.copyProperties(o, paperVo);
                    paperVo.setTeacherName(memberEntity.getMemberName());
                    return paperVo;
                }).sorted(((o1, o2) -> {
                    return o1.getBeginTime().compareTo(o2.getBeginTime());
                })).collect(Collectors.toList());
                if (BooleanUtil.isTrue(aBoolean)) {
                    return R.ok().put("data", paperVos).put("status", PaperConstant.DO_PAPER);
                } else {
                    return R.ok().put("data", paperVos).put("status", PaperConstant.NOT_DO_PAPER);
                }
            }
        }
        return R.ok().put("data", null).put("status", PaperConstant.NOT_DO_PAPER);
    }

    /**
     * 获得所有的试卷  如果某位学生已经答题 那么将不能再答题
     *
     * @return
     */
    @Override
    public R getAllPapers() {
        String key = this.getKey();
        Boolean aBoolean = stringRedisTemplate.hasKey(key);
        //获得所有的试卷
        List<PaperEntity> list = this.list();
        //遍历处理所有的试卷
        Collection<PaperVo> retList = list.stream().map(o -> {
            PaperVo paperVo = new PaperVo();
            BeanUtils.copyProperties(o, paperVo);
            //根据老师id查询老师的姓名
            MemberEntity one = memberService.getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getId, o.getTeacherId()));
            //获得老师姓名
            paperVo.setTeacherName(one.getMemberName());
            return paperVo;
        }).collect(Collectors.toList());

        return R.ok().put("data", retList);
    }

    /**
     * 获得全校所有的试卷
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils getTotalSchoolPaper(Map<String, Object> params) {
        IPage<PaperEntity> page = this.page(
                new Query<PaperEntity>().getPage(params),
                new QueryWrapper<PaperEntity>()
        );
        IPage<PaperVo> ret = new Page<>();
        List<PaperEntity> list = this.list();
        BeanUtils.copyProperties(page, ret, "records");
        ret.setRecords(list.stream().map(o -> {
            //获得老师ID
            Long teacherId = o.getTeacherId();
            //获得老师
            MemberEntity memberEntity = memberService.getById(teacherId);
            //获得老师姓名
            String teacherName = memberEntity.getMemberName();
            PaperVo paperVo = new PaperVo();
            BeanUtils.copyProperties(o, paperVo);
            paperVo.setTeacherName(teacherName);
            return paperVo;
        }).collect(Collectors.toList()));
        return new PageUtils(ret);


    }

    /**
     * 删除当前学生所做试卷的所有题目
     */
    private void deleteAllSubjects() {
        stringRedisTemplate.delete(this.getKey());
    }

    /**
     * 获得所有题目  元素为json格式
     *
     * @return 获得所有题目  元素为json格式
     */
    private List<String> getAllSubjectsJson() {
        //获得题目数量
        Long subjectCount = stringRedisTemplate.opsForList().size(this.getKey());
        if (subjectCount != null && !subjectCount.equals(0L)) {
            return stringRedisTemplate.opsForList().range(this.getKey(), 0, subjectCount.intValue() - 1);
        } else {
            return null;
        }
    }


    /**
     * 获得当前学生正在做的试卷的所有题目
     *
     * @return 当前学生正在做的试卷的所有题目
     */
    private List<SubjectVoEntity> getAllSubjects() {
        //获得题目数量
        Long subjectCount = stringRedisTemplate.opsForList().size(this.getKey());
        //获得所有的题目
        if (subjectCount != null && !subjectCount.equals(0L)) {
            List<String> range = stringRedisTemplate.opsForList().range(this.getKey(), 0, subjectCount.intValue() - 1);
            if (range != null && range.size() > 0) {
                return range.stream().map(o -> {
                    return JSON.parseObject(o, SubjectVoEntity.class);
                }).collect(Collectors.toList());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 获取redis中存储当前试卷 的key login:userinfo:paper:xxx
     *
     * @return 获取redis中存储当前试卷 的key
     */
    private String getKey() {
        return AuthServerConstant.PAPER_PREFIX_KEY + this.getUserName();
    }


    /**
     * 按照题目的类型 将题目进行分类 最后顺序依次是 判断题 单选题 多选题 填空题 大题
     *
     * @param subjectEntities 乱序的题目集合
     * @return 排好序的题目集合
     */
    public List<SubjectVoEntity> changeSubjectOrder(List<SubjectEntity> subjectEntities) {
        List<SubjectVoEntity> single = new ArrayList<>();
        List<SubjectVoEntity> noSingle = new ArrayList<>();
        List<SubjectVoEntity> judgement = new ArrayList<>();
        List<SubjectVoEntity> showAnswer = new ArrayList<>();
        List<SubjectVoEntity> fill = new ArrayList<>();
        //将题目进行分类  按照题目的类型
        subjectEntities.forEach(o -> {
            SubjectVoEntity subjectVoEntity = new SubjectVoEntity();
            BeanUtils.copyProperties(o, subjectVoEntity);
            if (o.getSubjectType().intValue() == SubjectConstant.SINGLE_SUBJECT.intValue()) {
                single.add(subjectVoEntity);
            } else if (o.getSubjectType().intValue() == SubjectConstant.NO_SINGLE_SUBJECT.intValue()) {
                noSingle.add(subjectVoEntity);
            } else if (o.getSubjectType().intValue() == SubjectConstant.FILL_SUBJECT.intValue()) {
                fill.add(subjectVoEntity);
            } else if (o.getSubjectType().intValue() == SubjectConstant.JUDGE_SUBJECT.intValue()) {
                judgement.add(subjectVoEntity);
            } else {
                showAnswer.add(subjectVoEntity);
            }
        });
        //对题目进行合并
        List<SubjectVoEntity> retList = new ArrayList<>();
        //健壮性判断
        if (judgement.size() > 0) {
            retList.addAll(judgement);
        }
        if (single.size() > 0) {
            retList.addAll(single);
        }
        if (noSingle.size() > 0) {
            retList.addAll(noSingle);
        }
        if (fill.size() > 0) {
            retList.addAll(fill);
        }
        if (showAnswer.size() > 0) {
            retList.addAll(showAnswer);
        }
        return retList;
    }

    /**
     * 通过SpringSecurityContextHolder获取到当前线程的用户实体类
     *
     * @return 当前线程用户实体类
     */
    private MemberEntity getMemberEntity() {
        return springSecurityUtil.getUser().getMemberEntity();
    }

    /**
     * 获取当前线程用户的用户名
     *
     * @return 当前线程用户名
     */
    private String getUserName() {
        return this.getMemberEntity().getMemberName();
    }


    private <T> boolean isValid(List<T> list) {
        return (list != null && list.size() > 0);
    }


    /**
     * 添加题目到redis中
     *
     * @param wrongAnswerOps  错题集
     * @param subjectVoEntity 题目
     */
    public void addWrongSubjectToRedis(BoundHashOperations<String, Object, Object> wrongAnswerOps, SubjectVoEntity subjectVoEntity) {
        Long subjectId = subjectVoEntity.getId();
        String selectAnswer = subjectVoEntity.getSelectAnswer();

        //需要判断题目是否已经添加过
        Object object = wrongAnswerOps.get(subjectId.toString());
        if (Objects.isNull(object)) {
            //说明没有添加过这个题目
            WorseSubject worseSubject = new WorseSubject();
            worseSubject.setSubjectId(subjectId);
            if (!"".equals(subjectVoEntity.getSelectAnswer()) && !subjectVoEntity.getSubjectAnwser().equals(subjectVoEntity.getSelectAnswer())) {
                //题目错了 那么添加错误次数1 同时记录错误答案及其次数 直接存入redis中
                //创建题目——频率
                Map<String, Integer> wrongAnswerFrequency = new HashMap<>();
                worseSubject.setWrongCount(1);
                Integer orDefault = wrongAnswerFrequency.getOrDefault(selectAnswer, 0);
                wrongAnswerFrequency.put(selectAnswer, orDefault + 1);
                worseSubject.setWrongAnswerFrequency(wrongAnswerFrequency);
            } else if (!"".equals(subjectVoEntity.getSelectAnswer()) && subjectVoEntity.getSubjectAnwser().equals(subjectVoEntity.getSelectAnswer())) {
                //说明答案没错 那么添加正确次数1 直接存入redis中
                worseSubject.setRightCount(1);
            }
            wrongAnswerOps.put(subjectId.toString(), JSON.toJSONString(worseSubject));
        } else {
            //说明已经添加过这个题目
            //获得该题目
            WorseSubject worseSubject = JSON.parseObject(object.toString(), WorseSubject.class);
            if (!"".equals(subjectVoEntity.getSelectAnswer()) && !subjectVoEntity.getSubjectAnwser().equals(subjectVoEntity.getSelectAnswer())) {
                //说明题目错了 那么设置错误次数+1 同时记录错误答案及其次数 直接存入redis中
                Map<String, Integer> wrongAnswerFrequency = worseSubject.getWrongAnswerFrequency();
                Integer orDefault = wrongAnswerFrequency.getOrDefault(selectAnswer, 0);
                wrongAnswerFrequency.put(selectAnswer, orDefault + 1);
                worseSubject.setWrongCount(worseSubject.getWrongCount() + 1);
                worseSubject.setWrongAnswerFrequency(wrongAnswerFrequency);
            } else if (!"".equals(subjectVoEntity.getSelectAnswer()) && subjectVoEntity.getSubjectAnwser().equals(subjectVoEntity.getSelectAnswer())) {
                //说明题目没错 那么设置正确次数+1 直接存入redis中
                worseSubject.setRightCount(worseSubject.getRightCount() + 1);
            }
            //存入redis中
            wrongAnswerOps.put(subjectId.toString(), JSON.toJSONString(worseSubject));
        }
    }

    /**
     * 将错题添加到mysql中
     *
     * @param subjectVoEntity 错题
     */
    private void addWrongAnswerToMysql(SubjectVoEntity subjectVoEntity) {
        //题目id
        Long subjectId = subjectVoEntity.getId();
        //题目正确答案
        String subjectAnswer = subjectVoEntity.getSubjectAnwser();
        //学生选的答案
        String selectAnswer = subjectVoEntity.getSelectAnswer();
        WrongSubjectEntity wrongSubjectEntity = wrongSubjectService.getOne(new LambdaQueryWrapper<WrongSubjectEntity>().eq(WrongSubjectEntity::getSubjectId, subjectId));
        if (Objects.isNull(wrongSubjectEntity)) {
            //说明数据库中不存在
            //下面是改题目错了
            if (!"".equals(selectAnswer) && !subjectAnswer.equals(selectAnswer)) {
                //说明题目错了 并且答了此题
                WrongSubjectEntity subjectEntity = new WrongSubjectEntity();
                subjectEntity.setSubjectId(subjectId);
                subjectEntity.setTotalCount(1L);
                subjectEntity.setWrongCount(1L);
                subjectEntity.setWrongFrequency(new BigDecimal("1"));
                Map<String, Integer> wrongAnswerFrequency = new HashMap<>();
                Integer orDefault = wrongAnswerFrequency.getOrDefault(selectAnswer, 0);
                wrongAnswerFrequency.put(selectAnswer, orDefault + 1);
                String jsonString = JSON.toJSONString(wrongAnswerFrequency);
                subjectEntity.setWrongAnswerFrequency(jsonString);
                wrongSubjectService.save(subjectEntity);
            }
            if (!"".equals(selectAnswer) && subjectAnswer.equals(selectAnswer)) {
                //说明题目对了 并且答了此题
                //说明题目错了 并且答了此题
                WrongSubjectEntity subjectEntity = new WrongSubjectEntity();
                subjectEntity.setSubjectId(subjectId);
                subjectEntity.setTotalCount(1L);
                Map<String, Integer> wrongAnswerFrequency = new HashMap<>();
                wrongSubjectService.save(subjectEntity);
            }
        } else {
            //说明数据库中存在当前对象
            if (!"".equals(selectAnswer) && !subjectAnswer.equals(selectAnswer)) {
                //题目错了 并且答了此题
                //错的人数
                wrongSubjectEntity.setWrongCount(wrongSubjectEntity.getWrongCount() + 1L);
                //答题的总人数
                wrongSubjectEntity.setTotalCount(wrongSubjectEntity.getTotalCount() + 1L);
                BigDecimal frequency = new BigDecimal("0");
                frequency = new BigDecimal(wrongSubjectEntity.getWrongCount().toString()).divide(new BigDecimal(wrongSubjectEntity.getTotalCount().toString()), 3, RoundingMode.HALF_UP);
                wrongSubjectEntity.setWrongFrequency(frequency);
                String jsonString = wrongSubjectEntity.getWrongAnswerFrequency();
                //反序列化
                HashMap<String, Integer> wrongAnswerFrequency = JSON.parseObject(jsonString, new TypeReference<HashMap<String, Integer>>() {
                });
                Integer orDefault = wrongAnswerFrequency.getOrDefault(selectAnswer, 0);
                wrongAnswerFrequency.put(selectAnswer, orDefault + 1);
                jsonString = JSON.toJSONString(wrongAnswerFrequency);
                wrongSubjectEntity.setWrongAnswerFrequency(jsonString);
                wrongSubjectService.updateById(wrongSubjectEntity);
            }
            if (!"".equals(selectAnswer) && subjectAnswer.equals(selectAnswer)) {
                wrongSubjectEntity.setTotalCount(wrongSubjectEntity.getTotalCount() + 1L);
                BigDecimal frequency = new BigDecimal("0");
                frequency = new BigDecimal(wrongSubjectEntity.getWrongCount().toString()).divide(new BigDecimal(wrongSubjectEntity.getTotalCount().toString()), 3, RoundingMode.HALF_UP);
                wrongSubjectEntity.setWrongFrequency(frequency);
                wrongSubjectService.updateById(wrongSubjectEntity);
            }
        }
    }
}
