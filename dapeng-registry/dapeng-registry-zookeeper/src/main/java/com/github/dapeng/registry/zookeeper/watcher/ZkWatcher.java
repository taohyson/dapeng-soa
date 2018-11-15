package com.github.dapeng.registry.zookeeper.watcher;

import com.github.dapeng.registry.zookeeper.ClientZk;
import com.github.dapeng.registry.zookeeper.ZkServiceInfo;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author maple 2018.09.04 下午3:58
 */
public class ZkWatcher implements Watcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkWatcher.class);
    private final ZkServiceInfo zkServiceInfo;


    public ZkWatcher(ZkServiceInfo zkServiceInfo) {
        this.zkServiceInfo = zkServiceInfo;
    }

    @Override
    public void process(WatchedEvent event) {
        LOGGER.warn("ZkWatcher::process, zkServiceInfo status: " + zkServiceInfo.getStatus() + ", zkEvent: " + event);
        if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
            synchronized (zkServiceInfo) {
                if (zkServiceInfo.getStatus() != ZkServiceInfo.Status.OUT_OF_SYNC) {
                    LOGGER.warn("{}::syncZkRuntimeInfo[{}]::子节点发生变化，重新获取信息,event:{}",
                            getClass().getSimpleName(), zkServiceInfo.getService(), event);
                    if (zkServiceInfo.getStatus() == ZkServiceInfo.Status.TRANSIENT) {
                        zkServiceInfo.setStatus(ZkServiceInfo.Status.OUT_OF_SYNC);
                    } else {
                        ClientZk.getMasterInstance().syncZkRuntimeInfo(zkServiceInfo);
                    }
                }
            }
        } else if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
            synchronized (zkServiceInfo) {
                if (zkServiceInfo.getStatus() != ZkServiceInfo.Status.OUT_OF_SYNC) {
                    LOGGER.warn("{}::syncZkConfigInfo[{}]::节点内容发生变化，重新获取配置信息,event:{}",
                            getClass().getSimpleName(), zkServiceInfo.getService(), event);
                    if (zkServiceInfo.getStatus() == ZkServiceInfo.Status.TRANSIENT) {
                        zkServiceInfo.setStatus(ZkServiceInfo.Status.OUT_OF_SYNC);
                    } else {
                        ClientZk.getMasterInstance().syncZkConfigInfo(zkServiceInfo);
                    }
                }
            }
        }
        LOGGER.warn("ZkWatcher::process after, zkServiceInfo status: " + zkServiceInfo.getStatus());
    }
}
