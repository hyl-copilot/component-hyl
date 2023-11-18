package com.hyl.component.export.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyl.component.export.constant.ExportJobStatusEnum;
import com.hyl.component.export.dto.ExportJob;
import com.hyl.component.export.mapper.ExportJobMapper;
import com.hyl.component.export.service.ExportJobService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExportJobServiceImpl extends ServiceImpl<ExportJobMapper, ExportJob> implements ExportJobService {
    @Override
    public ExportJob getToRunJob() {
        ExportJob exportJob = getOne(new LambdaQueryWrapper<ExportJob>()
                .eq(ExportJob::getStatus, ExportJobStatusEnum.WAITING)
                .orderByAsc(ExportJob::getId)
                .last("limit 1")
        );
        return exportJob;
    }

    @Override
    public void beginExport(Long jobId) {
        update(new LambdaUpdateWrapper<ExportJob>()
                .eq(ExportJob::getId, jobId)
                .set(ExportJob::getStatus, ExportJobStatusEnum.RUNNING.getStatus())
        );
    }

    @Override
    public void exportSuccess(Long id, String url) {
        update(new LambdaUpdateWrapper<ExportJob>()
                .eq(ExportJob::getId, id)
                .set(ExportJob::getStatus, ExportJobStatusEnum.SUCCESS.getStatus())
                .set(ExportJob::getMessage, "success")
                .set(ExportJob::getUrl, url)
        );
    }

    @Override
    public void exportFail(Long jobId, String message) {
        update(new LambdaUpdateWrapper<ExportJob>()
                .eq(ExportJob::getId, jobId)
                .set(ExportJob::getStatus, ExportJobStatusEnum.FAIL.getStatus())
                .set(ExportJob::getMessage, message)
        );
    }

    @Override
    public List<ExportJob> getRunningJobs() {
        return list(new LambdaQueryWrapper<ExportJob>()
                .eq(ExportJob::getStatus, ExportJobStatusEnum.RUNNING.getStatus())
                .last("limit 100")
        );
    }
}
