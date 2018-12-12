package com.mine.boot.config.zookeeper;

import com.mine.boot.exception.LockException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * ZookeeperLock
 *
 * 利用节点名称的唯一性来实现独占锁
 * @author zhangsl
 * @date 2018/12/12 15:42
 */
public class ZookeeperLock implements Lock, Watcher {

    private ZooKeeper zk;
    /**
     * 根
     */
    private String root = "/locks";

    /**
     * 竞争资源的标志
     */
    private String lockName;

    /**
     * 当前锁
     */
    private String myZnode;
    private int sessionTimeout = 30000;
    private List<Exception> exceptions = new ArrayList<>();

    /**
     * 创建分布式锁,使用前需确认config配置的zookeeper服务可用
     * @param config 127.0.0.1:2181
     * @param lockName 竞争资源标志,lockName中不能包含单词lock
     */
    public ZookeeperLock(String config, String lockName) {
        this.lockName = lockName;
        //创建一个与服务器的连接
        try {
            zk = new ZooKeeper(config, sessionTimeout, this);
            Stat stat = zk.exists(root, false);
            if (stat == null) {
                //创建根节点
                zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (IOException e) {
            exceptions.add(e);
        } catch (InterruptedException e) {
            exceptions.add(e);
        } catch (KeeperException e) {
            exceptions.add(e);
        }
    }

    @Override
    public void lock() {
        if (exceptions.size() > 0) {
            throw new LockException(exceptions.get(0));
        }
        if (!tryLock()) {
            throw new LockException("您的操作太频繁，请稍后再试");
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.lock();
    }

    @Override
    public boolean tryLock() {
        try {
            myZnode = zk.create(root + "/" + lockName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return tryLock();
    }

    @Override
    public void unlock() {
        try {
            zk.delete(myZnode, -1);
            myZnode = null;
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }

    /**
     * 使用方式
     * ZookeeperLock lock = null;
     * try {
     *     lock = new ZookeeperLock("127.0.0.1:2182","test1");
     *     lock.lock();
     *     //业务逻辑处理
     * } catch (LockException e) {
     *     throw e;
     * } finally {
     *     if(lock != null)
     *         lock.unlock();
     * }
     */

}
