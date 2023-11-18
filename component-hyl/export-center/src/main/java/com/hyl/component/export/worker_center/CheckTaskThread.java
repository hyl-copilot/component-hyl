package com.hyl.component.export.worker_center;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.hyl.component.export.annotation.ExportHandler;
import com.hyl.component.export.dto.ExportJob;
import com.hyl.component.export.dto.ExportTaskInfo;
import com.hyl.component.export.exception.NotFoundHandlerException;
import com.hyl.component.export.job_handler.JobHandler;
import com.hyl.component.export.service.ExportJobService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 检查任务线程
 */
@Slf4j
@Component
public class CheckTaskThread {

    @Resource
    private ExportJobService exportJobService;

    @Resource
    private RedissonClient redissonClient;


    @PostConstruct
    public void init() {
        log.debug("初始化检查任务线程");
        new Thread(this::start).start();
    }

    public void start() {
        while (true) {
            //查询执行中的任务
            List<ExportJob> runningJobs = exportJobService.getRunningJobs();
            if (CollUtil.isEmpty(runningJobs)) {
                log.debug("当前有执行中的任务，。。。");
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e) {
                    log.error("线程休眠失败", e);
                }
            }
            for (ExportJob runningJob : runningJobs) {
                //判断任务是否已经释放锁
                RLock lock = redissonClient.getLock("export_job_lock:" + runningJob.getId());
                if (lock.tryLock()) {
                    //释放锁
                    lock.unlock();
                    log.warn("任务：{}，已经释放锁，但是状态为执行中，将任务状态改为失败", runningJob.getId());
                    //将任务状态改为失败
                    exportJobService.exportFail(runningJob.getId(), "任务异常终止");
                }
            }
        }
    }
}
