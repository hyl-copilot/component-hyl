package com.hyl.component.export.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExportJobStatusEnum {

    /**
     * 任务状态
     */

    //"等待中
    WAITING(0),
    //运行中
    RUNNING(1),
    //成功
    SUCCESS(2),
    //失败
    FAIL(3),
    //取消
    CANCEL(4),
    ;
    private Integer status;

}
