package org.qiyu.live.gift.provider.kafka;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.qiyu.live.common.interfaces.topic.GiftProviderTopicNames;
import org.qiyu.live.gift.bo.RollBackStockBO;
import org.qiyu.live.gift.provider.service.ISkuStockInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class StockRollBackConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockRollBackConsumer.class);

    @Resource
    private ISkuStockInfoService skuStockInfoService;

    private static final DelayQueue<DelayedTask> DELAY_QUEUE = new DelayQueue<>();

    private static final ExecutorService DELAY_QUEUE_THREAD_POOL = new ThreadPoolExecutor(
            3, 10,
            10L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100)
    );

    @PostConstruct()
    private void init() {
        DELAY_QUEUE_THREAD_POOL.submit(() -> {
            while (true) {
                try {
                    DelayedTask task = DELAY_QUEUE.take();
                    task.execute();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    
    @KafkaListener(topics = GiftProviderTopicNames.ROLL_BACK_STOCK, groupId = "stock-roll-back")
    public void stockRollBack(String rollBackStockBoStr) {
        RollBackStockBO rollBackStockBO = JSON.parseObject(rollBackStockBoStr, RollBackStockBO.class);
        DELAY_QUEUE.offer(new DelayedTask(30 * 60 * 1000, () -> skuStockInfoService.stockRollBackHandler(rollBackStockBO)));
        LOGGER.info("[StockRollBackConsumer] rollback success, rollbackInfo is {}", rollBackStockBO);
    }
    
}
