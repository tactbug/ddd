package com.tactbug.ddd.common.utils;

import com.tactbug.ddd.common.entity.BaseAggregate;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 15:36
 */
class IdUtilTest {
    @Test
    public void testGet() throws InterruptedException {
        IdUtil common = IdUtil.getOrGenerate("common", BaseAggregate.class, null, null, null);
        ConcurrentHashMap<Integer, List<Long>> idMap = new ConcurrentHashMap<>();
        ArrayList<Integer> threads = new ArrayList<>(10);
        for (int i = 0; i < 1000; i++) {
            threads.add(i + 1);
        }
        for (Integer i : threads) {
            new Thread(() -> {
                List<Long> ids = idMap.getOrDefault(i, new ArrayList<>(20));
                idMap.putIfAbsent(i, ids);
                for (int j = 0; j < 20; j++) {
                    Long id = common.getId();
                    ids.add(id);
                }
            }).start();
        }
        Thread.sleep(3000);
        idMap.forEach((index, ids) -> {
            assertTrue(Objects.nonNull(ids));
            assertEquals(20, ids.size());
            assertEquals(20, ids.stream().distinct().count());
        });
        for (int i = 1; i < idMap.size() + 1; i++) {
            for (int j = i + 1; j < idMap.size() + 1; j++) {
                assertTrue(idMap.get(i).stream().noneMatch(idMap.get(j)::contains));
            }
        }
    }
}