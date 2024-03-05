package com.hyl.component.binlog.client;


import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.hyl.component.binlog.config.MasterConfig;
import com.hyl.component.binlog.listener.BinlogEventListener;
import com.hyl.component.binlog.register.TableRegister;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BinlogClient {

    private TableRegister tableRegister;

    public BinlogClient(MasterConfig masterConfig, TableRegister tableRegister) {
        if (tableRegister == null) {
            throw new RuntimeException("tableRegister is null");
        }else if (tableRegister.isEmpty()){
            log.warn("tableRegister is empty not need to start binlog client");
            return;
        }
        this.tableRegister = tableRegister;
        //校验配置
        checkConfig(masterConfig);
        //创建连接
        connect(masterConfig);

    }

    private void checkConfig(MasterConfig masterConfig) {
        if (masterConfig == null) {
            throw new RuntimeException("masterConfig is null");
        }
        if (masterConfig.getHost() == null) {
            throw new RuntimeException("host is null");
        }
        if (masterConfig.getPort() == null) {
            throw new RuntimeException("port is null");
        }
        if (masterConfig.getUsername() == null) {
            throw new RuntimeException("username is null");
        }
        if (masterConfig.getPassword() == null) {
            throw new RuntimeException("password is null");
        }
    }

    //创建连接
    public void connect(MasterConfig masterConfig) {
        log.info("binlog client connect...");
        //创建连接
        BinaryLogClient client = new BinaryLogClient(masterConfig.getHost(), masterConfig.getPort(), masterConfig.getUsername(), masterConfig.getPassword());
        client.setServerId(masterConfig.getServerId());
        //设置binlog 格式和binlog位置
        if (masterConfig.getBinlog() != null) {
            client.setBinlogFilename(masterConfig.getBinlog().getFileName());
            client.setBinlogPosition(masterConfig.getBinlog().getLogPosition());
        }
        //注册监听器
        client.registerEventListener(new BinlogEventListener(tableRegister));
        //启动连接
        try {
            client.connect();
        } catch (Exception e) {
            throw new RuntimeException("binlog client connect error", e);
        }
        log.info("binlog client connect success");
    }

}
