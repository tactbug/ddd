package com.tactbug.ddd.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownServiceException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/4 0:11
 */
public class IdUtil {

    private String application;
    private Class<?> aggregate;
    private Integer maxSize = 150000;
    private Integer warningSize = 20000;
    private Integer perQuantity = 50000;

    private static final ConcurrentHashMap<Class<?>, PriorityBlockingQueue<Long>> ID_MAP = new ConcurrentHashMap<>();

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String URL = "http://192.168.1.200:10001/id/batch";

    private static final ConcurrentHashMap<Class<?>, IdUtil> UTIL_MAP = new ConcurrentHashMap<>();

    public static IdUtil getOrGenerate(
            String application, Class<?> aggregate, Integer maxSize, Integer warningSize, Integer perQuantity
    ){
        if (UTIL_MAP.containsKey(aggregate)){
            return UTIL_MAP.get(aggregate);
        }else {
            return getAndUpdate(application, aggregate, maxSize, warningSize, perQuantity);
        }
    }

    public static IdUtil getAndUpdate(
            String application, Class<?> aggregate, Integer maxSize, Integer warningSize, Integer perQuantity
    ){
        IdUtil idUtil = UTIL_MAP.getOrDefault(aggregate, new IdUtil());
        assemble(idUtil, application, aggregate, maxSize, warningSize, perQuantity);
        UTIL_MAP.put(aggregate, idUtil);
        return idUtil;
    }

    public Long getId(){
        PriorityBlockingQueue<Long> idQueue = ID_MAP.getOrDefault(aggregate, new PriorityBlockingQueue<>(maxSize));
        ID_MAP.putIfAbsent(aggregate, idQueue);
        return generateId(idQueue);
    }

    private static void assemble(IdUtil idUtil, String application, Class<?> aggregate, Integer maxSize, Integer warningSize, Integer perQuantity){
        if (Objects.nonNull(maxSize)){
            idUtil.maxSize = maxSize;
        }
        if (Objects.nonNull(warningSize)){
            idUtil.warningSize = warningSize;
        }
        if (Objects.nonNull(perQuantity)){
            idUtil.perQuantity = perQuantity;
        }
        idUtil.application = application;
        idUtil.aggregate = aggregate;
        idUtil.check();
    }

    private void check(){
        if (Objects.isNull(application) || application.isBlank()){
            throw new IllegalArgumentException("?????????????????????");
        }
        if (Objects.isNull(aggregate)){
            throw new IllegalArgumentException("????????????????????????");
        }
        if (maxSize < perQuantity){
            throw new IllegalArgumentException("??????ID????????????????????????????????????");
        }
        if (perQuantity < 10000 || perQuantity > 300000){
            throw new IllegalArgumentException("??????????????????????????????300000?????????10000");
        }
    }

    private Long generateId(PriorityBlockingQueue<Long> idQueue){
        if (idQueue.size() <= warningSize) {
            AtomicBoolean topUpTag = new AtomicBoolean(true);
            while (idQueue.size() < maxSize && topUpTag.get()){
                if (!idQueue.isEmpty()) {
                    new Thread(() -> {
                        try {
                            topUp(idQueue);
                        } catch (UnknownServiceException | JsonProcessingException e) {
                            e.printStackTrace();
                            topUpTag.set(false);
                        }
                    }).start();
                } else {
                    try {
                        topUp(idQueue);
                    } catch (UnknownServiceException | JsonProcessingException e) {
                        throw new UnsupportedOperationException("ID????????????", e);
                    }
                }
            }
        }
        return idQueue.poll();
    }

    private synchronized void topUp(PriorityBlockingQueue<Long> idQueue) throws JsonProcessingException, UnknownServiceException {
        if (idQueue.size() > maxSize){
            return;
        }
        int retryTimes = 3;
        while (retryTimes > 0){
            HttpResponse<String> response;
            try {
                response = HTTP_CLIENT.send(request(), HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                retryTimes --;
                continue;
            }
            Queue<Long> ids = SerializeUtil.jsonToObject(response.body(), new TypeReference<>() {});
            idQueue.addAll(ids);
            return;
        }
        throw new UnknownServiceException("ID????????????, ID??????????????????");
    }

    private HttpRequest request(){
        String url =URL +  "/" + application + "/" + aggregate.getName() + "/" + perQuantity;
        return HttpRequest.newBuilder()
                .GET()
                .timeout(Duration.of(3L, ChronoUnit.SECONDS))
                .uri(URI.create(url))
                .build();
    }
}
