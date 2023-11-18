package com.hyl.component.export.worker_center;

import cn.hutool.json.JSONObject;
import com.hyl.component.export.dto.ExportJob;
import com.hyl.component.export.dto.ExportTaskInfo;
import com.hyl.component.export.job_handler.JobHandler;
import com.hyl.component.export.service.ExportJobService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

@Slf4j
public class TaskThread implements Runnable {

    private ExportTaskInfo taskInfo;



    public TaskThread(ExportTaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }


    @Override
    public void run() {
        ExportJob exportJob = taskInfo.getExportJob();
        //获取锁
        RedissonClient redissonClient = taskInfo.getRedissonClient();
        RLock lock = redissonClient.getLock("export_job_lock:" + exportJob.getId());
        if (!lock.tryLock()) {
            log.warn("任务[{}]获取锁失败", exportJob.getId());
            //获取锁失败
            return;
        }
        ExportJobService exportJobService = taskInfo.getExportJobService();
        JobHandler jobHandler = taskInfo.getJobHandler();

        //更新任务状态
        exportJobService.beginExport(exportJob.getId());
        //执行任务
        try {
            String url = jobHandler.execute(exportJob.getJobParams());
            //更新任务状态
            exportJobService.exportSuccess(exportJob.getId(), url);
        } catch (Exception e) {
            //更新任务状态
            exportJobService.exportFail(exportJob.getId(), e.getMessage());
        } finally {
            //释放锁
            lock.unlock();
        }
    }
}
