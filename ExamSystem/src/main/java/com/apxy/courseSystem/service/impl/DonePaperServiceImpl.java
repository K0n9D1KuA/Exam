package com.apxy.courseSystem.service.impl;

import com.apxy.courseSystem.constant.DonePaperConstant;
import com.apxy.courseSystem.constant.SubjectConstant;
import com.apxy.courseSystem.dao.DonePaperMapper;
import com.apxy.courseSystem.entity.DonePaperEntity;
import com.apxy.courseSystem.entity.DoneSubject;
import com.apxy.courseSystem.entity.MemberEntity;
import com.apxy.courseSystem.entity.ScoreAndPeopleCount;
import com.apxy.courseSystem.entity.vo.*;
import com.apxy.courseSystem.service.DonePaperService;
import com.apxy.courseSystem.service.DoneSubjectService;
import com.apxy.courseSystem.service.MemberService;
import com.apxy.courseSystem.util.usingUtil.R;
import com.apxy.courseSystem.util.usingUtil.SpringSecurityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class DonePaperServiceImpl extends ServiceImpl<DonePaperMapper, DonePaperEntity> implements DonePaperService {
    @Autowired
    private MemberService memberService;
    @Lazy
    @Autowired
    private DoneSubjectService doneSubjectService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 描述：根据已做试卷id 获得其所有的已做题目
     *
     * @param donePaperId 已做试卷id
     * @return 该已做试卷下所有的已做题目
     */
    @Override
    public List<DoneSubjectVo> getAllDoneSubjectsByDonePaperId(Long donePaperId) {
        //获得所有的已做题目
        List<DoneSubject> doneSubjects = doneSubjectService.list(new LambdaQueryWrapper<DoneSubject>().eq(DoneSubject::getDonePaperId, donePaperId));
        //遍历封装
        return doneSubjects.stream()
                //转化
                .map(o -> {
                    DoneSubjectVo doneSubjectVo = new DoneSubjectVo();
                    BeanUtils.copyProperties(o, doneSubjectVo);
                    //如果是大题 需要将题目答案用;分隔开
                    //格式https://wcity.oss-cn-hangzhou.aliyuncs.com/2022/09/15/8dd4874328434bf68575957e84bf577fGw8vk5H6VQ3i47edb94a078b690135ce0c0b1385a0e8.jpeg;https://wcity.oss-cn-hangzhou.aliyuncs.com/2022/09/15/fdab27a373a24804bcd34e7cb0e206adRxKy0zCBjWVT2837b79b3b2c35ed910ffd41cefc3bd3.jpg;
                    if (o.getSubjectType().equals(SubjectConstant.SHORT_ANSWER_SUBJECT)) {
                        //格式https://wcity.oss-cn-hangzhou.aliyuncs.com/2022/09/15/8dd4874328434bf68575957e84bf577fGw8vk5H6VQ3i47edb94a078b690135ce0c0b1385a0e8.jpeg;https://wcity.oss-cn-hangzhou.aliyuncs.com/2022/09/15/fdab27a373a24804bcd34e7cb0e206adRxKy0zCBjWVT2837b79b3b2c35ed910ffd41cefc3bd3.jpg;
                        String selectAnswer = doneSubjectVo.getSelectAnswer();
//                        List<String> strings = new ArrayList<>(Arrays.asList(selectAnswer.split(";")));
                        List<String> answerImagesByStudent = Arrays.asList(selectAnswer.split(";"));
                        //过滤掉空的图片
                        answerImagesByStudent = answerImagesByStudent.stream().filter(j -> {
                            //过滤掉空的字符串
                            return !"".equals(j);
                        }).collect(Collectors.toList());
                        //转化为String[]
                        String[] temp = answerImagesByStudent.toArray(new String[0]);
//                        String[] temp = new String[answerImagesByStudent.size()];
//                        for (int i = 0; i < answerImagesByStudent.size(); i++) {
//                            temp[i] = answerImagesByStudent.get(i);
//                        }
                        doneSubjectVo.setStudentImages(temp);
                    }
                    return doneSubjectVo;
                }).collect(Collectors.toList());
    }

    /**
     * 根据已做试卷id 获得 已做试卷 + 该试卷下所包含的所有题目
     *
     * @param donePaperId 已做试卷id
     * @return 已做试卷 + 该试卷下所包含的所有题目
     */
    @Override
    public R getDonePaperAndDoneSubjects(Long donePaperId) {
        List<DoneSubject> doneSubjects = doneSubjectService.list(new LambdaQueryWrapper<DoneSubject>().eq(DoneSubject::getDonePaperId, donePaperId));
        DonePaperEntity donePaperEntity = this.getById(donePaperId);
        return R.ok()
                .put("donePaperEntity", donePaperEntity)
                .put("doneSubjectList", doneSubjects);

    }

    /*
     * @author: K0n9D1KuA
     * @description: 获得某学生所有已做试卷
     * @param: null
     * @return:
     * @date: 2022/12/4 1:11
     */

    @Override
    public List<DonePaperVo> getDonePapers() {
        //获得学生id
        Long studentId = this.getMemberId();
        List<DonePaperEntity> donePaperEntities = this.list(new LambdaQueryWrapper<DonePaperEntity>().eq(DonePaperEntity::getStudentId, studentId));
        return donePaperEntities.stream().map(o -> {
            DonePaperVo donePaperVo = new DonePaperVo();
            BeanUtils.copyProperties(o, donePaperVo);
            //获得老师id
            Long teacherId = donePaperVo.getTeacherId();
            //获得老师
            MemberEntity teacher = memberService.getById(teacherId);
            String teacherName = teacher.getMemberName();
            donePaperVo.setTeacherName(teacherName);
            return donePaperVo;
        }).collect(Collectors.toList());

    }

    /*
     * @author: K0n9D1KuA
     * @description: 根据已做过试卷id  查询该试卷下同班同学的排名情况
     * @param: donePaperId 已做试卷id
     * @return:
     * @date: 2022/12/4 1:13
     */

    @Override
    public List<RankVo> getRankByPaperId(Long donePaperId) {
        //获得所有试卷
        List<DonePaperEntity> donePaperEntities = this.list(new LambdaQueryWrapper<DonePaperEntity>().eq(DonePaperEntity::getPaperId, donePaperId));
        //排序一下 按照试卷分数排降序
        donePaperEntities.sort((o1, o2) -> {
            return o2.getActualScore() - o1.getActualScore();
        });
        List<RankVo> ret = new ArrayList<>();
        for (int i = 0; i < donePaperEntities.size(); i++) {
            DonePaperEntity currentDonePaperEntity = donePaperEntities.get(i);
            RankVo rankVo = new RankVo();
            //分数
            rankVo.setScore(currentDonePaperEntity.getActualScore());
            //排名
            rankVo.setPosition(i + 1);
            //学生id
            Long studentId = currentDonePaperEntity.getStudentId();
            //获得学生
            MemberEntity student = memberService.getById(studentId);
            //学生姓名
            rankVo.setStudentName(student.getMemberName());
            ret.add(rankVo);
        }
        return ret;
    }

    /*
     * @author: K0n9D1KuA
     * @description: 获得某老师已发布的试卷 并且试卷是已完成状态
     * @param: null
     * @return:
     * @date: 2022/12/4 1:15
     */

    @Override
    public R getDonePaper() {
        //首先获得老师的id
        Long teacherId = this.getMemberId();
        //获得该老师下面所有已经做完的试卷
        LambdaQueryWrapper<DonePaperEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DonePaperEntity::getTeacherId, teacherId);
        List<DonePaperEntity> donePaperEntities = this.list(lambdaQueryWrapper);
        return donePaperEntities.size() == 0
                //没有卷子 返回空集合
                ? R.ok().put("data", Collections.emptyList())
                //根据试卷id去重
                : R.ok().put("data", donePaperEntities.stream().filter(distinctByKey(DonePaperEntity::getPaperId)).collect(Collectors.toList()));
//        if (donePaperEntities.size() == 0) {
//            //如果为空返回空集合
//            return R.ok().put("data", Collections.emptyList());
//        } else {
//            //根据试卷id去重
//            return R.ok().put("data", donePaperEntities.stream().filter(distinctByKey(DonePaperEntity::getPaperId)).collect(Collectors.toList()));
//        }

    }

    /*
     * @author: K0n9D1KuA
     * @description: 返回已做试卷相关数据分析 采用异步编排
     * @param: paperId 试卷id
     * @return:
     * @date: 2022/12/4 1:19
     */


    @Override
    public R getDonePaperDetail(Long paperId) throws ExecutionException, InterruptedException {
        //===========================================================================
        //1,值的定义
        //最高分
        BigDecimal maxScore = new BigDecimal("0");
        //最高分学生名字
        String maxScoreStudentName = new String("");
        //最低分学生名字
        String minScoreStudentName = new String("");
        //最低分
        BigDecimal minScore = new BigDecimal("0");
        //平均分
        BigDecimal average = new BigDecimal("0");
        //折线图
        StaticVo staticVo = new StaticVo();
        //所有已做试卷
        List<DonePaperEntity> donePaperEntities = new ArrayList<>();
        //不及格人数
        AtomicInteger unPassCount = new AtomicInteger(0);
        //排名情况[表格]
        List<RankVo> rankVos = new ArrayList<>();
        //分数分布 饼状图
        final List<PieChartVo> pieChartVos = new ArrayList<>(5);
        //中位数
        BigDecimal midScore = new BigDecimal("0");
        //及格率
        BigDecimal passPercent = new BigDecimal("0");
        //总参考人数
        Integer peopleCount = 0;
        //donePaperIds
        List<Integer> donePaperIds = new ArrayList<>();
        //doneSubjects
        List<DoneSubject> doneSubjects = new ArrayList<>();
        //每道题情况
        List<SubjectsSituation> subjectsSituation = new ArrayList<>();
        //===========================================================================

        //2,异步编排
        //2.1,getStaticVoFuture 封装折线图 独立的任务
        CompletableFuture<StaticVo> getStaticVoFuture = CompletableFuture.supplyAsync(() -> {
            return this.getStaticVo(paperId);
        }, threadPoolExecutor);

        //2.2,获得所有已做试卷
        //异步有返回值的任务 获得所有已做试卷
        CompletableFuture<List<DonePaperEntity>> getDonePaperEntityFuture = CompletableFuture.supplyAsync(() -> {
            return this.getDonePaperEntities(paperId, donePaperIds);
        }, threadPoolExecutor);

        //2.3,获得排名
        //异步无返回值的任务 依赖于getDonePaperEntityFuture
        CompletableFuture<Void> getRankVosFuture = getDonePaperEntityFuture.thenAcceptAsync(result -> {
            this.getRankVos(result, rankVos);
        }, threadPoolExecutor);
        //2.4,获得饼状图
        //异步无返回值的任务 依赖于getDonePaperEntityFuture
        CompletableFuture<Void> getPieCharVosFuture = getDonePaperEntityFuture.thenAcceptAsync(result -> {
            this.getPieCharVos(result, unPassCount, pieChartVos);
        }, threadPoolExecutor);
        //2.5,获得所有已做题目
        //异步无返回值的任务 依赖于getDonePaperEntityFuture
        CompletableFuture<Void> getDoneSubjectByDonePaperIdsFuture = getDonePaperEntityFuture.thenAcceptAsync(result -> {
            this.getDoneSubjectByDonePaperIds(donePaperIds, subjectsSituation);
        }, threadPoolExecutor);

        //等待上述线程全部执行完毕
        CompletableFuture.allOf(getStaticVoFuture, getPieCharVosFuture, getRankVosFuture, getDoneSubjectByDonePaperIdsFuture).get();
        staticVo = getStaticVoFuture.get();
        donePaperEntities = getDonePaperEntityFuture.get();
        //===========================================================================
        //3,一些值的计算
        //过滤掉null
        for (int i = 0; i < pieChartVos.size(); i++) {
            if (pieChartVos.get(i).getValue() == 0) {
                pieChartVos.set(i, null);
            }
        }
        //总参考人数
        peopleCount = donePaperEntities.size();
        //计算及格率
        passPercent = new BigDecimal("100").multiply(new BigDecimal(peopleCount - unPassCount.get()).divide(new BigDecimal(peopleCount), 2, BigDecimal.ROUND_HALF_UP));
        //最高分计算
        maxScore = new BigDecimal(donePaperEntities.get(0).getActualScore().toString());
        maxScoreStudentName = memberService.getById(donePaperEntities.get(0).getStudentId()).getMemberName();
        //最低分计算
        minScore = new BigDecimal(donePaperEntities.get(donePaperEntities.size() - 1).getActualScore().toString());
        minScoreStudentName = memberService.getById(donePaperEntities.get(donePaperEntities.size() - 1).getStudentId()).getMemberName();
        for (DonePaperEntity o : donePaperEntities) {
            average = average.add(new BigDecimal(o.getActualScore()));
        }
        //计算平均分
        average = average.divide(new BigDecimal(peopleCount), 2, BigDecimal.ROUND_HALF_UP);
        //计算中位数
        if (peopleCount % 2 == 0) {
            //说明是偶数个元素 那么中位数就是 (mid+mid-1)/2
            int mid = peopleCount / 2;
            midScore = new BigDecimal(donePaperEntities.get(mid - 1).getActualScore())
                    //+
                    .add(new BigDecimal(donePaperEntities.get(mid).getActualScore()))
                    //÷ 保留两位小数
                    .divide(new BigDecimal(2), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            int mid = peopleCount / 2;
            //如果是奇数个元素 那么中位数就是(mid)
            midScore = new BigDecimal(donePaperEntities.get(mid).getActualScore().toString());
        }
        //===========================================================================
        //4,返回结果
        return R.ok()
                .put("data", staticVo)
                .put("minScore", minScore)
                .put("minScoreStudentName", minScoreStudentName)
                .put("maxScore", maxScore)
                .put("maxScoreStudentName", maxScoreStudentName)
                .put("average", average)
                .put("peopleCount", peopleCount)
                .put("dataList", rankVos)
                .put("pieChartVos", pieChartVos)
                .put("passPercent", passPercent)
                .put("midScore", midScore)
                .put("subjectsSituation", subjectsSituation);
    }

    //根据donePaperIds 获取到所有的doneSubject 同时封装试卷答题情况
    private void getDoneSubjectByDonePaperIds(List<Integer> donePaperIds, List<SubjectsSituation> subjectsSituation) {
        List<DoneSubject> doneSubjectList = doneSubjectService.list(new LambdaQueryWrapper<DoneSubject>().in(DoneSubject::getDonePaperId, donePaperIds));
        //获得题目数量
        int subjectCount = doneSubjectList.size() / donePaperIds.size();
        //填充题目数量个0
        for (int i = 0; i < subjectCount; i++) {
            subjectsSituation.add(new SubjectsSituation());
        }
        for (int i = 0; i < doneSubjectList.size(); i++) {
            //当前题目
            DoneSubject currentSubject = doneSubjectList.get(i);
            //实际得分与题目总分相同 说明题目对了
            if (currentSubject.getScore().intValue() == currentSubject.getActualScore()) {
                //说明题目正确
                //题目对的次数+1
                subjectsSituation.get(i % subjectCount).setRightCount(subjectsSituation.get(i % subjectCount).getRightCount() + 1);
            }
        }
        //封装正确率
        subjectsSituation.forEach(o -> {
            //获得正确率
            //正确率 = 正确数/总人数
            BigDecimal rightPercent = new BigDecimal(o.getRightCount())
                    //保留两位小数
                    .divide(new BigDecimal(((Integer) donePaperIds.size())), 2, BigDecimal.ROUND_HALF_UP);
            o.setRightPercent(new BigDecimal(rightPercent.toString()).multiply(new BigDecimal("100")));
        });

    }

    //获得所有已做试卷 同时获得平均分 中位数
    private List<DonePaperEntity> getDonePaperEntities(Long paperId,
                                                       List<Integer> donePaperIds) {
        //获得所有已做试卷
        LambdaQueryWrapper<DonePaperEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DonePaperEntity::getPaperId, paperId);
        List<DonePaperEntity> donePaperEntities = this.list(lambdaQueryWrapper);
        //排序 降序
        Collections.sort(donePaperEntities, (o1, o2) -> {
            return o2.getActualScore() - o1.getActualScore();
        });
        //获得他们的id
        donePaperEntities.forEach(o -> {
            donePaperIds.add(o.getId());
        });
        return donePaperEntities;

    }

    //封装折线数据
    private StaticVo getStaticVo(Long paperId) {
        DonePaperMapper baseMapper = this.getBaseMapper();
        List<ScoreAndPeopleCount> scoreAndPeopleCount = baseMapper.getDonePaperDetail(paperId);
        StaticVo staticVo = new StaticVo();
        for (ScoreAndPeopleCount o : scoreAndPeopleCount) {
            staticVo.getScores().add(o.getScore() + "分");
            staticVo.getPeopleCount().add(o.getPeopleCount());
//            //总参考人数++
//            peopleCount.incrementAndGet();
        }
        return staticVo;
    }

    //封装排名(表格)
    private List<RankVo> getRankVos(List<DonePaperEntity> donePaperEntities, List<RankVo> rankVos) {
        int position = 0;
        for (DonePaperEntity o : donePaperEntities
        ) {
            RankVo rankVo = new RankVo();
            rankVo.setScore(o.getActualScore());
            rankVo.setPosition(++position);
            rankVo.setStudentName(memberService.getById(o.getStudentId()).getMemberName());
            rankVos.add(rankVo);
        }
        return rankVos;
    }

    //封装分数组成（饼状图）同时计算及格率
    private List<PieChartVo> getPieCharVos(List<DonePaperEntity> donePaperEntities, AtomicInteger unPassCount, List<PieChartVo> pieChartVos) {
        //创建占位
        for (int i = 0; i < 5; i++) {
            pieChartVos.add(new PieChartVo());
        }
        //遍历试卷
        for (DonePaperEntity o : donePaperEntities
        ) {
            PieChartVo pieChartVo = new PieChartVo();
            // 获得真实分数
            Integer actualScore = o.getActualScore();
            //获得总分
            Integer totalScore = o.getTotalScore();
            //获得比例
            BigDecimal proportion = new BigDecimal(actualScore.toString()).divide(new BigDecimal(totalScore.toString()), 2, BigDecimal.ROUND_HALF_UP);
            if (proportion.compareTo(DonePaperConstant.ExcellentProportion) > 0) {
                if (pieChartVos.get(0).getValue() == 0) {
                    //说明是优秀
                    pieChartVo.setName("优秀");
                    pieChartVo.setValue(1);
                    pieChartVos.set(0, pieChartVo);
                } else {
                    pieChartVos.get(0).setValue(pieChartVos.get(0).getValue() + 1);
                }
            } else if (proportion.compareTo(DonePaperConstant.GoodProportion) > 0) {
                if (pieChartVos.get(1).getValue() == 0) {
                    //说明是良好
                    pieChartVo.setName("良好");
                    pieChartVo.setValue(1);
                    pieChartVos.set(1, pieChartVo);
                } else {
                    pieChartVos.get(1).setValue(pieChartVos.get(1).getValue() + 1);
                }
            } else if (proportion.compareTo(DonePaperConstant.MediumProportion) > 0) {
                if (pieChartVos.get(2).getValue() == 0) {
                    //说明是中等
                    pieChartVo.setName("中等");
                    pieChartVo.setValue(1);
                    pieChartVos.set(2, pieChartVo);
                } else {
                    pieChartVos.get(2).setValue(pieChartVos.get(2).getValue() + 1);
                }
            } else if (proportion.compareTo(DonePaperConstant.PassProportion) > 0) {
                if (pieChartVos.get(3).getValue() == 0) {
                    //说明及格
                    pieChartVo.setName("及格");
                    pieChartVo.setValue(1);
                    pieChartVos.set(3, pieChartVo);
                } else {
                    pieChartVos.get(3).setValue(pieChartVos.get(3).getValue() + 1);
                }
            } else {
                if (pieChartVos.get(4).getValue() == 0) {
                    //说明不及格
                    pieChartVo.setName("不及格");
                    pieChartVo.setValue(1);
                    pieChartVos.set(4, pieChartVo);
                } else {
                    pieChartVos.get(4).setValue(pieChartVos.get(4).getValue() + 1);
                }
                unPassCount.incrementAndGet();
            }
            //最后再排除为null的
        }

        return pieChartVos;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


    private Long getMemberId() {
        return SpringSecurityUtil.getUser().getMemberEntity().getId().longValue();
    }

}
