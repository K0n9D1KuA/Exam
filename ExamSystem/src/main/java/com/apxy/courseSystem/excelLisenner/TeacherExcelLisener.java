package com.apxy.courseSystem.excelLisenner;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.apxy.courseSystem.entity.excel.Member;
import com.apxy.courseSystem.service.MemberService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class TeacherExcelLisener implements ReadListener<Member> {

    /**
     * 每隔100条存储数据库，，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;
    /**
     * 缓存的数据
     */
    private List<Member> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 和数据库打交道
     */
    private MemberService memberService;
    public TeacherExcelLisener(MemberService memberService)
    {
        this.memberService = memberService;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     */
    @Override
    public void invoke(Member member, AnalysisContext analysisContext) {
        log.info("解析到一条数据:{}", JSON.toJSONString(member));
        cachedDataList.add(member);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用

     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        memberService.saveTeacherByExcel(this.cachedDataList);
        log.info("存储数据库成功！");
    }
}
