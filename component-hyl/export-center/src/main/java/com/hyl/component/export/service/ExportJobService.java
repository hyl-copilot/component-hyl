package com.hyl.component.export.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hyl.component.export.dto.ExportJob;

import java.util.List;

public interface ExportJobService extends IService<ExportJob> {
    /**
     * 获取一个待执行的任务
     * @return
     */
    ExportJob getToRunJob();

    void beginExport(Long jobId);

    void exportSuccess(Long id, String url);

    void exportFail(Long jobId, String message);

    List<ExportJob> getRunningJobs();

}
