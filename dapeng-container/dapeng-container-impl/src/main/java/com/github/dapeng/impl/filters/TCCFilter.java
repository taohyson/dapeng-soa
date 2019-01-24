package com.github.dapeng.impl.filters;

import com.github.dapeng.client.netty.JsonPost;
import com.github.dapeng.core.*;
import com.github.dapeng.core.filter.Filter;
import com.github.dapeng.core.filter.FilterChain;
import com.github.dapeng.core.filter.FilterContext;
import com.github.dapeng.core.helper.SoaSystemEnvProperties;
import com.github.dapeng.core.metadata.Service;
import com.github.dapeng.json.OptimizedMetadata;
import com.github.dapeng.metadata.MetadataClient;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import javax.xml.bind.JAXB;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

/**
 * @Author: zhup
 * @Date: 2019/1/17 2:23 PM
 */


public class TCCFilter implements Filter {

    private final String tmServiceName = "com.github.dapeng.tm.service.TransactionManagerService";
    private final String tmVersionName = "1.0.0";
    private final String tmMethodName = "beginTransaction";
    private final String tmConfirmName = "confirm";
    private final String tmCancelName = "cancel";
    private final Gson gson = new Gson();

    @Override
    public void onEntry(FilterContext ctx, FilterChain next) throws SoaException {
        try {
            TransactionContext transactionContext = (TransactionContext) ctx.getAttribute("context");
            Application application = (Application) ctx.getAttribute("application");
            SoaHeader header = transactionContext.getHeader();
            String methodName = header.getMethodName();
            String versionName = header.getVersionName();
            String serviceName = header.getServiceName();
            Optional<ServiceInfo> serviceInfo = application.getServiceInfo(serviceName, versionName);
            TCC tcc = serviceInfo.get().tccMap.get(methodName);
            if (tcc != null) {
                //构建JsonPost请求
                JsonPost jsonPost = new JsonPost(tmServiceName, tmVersionName, tmMethodName);
                MetadataClient client = new MetadataClient(serviceName, versionName);
                String metadata = client.getServiceMetadata();
                OptimizedMetadata.OptimizedService service = null;
                Object args = transactionContext.getAttribute("args");
                if (metadata != null) {
                    try (StringReader reader = new StringReader(metadata)) {
                        service = new OptimizedMetadata.OptimizedService(JAXB.unmarshal(reader, Service.class));
                    }
                }
                //构建beginRequest请求参数
                Map<String, Object> map = new HashMap<>();
                map.put("method", methodName);
                map.put("version", serviceName);
                map.put("serviceName", serviceName);

                map.put("confirmMethod", Optional.of(tcc.confirmMethod()));
                map.put("cancelMethod", Optional.of(tcc.cancelMethod()));
                map.put("expiredAt", Optional.of(60000));
                map.put("params", Optional.of(args));

                String jsonResponse = jsonPost.callServiceMethod(gson.toJson(map), service);
                Map resultMap = gson.fromJson(jsonResponse, Map.class);
                Object gtxId = resultMap.get("gtxId");
                Object stepId = resultMap.get("stepId");
                header.addCookie("gtxId", gtxId.toString());
                //构建子事务序列栈
                Stack stack = null;
                if (transactionContext.getAttribute("stack") != null) {
                    stack = (Stack) transactionContext.getAttribute("stack");
                } else {
                    stack = new Stack();
                }
                stack.push(stepId);
            }
            next.onEntry(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onExit(FilterContext ctx, FilterChain prev) throws SoaException {
        try {
            TransactionContext transactionContext = (TransactionContext) ctx.getAttribute("context");
            Application application = (Application) ctx.getAttribute("application");
            SoaHeader header = transactionContext.getHeader();
            String methodName = header.getMethodName();
            String versionName = header.getVersionName();
            String serviceName = header.getServiceName();
            Optional<ServiceInfo> serviceInfo = application.getServiceInfo(serviceName, versionName);
            TCC tcc = serviceInfo.get().tccMap.get(methodName);
            if (tcc != null) {
                Stack stack = (Stack) transactionContext.getAttribute("stack");
                stack.pop();
                MetadataClient client = new MetadataClient(serviceName, versionName);
                String metadata = client.getServiceMetadata();
                OptimizedMetadata.OptimizedService service = null;
                JsonPost jsonPost = null;
                if (stack.empty() && header.getRespCode().equals(SoaSystemEnvProperties.SOA_NORMAL_RESP_CODE)) {
                    jsonPost = new JsonPost(tmServiceName, tmVersionName, tmConfirmName);
                    if (metadata != null) {
                        try (StringReader reader = new StringReader(metadata)) {
                            service = new OptimizedMetadata.OptimizedService(JAXB.unmarshal(reader, Service.class));
                        }
                    }
                } else {
                    jsonPost = new JsonPost(tmServiceName, tmVersionName, tmCancelName);
                    if (metadata != null) {
                        try (StringReader reader = new StringReader(metadata)) {
                            service = new OptimizedMetadata.OptimizedService(JAXB.unmarshal(reader, Service.class));
                        }
                    }
                }
                long gtxId = Long.parseLong(transactionContext.getAttribute("gtxId").toString());
                Map<String, Long> map = new <String, Long>HashMap();
                map.put("gtxId", gtxId);
                jsonPost.callServiceMethod(gson.toJson(map), service);
            }

            prev.onExit(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}