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
import com.apxy.courseSystem.util.R;
import com.apxy.courseSystem.util.SpringSecurityUtil;
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
    private SpringSecurityUtil springSecurityUtil;
    @Autowired
    private MemberService memberService;
    @Lazy
    @Autowired
    private DoneSubjectService doneSubjectService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    /**
     * Caused by: org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'doneSubjectServiceimpl':
     * Bean with name 'doneSubjectServiceimpl' has been injected into other beans [donePaperServiceImpl] in its raw version as part of a circular reference,
     * but has eventually been wrapped.
     * This means that said other beans do not use the final version of the bean.
     * This is often the result of over-eager type matching - consider using 'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.
     */

    /**
     * ???????????????????????????id ?????????????????????????????? ???
     *
     * @param donePaperId ??????id
     * @return ???????????????????????????????????????
     */
    @Override
    public List<DoneSubjectVo> getAllDoneSubjectsWithDonePaperId(Long donePaperId) {
        //???????????????????????????
        List<DoneSubject> list = doneSubjectService.list(new LambdaQueryWrapper<DoneSubject>().eq(DoneSubject::getDonePaperId, donePaperId));
        //????????????
        return list.stream().map(o -> {
            DoneSubjectVo doneSubjectVo = new DoneSubjectVo();
            BeanUtils.copyProperties(o, doneSubjectVo);
            //???????????????
            if (o.getSubjectType().equals(SubjectConstant.SHORT_ANSWER_SUBJECT)) {
                //??????https://wcity.oss-cn-hangzhou.aliyuncs.com/2022/09/15/8dd4874328434bf68575957e84bf577fGw8vk5H6VQ3i47edb94a078b690135ce0c0b1385a0e8.jpeg;https://wcity.oss-cn-hangzhou.aliyuncs.com/2022/09/15/fdab27a373a24804bcd34e7cb0e206adRxKy0zCBjWVT2837b79b3b2c35ed910ffd41cefc3bd3.jpg;
                String selectAnswer = doneSubjectVo.getSelectAnswer();
                List<String> strings = new ArrayList<>(Arrays.asList(selectAnswer.split(";")));
                strings = strings.stream().filter(j -> {
                    return !"".equals(j);
                }).collect(Collectors.toList());
                String[] temp = new String[strings.size()];
                for (int i = 0; i < strings.size(); i++) {
                    temp[i] = strings.get(i);
                }
                doneSubjectVo.setStudentImages(temp);
            }
            return doneSubjectVo;
        }).collect(Collectors.toList());
    }

    /**
     * ??????????????????id ?????? ???????????? + ????????????????????????????????????
     *
     * @param donePaperId ????????????id
     * @return ???????????? + ????????????????????????????????????
     */
    @Override
    public R getDonePaperAndDoneSubjects(Long donePaperId) {
        List<DoneSubject> list = doneSubjectService.list(new LambdaQueryWrapper<DoneSubject>().eq(DoneSubject::getDonePaperId, donePaperId));
        DonePaperEntity byId = this.getById(donePaperId);
        return R.ok().put("donePaperEntity", byId).put("doneSubjectList", list);

    }

    /**
     * ??????:????????????????????????????????????
     *
     * @return ??????????????????????????????(????????????????????????)
     */
    @Override
    public List<DonePaperVo> getDonePapers() {
        List<DonePaperEntity> list = this.list(new LambdaQueryWrapper<DonePaperEntity>().eq(DonePaperEntity::getStudentId, this.getMemberId()));
        return list.stream().map(o -> {
            DonePaperVo donePaperVo = new DonePaperVo();
            BeanUtils.copyProperties(o, donePaperVo);
            //????????????id
            Long teacherId = donePaperVo.getTeacherId();
            MemberEntity byId = memberService.getById(teacherId);
            String teacherName = byId.getMemberName();
            donePaperVo.setTeacherName(teacherName);
            return donePaperVo;
        }).collect(Collectors.toList());

    }

    /**
     * ?????????????????????id  ?????????????????????????????????????????????
     *
     * @param donePaperId ??????id
     * @return ????????????
     */
    @Override
    public List<RankVo> getRankByPaperId(Long donePaperId) {
        //??????????????????
        List<DonePaperEntity> list = this.list(new LambdaQueryWrapper<DonePaperEntity>().eq(DonePaperEntity::getPaperId, donePaperId));
        //????????????
        list.sort((o1, o2) -> {
            return o2.getActualScore() - o1.getActualScore();
        });
        List<RankVo> ret = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            DonePaperEntity current = list.get(i);
            RankVo rankVo = new RankVo();
            rankVo.setScore(current.getActualScore());
            rankVo.setPosition(i + 1);
            //??????id
            Long studentId = current.getStudentId();
            MemberEntity byId1 = memberService.getById(studentId);
            rankVo.setStudentName(byId1.getMemberName());
            ret.add(rankVo);
        }
        //????????????????????????

        return ret;
    }

    @Override
    public R getDonePaper() {
        //?????????????????????id
        Long memberId = this.getMemberId();
        //????????????????????????????????????????????????
        LambdaQueryWrapper<DonePaperEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DonePaperEntity::getTeacherId, memberId);
        List<DonePaperEntity> list = this.list(lambdaQueryWrapper);
        if (list.size() == 0) {
            //???????????????????????????
            return R.ok().put("data", Collections.emptyList());
        } else {
            //????????????id??????
            return R.ok().put("data", list.stream().filter(distinctByKey(DonePaperEntity::getPaperId)).collect(Collectors.toList()));
        }

    }

    /**
     * ????????????????????????????????????
     * ???????????????
     * @param paperId
     * @return
     */
    @Override
    public R getDonePaperDetail(Long paperId) throws ExecutionException, InterruptedException {
        //1,????????????
        //?????????
        BigDecimal maxScore = new BigDecimal("0");
        //???????????????
        String maxScoreStudentName = new String("");
        //???????????????
        String minScoreStudentName = new String("");
        //?????????
        BigDecimal minScore = new BigDecimal("0");
        //?????????
        BigDecimal average = new BigDecimal("0");
        //?????????
        StaticVo staticVo = new StaticVo();
        //??????????????????
        List<DonePaperEntity> donePaperEntities = new ArrayList<>();
        //???????????????
        AtomicInteger unPassCount = new AtomicInteger(0);
        //????????????[??????]
        List<RankVo> rankVos = new ArrayList<>();
        //???????????? ?????????
        final List<PieChartVo> pieChartVos = new ArrayList<>(5);
        //?????????
        BigDecimal midScore = new BigDecimal("0");
        //?????????
        BigDecimal passPercent = new BigDecimal("0");
        //???????????????
        Integer peopleCount = 0;
        //donePaperIds
        List<Integer> donePaperIds = new ArrayList<>();
        //doneSubjects
        List<DoneSubject> doneSubjects = new ArrayList<>();
        //???????????????
        List<SubjectsSituation> subjectsSituation = new ArrayList<>();


        //2,????????????

        CompletableFuture<StaticVo> getStaticVoFuture = CompletableFuture.supplyAsync(() -> {
            return this.getStaticVo(paperId);
        }, threadPoolExecutor);

        //????????????????????????
        //??????????????????????????? ????????????????????????
        CompletableFuture<List<DonePaperEntity>> getDonePaperEntityFuture = CompletableFuture.supplyAsync(() -> {
            return this.getDonePaperEntities(paperId, donePaperIds);
        }, threadPoolExecutor);

        //????????????
        //??????????????????????????? ?????????getDonePaperEntityFuture
        CompletableFuture<Void> getRankVosFuture = getDonePaperEntityFuture.thenAcceptAsync(result -> {
            this.getRankVos(result, rankVos);
        }, threadPoolExecutor);
        //???????????????
        //??????????????????????????? ?????????getDonePaperEntityFuture
        CompletableFuture<Void> getPieCharVosFuture = getDonePaperEntityFuture.thenAcceptAsync(result -> {
            this.getPieCharVos(result, unPassCount, pieChartVos);
        }, threadPoolExecutor);
        //????????????????????????
        //??????????????????????????? ?????????getDonePaperEntityFuture
        CompletableFuture<Void> getDoneSubjectByDonePaperIdsFuture = getDonePaperEntityFuture.thenAcceptAsync(result -> {
            this.getDoneSubjectByDonePaperIds(donePaperIds, subjectsSituation);
        }, threadPoolExecutor);

        //????????????????????????????????????
        CompletableFuture.allOf(getStaticVoFuture, getPieCharVosFuture, getRankVosFuture, getDoneSubjectByDonePaperIdsFuture).get();
        staticVo = getStaticVoFuture.get();
        donePaperEntities = getDonePaperEntityFuture.get();

        //3,??????????????????
        //?????????null
        for (int i = 0; i < pieChartVos.size(); i++) {
            if (pieChartVos.get(i).getValue() == 0) {
                pieChartVos.set(i, null);
            }
        }
        //???????????????
        peopleCount = donePaperEntities.size();
        //???????????????
        passPercent = new BigDecimal("100").multiply(new BigDecimal(peopleCount - unPassCount.get()).divide(new BigDecimal(peopleCount),2, BigDecimal.ROUND_HALF_UP));
        //???????????????
        maxScore = new BigDecimal(donePaperEntities.get(0).getActualScore().toString());
        maxScoreStudentName = memberService.getById(donePaperEntities.get(0).getStudentId()).getMemberName();
        //???????????????
        minScore = new BigDecimal(donePaperEntities.get(donePaperEntities.size() - 1).getActualScore().toString());
        minScoreStudentName = memberService.getById(donePaperEntities.get(donePaperEntities.size() - 1).getStudentId()).getMemberName();
        for (DonePaperEntity o : donePaperEntities) {
            average = average.add(new BigDecimal(o.getActualScore().toString()));
        }
        //???????????????
        average = average.divide(new BigDecimal(peopleCount.toString()),2, BigDecimal.ROUND_HALF_UP);
        //???????????????
        if (peopleCount % 2 == 0) {
            //???????????????????????? ????????????????????? (mid+mid-1)/2
            int mid = peopleCount / 2;
            midScore = new BigDecimal(donePaperEntities.get(mid - 1).getActualScore().toString()).add(new BigDecimal(donePaperEntities.get(mid).getActualScore().toString())).divide(new BigDecimal("2"),2, BigDecimal.ROUND_HALF_UP);
        } else {
            int mid = peopleCount / 2;
            //???????????????????????? ?????????????????????(mid)
            midScore = new BigDecimal(donePaperEntities.get(mid).getActualScore().toString());
        }

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

    //??????donePaperIds ??????????????????doneSubject ??????????????????????????????
    private void getDoneSubjectByDonePaperIds(List<Integer> donePaperIds, List<SubjectsSituation> subjectsSituation) {
        List<DoneSubject> doneSubjectList = doneSubjectService.list(new LambdaQueryWrapper<DoneSubject>().in(DoneSubject::getDonePaperId, donePaperIds));
        //??????????????????
        int subjectCount = doneSubjectList.size() / donePaperIds.size();
        //?????????????????????0
        for (int i = 0; i < subjectCount; i++) {
            subjectsSituation.add(new SubjectsSituation());
        }
        for (int i = 0; i < doneSubjectList.size(); i++) {
            //????????????
            DoneSubject currentSubject = doneSubjectList.get(i);
            if (currentSubject.getScore().intValue() == currentSubject.getActualScore()) {
                //??????????????????
                subjectsSituation.get(i % subjectCount).setRightCount(subjectsSituation.get(i % subjectCount).getRightCount() + 1);
            }
        }
        //???????????????
        subjectsSituation.forEach(o -> {
            //???????????????
            BigDecimal rightPercent = new BigDecimal(o.getRightCount().toString()).divide(new BigDecimal(((Integer) donePaperIds.size()).toString()),2, BigDecimal.ROUND_HALF_UP);
            o.setRightPercent(new BigDecimal(rightPercent.toString()).multiply(new BigDecimal("100")));
        });

    }

    //???????????????????????? ????????????????????? ?????????
    private List<DonePaperEntity> getDonePaperEntities(Long paperId,
                                                       List<Integer> donePaperIds) {
        //????????????????????????
        LambdaQueryWrapper<DonePaperEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DonePaperEntity::getPaperId, paperId);
        List<DonePaperEntity> donePaperEntities = this.list(lambdaQueryWrapper);
        //?????? ??????
        Collections.sort(donePaperEntities, (o1, o2) -> {
            return o2.getActualScore() - o1.getActualScore();
        });
        //???????????????id
        donePaperEntities.forEach(o -> {
            donePaperIds.add(o.getId());
        });
        return donePaperEntities;

    }

    //??????????????????
    private StaticVo getStaticVo(Long paperId) {
        DonePaperMapper baseMapper = this.getBaseMapper();
        List<ScoreAndPeopleCount> scoreAndPeopleCount = baseMapper.getDonePaperDetail(paperId);
        StaticVo staticVo = new StaticVo();
        for (ScoreAndPeopleCount o : scoreAndPeopleCount) {
            staticVo.getScores().add(o.getScore() + "???");
            staticVo.getPeopleCount().add(o.getPeopleCount());
//            //???????????????++
//            peopleCount.incrementAndGet();
        }
        return staticVo;
    }

    //????????????(??????)
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

    //??????????????????????????????????????????????????????
    private List<PieChartVo> getPieCharVos(List<DonePaperEntity> donePaperEntities, AtomicInteger unPassCount, List<PieChartVo> pieChartVos) {
        //????????????
        for (int i = 0; i < 5; i++) {
            pieChartVos.add(new PieChartVo());
        }
        //????????????
        for (DonePaperEntity o : donePaperEntities
        ) {
            PieChartVo pieChartVo = new PieChartVo();
            // ??????????????????
            Integer actualScore = o.getActualScore();
            //????????????
            Integer totalScore = o.getTotalScore();
            //????????????
            BigDecimal proportion = new BigDecimal(actualScore.toString()).divide(new BigDecimal(totalScore.toString()),2, BigDecimal.ROUND_HALF_UP);
            if (proportion.compareTo(DonePaperConstant.ExcellentProportion) > 0) {
                if (pieChartVos.get(0).getValue() == 0) {
                    //???????????????
                    pieChartVo.setName("??????");
                    pieChartVo.setValue(1);
                    pieChartVos.set(0, pieChartVo);
                } else {
                    pieChartVos.get(0).setValue(pieChartVos.get(0).getValue() + 1);
                }
            } else if (proportion.compareTo(DonePaperConstant.GoodProportion) > 0) {
                if (pieChartVos.get(1).getValue() == 0) {
                    //???????????????
                    pieChartVo.setName("??????");
                    pieChartVo.setValue(1);
                    pieChartVos.set(1, pieChartVo);
                } else {
                    pieChartVos.get(1).setValue(pieChartVos.get(1).getValue() + 1);
                }
            } else if (proportion.compareTo(DonePaperConstant.MediumProportion) > 0) {
                if (pieChartVos.get(2).getValue() == 0) {
                    //???????????????
                    pieChartVo.setName("??????");
                    pieChartVo.setValue(1);
                    pieChartVos.set(2, pieChartVo);
                } else {
                    pieChartVos.get(2).setValue(pieChartVos.get(2).getValue() + 1);
                }
            } else if (proportion.compareTo(DonePaperConstant.PassProportion) > 0) {
                if (pieChartVos.get(3).getValue() == 0) {
                    //????????????
                    pieChartVo.setName("??????");
                    pieChartVo.setValue(1);
                    pieChartVos.set(3, pieChartVo);
                } else {
                    pieChartVos.get(3).setValue(pieChartVos.get(3).getValue() + 1);
                }
            } else {
                if (pieChartVos.get(4).getValue() == 0) {
                    //???????????????
                    pieChartVo.setName("?????????");
                    pieChartVo.setValue(1);
                    pieChartVos.set(4, pieChartVo);
                } else {
                    pieChartVos.get(4).setValue(pieChartVos.get(4).getValue() + 1);
                }
                unPassCount.incrementAndGet();
            }
            //??????????????????null???
        }

        return pieChartVos;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


    private Long getMemberId() {
        return springSecurityUtil.getUser().getMemberEntity().getId().longValue();
    }

}
