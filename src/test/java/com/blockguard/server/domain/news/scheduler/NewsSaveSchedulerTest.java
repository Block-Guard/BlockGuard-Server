package com.blockguard.server.domain.news.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NewsSaveSchedulerTest {

    @Autowired
    private NewsSaveScheduler newsSaveScheduler;

    @Test
    void testSchedulerManually() {
        newsSaveScheduler.saveNewsArticlesScheduler();
    }
}