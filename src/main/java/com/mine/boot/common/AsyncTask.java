package com.mine.boot.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * AsyncTask
 *
 * @author zhangsl
 * @date 2019/2/14 14:33
 */
@Slf4j
@Component
public class AsyncTask {

    @Async("mySimpleAsync")
    public Future<String> doTask1() throws InterruptedException {
        log.info("task1 started.");
        long start = System.currentTimeMillis();
        Thread.sleep(5000);
        long end = System.currentTimeMillis();
        log.info("task1 finshed, time elapsed: {} ms.", end - start);
        return new AsyncResult<>("Task1 accomplished!");
    }

    @Async("myAsync")
    public Future<String> doTask2() throws InterruptedException {
        log.info("task2 started.");
        long start = System.currentTimeMillis();
        Thread.sleep(3000);
        long end = System.currentTimeMillis();
        log.info("task2 finshed, time elapsed: {} ms.", end - start);
        return new AsyncResult<>("Task2 accomplished!");
    }
}
