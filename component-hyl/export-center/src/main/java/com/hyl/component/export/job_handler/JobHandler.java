package com.hyl.component.export.job_handler;


import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public abstract class JobHandler<T> {


    public String execute(String params){
        if (params == null) {
            return export(null);
        }
        if (!JSONUtil.isTypeJSON(params)){
            return export((T) params);
        }
        T t = JSONUtil.toBean(params, new TypeReference<T>() {}, false);
        return export(t);
    }


    /**
     * @Description: 执行导出任务
     * @param param
     * @return: java.lang.String 文件下载地址
     */
    public abstract String export(T param);

}
