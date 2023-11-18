package com.hyl.component.export.worker_center;

import cn.hutool.core.util.StrUtil;
import com.hyl.component.export.annotation.ExportHandler;
import com.hyl.component.export.dto.ExportJob;
import com.hyl.component.export.dto.ExportTaskInfo;
import com.hyl.component.export.exception.NotFoundHandlerException;
import com.hyl.component.export.job_handler.JobHandler;
import com.hyl.component.export.service.ExportJobService;
import lombok.extern.slf4j.Slf4j;
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
 * 主线程
 * 1. 从数据库中获取任务
 * 2. 将任务分发给子线程
 */
@Slf4j
@Component
public class CoreThread {

    @Resource
    private ExportJobService exportJobService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAXIMUM_POOL_SIZE = 2;


    /**
     * 导出任务线程池
     * 核心线程数：1 最大线程数：2  空闲线程存活时间：1分钟 任务队列：1个任务 任务拒绝策略：抛出异常
     */
    public static final ThreadPoolExecutor exportJobPool =
            new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                    1, TimeUnit.MINUTES,
                    new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.AbortPolicy());


    private static final Map<String, JobHandler> handlerMap = new HashMap<>(16);


    @Autowired
    public CoreThread(List<JobHandler> handlers) {
        for (JobHandler handler : handlers) {
            //获取ExportHandler注解的值
            ExportHandler annotation = handler.getClass().getAnnotation(ExportHandler.class);
            String handlerName = Optional.ofNullable(annotation).map(ExportHandler::value).orElse(handler.getClass().getSimpleName());
            //判断是否有重复的handlerName
            if (handlerMap.containsKey(handlerName)) {
                throw new RuntimeException("存在重复的handlerName 请修改Handler类名或者使用ExportHandler注解");
            }
            handlerMap.put(handlerName, handler);
        }
    }


    @PostConstruct
    public void init() {
        log.info("初始化任务线程池");
        exportJobPool.prestartAllCoreThreads();
        new Thread(this::start).start();
    }

    public void start() {
        log.debug("启动导出中心核心线程");
        while (true) {
            try {
                // 获取活跃线程数
                int activeCount = exportJobPool.getActiveCount();
                log.debug("当前活跃线程数：{}", activeCount);
                if (activeCount == MAXIMUM_POOL_SIZE) {
                    // 如果活跃线程数等于最大线程数，休眠10秒
                    log.debug("当前活跃线程数等于最大线程数，休眠10秒。。。");
                    Thread.sleep(10000);
                    continue;
                }
                executeJob();
                // 休眠1秒
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error("线程休眠异常", e);
            } catch (RejectedExecutionException e) {
                //任务分发失败，休眠10秒
                log.debug("当前任务队列已满，休眠10秒。。。");
            }
        }
    }

    private void executeJob() throws InterruptedException {
        // 从数据库中获取任务
        ExportJob exportJob = exportJobService.getToRunJob();
        if (Objects.isNull(exportJob)) {
            // 如果没有任务，休眠10秒
            log.debug("当前没有任务，休眠10秒。。。");
            Thread.sleep(10000);
            return;
        }
        try {
            // 将任务分发给子线程
            ExportTaskInfo exportTaskInfo = ExportTaskInfo.builder()
                    .redissonClient(redissonClient)
                    .exportJobService(exportJobService)
                    .jobHandler(getJobHandler(exportJob))
                    .exportJob(exportJob)
                    .build();
            log.info("分发任务：{} 由 {} 处理", exportJob.getId(), exportJob.getJobHandler());
            exportJobPool.execute(new TaskThread(exportTaskInfo));
        } catch (NotFoundHandlerException e) {
            //任务处理器未找到，休眠10秒
            log.warn("任务处理器未找到，休眠10秒。。。");
            exportJobService.exportFail(exportJob.getId(), e.getMessage());
        } catch (RejectedExecutionException e) {
            //任务分发失败，休眠10秒
            log.info("当前任务队列已满，休眠10秒。。。");
            throw e;
        } catch (Exception e) {
            log.error("任务分发失败", e);
            exportJobService.exportFail(exportJob.getId(), e.getMessage());
        }

    }

    /**
     * 根据任务类型获取任务处理器
     *
     * @param exportJob
     * @return
     */
    private JobHandler getJobHandler(ExportJob exportJob) {
        if (!handlerMap.containsKey(exportJob.getJobHandler())) {
            throw new NotFoundHandlerException(StrUtil.format("未找到任务处理器：{}", exportJob.getJobHandler()));
        }
        return handlerMap.get(exportJob.getJobHandler());
    }

}
