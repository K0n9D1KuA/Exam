package com.apxy.courseSystem.util;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.apxy.courseSystem.constant.AuthServerConstant;
import com.apxy.courseSystem.constant.PaperConstant;
import com.apxy.courseSystem.constant.SubjectConstant;
import com.apxy.courseSystem.constant.WebSocketConstant;
import com.apxy.courseSystem.entity.*;
import com.apxy.courseSystem.entity.event.UpdateWrongSubjectEvent;
import com.apxy.courseSystem.entity.redis.WorseSubject;
import com.apxy.courseSystem.entity.socket.SocketMsg;
import com.apxy.courseSystem.entity.vo.SubjectVoEntity;
import com.apxy.courseSystem.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.sf.jsqlparser.statement.select.SubJoin;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpoint;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/web-socket/{userName}")
public class WebSocketUtil {
    /**
     * 属于每个具体对象的属性
     */
    private String userName;
    private Session session;


    private static StringRedisTemplate stringRedisTemplate;

    private static PaperService paperService;

    private static DonePaperService donePaperService;

    private static MemberService memberService;

    private static DoneSubjectService doneSubjectService;

    private static WrongSubjectService wrongSubjectService;

    @Autowired
    public void setWrongSubjectService(WrongSubjectService wrongSubjectService) {
        WebSocketUtil.wrongSubjectService = wrongSubjectService;
    }

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        WebSocketUtil.stringRedisTemplate = stringRedisTemplate;
    }

    @Autowired
    public void setPaperService(PaperService paperService) {
        WebSocketUtil.paperService = paperService;
    }

    @Autowired
    public void setPaperService(DonePaperService donePaperService) {
        WebSocketUtil.donePaperService = donePaperService;
    }

    @Autowired
    public void setMemberService(MemberService memberService) {
        WebSocketUtil.memberService = memberService;
    }

    @Autowired
    public void setDoneSubjectService(DoneSubjectService doneSubjectService) {
        WebSocketUtil.doneSubjectService = doneSubjectService;
    }

    /**
     * 固定前缀
     */
    private static final String USER_NAME_PREFIX = "user_name_";

    /**
     * 用来存放每个客户端对应的MyWebSocket对象。
     **/
    private static CopyOnWriteArraySet<WebSocketUtil> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 存放Session集合，方便推送消息 （javax.websocket.Session）
     */
    private static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 私聊：向指定客户端推送消息
     */
    public synchronized static void privateMessage(SocketMsg socketMsg) {
        //接收消息的用户
        Session receiveUser = sessionMap.get(USER_NAME_PREFIX + socketMsg.getReceiveUser());
        //发送给接收者
        if (receiveUser != null) {
            //发送给接收者
            System.out.println(socketMsg.getSendOutUser() + " 向 " + socketMsg.getReceiveUser() + " 发送了一条消息：" + socketMsg.getMsg());
            receiveUser.getAsyncRemote().sendText(socketMsg.getSendOutUser() + "：" + socketMsg.getMsg());
        } else {
            //发送消息的用户
            System.out.println(socketMsg.getSendOutUser() + " 发送用户 " + socketMsg.getReceiveUser() + " 未在线");
//            Session sendOutUser = sessionMap.get(USER_NAME_PREFIX + socketMsg.getSendOutUser());
//            //将系统提示推送给发送者
//            sendOutUser.getAsyncRemote().sendText("系统消息：对方未在线");
        }
    }

    /**
     * 群聊：公开聊天记录
     *
     * @param userName 发送者的用户名称（当前用户）
     * @param message  发送的消息
     * @param flag     用来标识 是否要将消息推送给 当前用户
     */
    public synchronized static void publicMessage(String userName, String message, boolean flag) {
        for (WebSocketUtil item : webSocketSet) {
            Session session = item.session;
            if (flag) {
                session.getAsyncRemote().sendText(message);
            } else {
                //获取发送这条消息的用户
                Session currentUser = sessionMap.get(USER_NAME_PREFIX + userName);
                //消息不用推送到发送者的客户端
                if (!session.getId().equals(currentUser.getId())) {
                    session.getAsyncRemote().sendText(message);
                }
            }
        }
        System.out.println("公共频道接收了一条消息：" + message);
    }

    /**
     * 监听：连接成功
     *
     * @param session
     * @param userName 连接的用户名
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userName") String userName) {
        System.out.println(userName);
        this.userName = userName;
        this.session = session;
        sessionMap.put(USER_NAME_PREFIX + userName, session);
        webSocketSet.add(this);
//        //在线数加1
//        String tips = userName+" 加入聊天室。当前聊天室人数为" + webSocketSet.size();
//        System.out.println(tips);
//        publicMessage(userName,tips,true);
    }

    /**
     * 监听：收到客户端发送的消息
     *
     * @param message 发送的信息（json格式，里面是 SocketMsg 的信息）
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
        SocketMsg socketMsg = JSONUtil.toBean(message, SocketMsg.class);
        if (socketMsg.getType() == 1) {
            //单聊，需要找到发送者和接受者
            if (socketMsg.getIdentity().intValue() == WebSocketConstant.STUDENT_REPRESENTATIVE) {
                //假如是学生发的消息
                this.addPaperAndAddSubjectsAndChangeName(socketMsg);
            }
            privateMessage(socketMsg);
        } else {
            //群发消息
            publicMessage(socketMsg.getSendOutUser(), socketMsg.getSendOutUser() + ": " + socketMsg.getMsg(), false);
        }

    }

    /**
     * 监听: 连接关闭
     */
    @OnClose
    public void onClose() {
        if (sessionMap.containsKey(USER_NAME_PREFIX + userName)) {
            //连接关闭后，将此websocket从set中删除
            sessionMap.remove(USER_NAME_PREFIX + userName);
            webSocketSet.remove(this);
        }
        String tips = userName + " 退出聊天室。当前聊天室人数为" + webSocketSet.size();
        System.out.println(tips);
//        publicMessage(userName, tips, true);
    }

    /**
     * 监听：发生异常
     *
     * @param error
     */
    @OnError
    public void onError(Throwable error) {
        System.out.println("userName为：" + userName + "，发生错误：" + error.getMessage());
        error.printStackTrace();
    }

    //    /**
//     * 添加试卷 添加已做题目 改变发送者和接收者
//     */
//
    private void addPaperAndAddSubjectsAndChangeName(SocketMsg socketMsg) { //说明消息发送者是学生 那么需要获得学生的题目信息
        //获得试卷id
        Long paperId = socketMsg.getPaperId();
        //获得试卷信息
        PaperEntity one = paperService.getOne(new LambdaQueryWrapper<PaperEntity>().eq(PaperEntity::getId, paperId));
        DonePaperEntity donePaperEntity = new DonePaperEntity();
        //设置试卷类型未为批改
        donePaperEntity.setPaperType(PaperConstant.UNCOMPLETED);
        donePaperEntity.setPaperId(socketMsg.getPaperId());
        BeanUtils.copyProperties(one, donePaperEntity);
        donePaperEntity.setStudentId(Long.valueOf(socketMsg.getSendOutUser()));
        donePaperService.save(donePaperEntity);
        //将发送者和接收者的名字查出来
        MemberEntity sendOutUser = memberService.getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getId, socketMsg.getSendOutUser()));
        MemberEntity receiver = memberService.getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getId, socketMsg.getReceiveUser()));
        socketMsg.setSendOutUser(sendOutUser.getMemberName());
        socketMsg.setReceiveUser(receiver.getMemberName());

        String key = AuthServerConstant.PAPER_PREFIX_KEY + socketMsg.getSendOutUser();
        //获得题目数量
        Long subjectCount = stringRedisTemplate.opsForList().size(key);
        //获得所有的题目
        List<String> range = stringRedisTemplate.opsForList().range(key, 0, subjectCount - 1);
        range.forEach(System.out::println);
        //删除redis中的题目
        stringRedisTemplate.delete(key);
        range.forEach(o -> {
            SubjectVoEntity subjectVoEntity = JSON.parseObject(o, SubjectVoEntity.class);
//            System.out.println(subjectVoEntity);
            if (subjectVoEntity.getSubjectType() != SubjectConstant.SHORT_ANSWER_SUBJECT) {
                //不是大题的情况下需要更新错题频率
                this.addWrongAnswerToMysql(subjectVoEntity);
            }
            DoneSubject doneSubject = new DoneSubject();
            BeanUtils.copyProperties(subjectVoEntity, doneSubject);
            doneSubject.setDonePaperId(donePaperEntity.getId().longValue());
            doneSubject.setActualScore(subjectVoEntity.getSubjectAnwser().equals(subjectVoEntity.getSelectAnswer()) ? Integer.valueOf(subjectVoEntity.getScore().intValue()) : 0);
            if (subjectVoEntity.getSubjectType() != 4) {
                donePaperEntity.setActualScore(donePaperEntity.getActualScore() + doneSubject.getActualScore());
                donePaperService.updateById(donePaperEntity);
            }
            doneSubjectService.save(doneSubject);
            //selectAnswer就为空
//            System.out.println(doneSubject);
        });

        System.out.println("-------------------------------------------------主线程执行完毕------------------------------------------------------------");
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

//    private BoundHashOperations<String, Object, Object> getWrongAnswerOps(String userName) {
//        String keyPrefix = SubjectConstant.WRONG_ANSWER_FREQUENCY_PREFIX_KEY;
//        String key = keyPrefix + userName;
//        //绑定哈希操作
//        return stringRedisTemplate.boundHashOps(key);
//    }

//    private void saveWrongAnswerAndFrequnencyToRedis(SubjectVoEntity subjectVoEntity, BoundHashOperations<String, Object, Object> wrongAnswerOps) {
//        Long subjectId = subjectVoEntity.getId();
//        String selectAnswer = subjectVoEntity.getSelectAnswer();
//        //需要判断题目是否已经添加过
//        Object object = wrongAnswerOps.get(subjectId.toString());
//        if (Objects.isNull(object)) {
//            //说明没有添加过这个题目
//            WorseSubject worseSubject = new WorseSubject();
//            worseSubject.setSubjectId(subjectId);
//            if (!"".equals(subjectVoEntity.getSelectAnswer()) && !subjectVoEntity.getSubjectAnwser().equals(subjectVoEntity.getSelectAnswer())) {
//                //题目错了 那么添加错误次数1 同时记录错误答案及其次数 直接存入redis中
//                //创建题目——频率
//                Map<String, Integer> wrongAnswerFrequency = new HashMap<>();
//                worseSubject.setWrongCount(1);
//                Integer orDefault = wrongAnswerFrequency.getOrDefault(selectAnswer, 0);
//                wrongAnswerFrequency.put(selectAnswer, orDefault + 1);
//                worseSubject.setWrongAnswerFrequency(wrongAnswerFrequency);
//            } else if (!"".equals(subjectVoEntity.getSelectAnswer()) && subjectVoEntity.getSubjectAnwser().equals(subjectVoEntity.getSelectAnswer())) {
//                //说明答案没错 那么添加正确次数1 直接存入redis中
//                worseSubject.setRightCount(1);
//            }
//            wrongAnswerOps.put(subjectId.toString(), JSON.toJSONString(worseSubject));
//        } else {
//            //说明已经添加过这个题目
//            //获得该题目
//            WorseSubject worseSubject = JSON.parseObject(object.toString(), WorseSubject.class);
//            if (!"".equals(subjectVoEntity.getSelectAnswer()) && !subjectVoEntity.getSubjectAnwser().equals(subjectVoEntity.getSelectAnswer())) {
//                //说明题目错了 那么设置错误次数+1 同时记录错误答案及其次数 直接存入redis中
//                Map<String, Integer> wrongAnswerFrequency = worseSubject.getWrongAnswerFrequency();
//                Integer orDefault = wrongAnswerFrequency.getOrDefault(selectAnswer, 0);
//                wrongAnswerFrequency.put(selectAnswer, orDefault + 1);
//                worseSubject.setWrongCount(worseSubject.getWrongCount() + 1);
//                worseSubject.setWrongAnswerFrequency(wrongAnswerFrequency);
//            } else if (!"".equals(subjectVoEntity.getSelectAnswer()) && subjectVoEntity.getSubjectAnwser().equals(subjectVoEntity.getSelectAnswer())) {
//                //说明题目没错 那么设置正确次数+1 直接存入redis中
//                worseSubject.setRightCount(worseSubject.getRightCount() + 1);
//            }
//            //存入redis中
//            wrongAnswerOps.put(subjectId.toString(), JSON.toJSONString(worseSubject));
//        }
//
//    }
}
