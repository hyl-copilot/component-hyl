package com.hyl.component.export.dto;

import com.hyl.component.export.job_handler.JobHandler;
import com.hyl.component.export.service.ExportJobService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportTaskInfo {

    private RedissonClient redissonClient;
    private ExportJobService exportJobService;

    private JobHandler jobHandler;

    private ExportJob exportJob;

}
