package com.hyl.component.export.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("export_job")
public class ExportJob {
    private Long id;
    private String name;
    private String jobHandler;
    private String jobParams;
    private Integer pageSize;
    private Integer status;
    private String url;
    private String message;

}
