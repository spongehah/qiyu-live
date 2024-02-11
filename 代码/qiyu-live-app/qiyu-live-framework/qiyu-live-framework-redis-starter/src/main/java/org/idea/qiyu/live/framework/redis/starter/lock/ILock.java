package org.idea.qiyu.live.framework.redis.starter.lock;

public interface ILock {
    
    boolean tryLock(Long timeoutSec);
    
    
    void unlock();
}