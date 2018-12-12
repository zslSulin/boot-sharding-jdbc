package com.mine.boot.config.zookeeper;

import com.mine.boot.exception.LockException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * DistributedLock
 *
 * 利用临时顺序节点控制时序实现
 * @author zhangsl
 * @date 2018/12/12 16:13
 */
public class DistributedLock implements Lock, Watcher {

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
     * 等待前一个锁
     */
    private String waitNode;

    /**
     * 当前锁
     */
    private String myZnode;
    /**
     * 计数器
     */
    private CountDownLatch latch;
    private int sessionTimeout = 30000;
    private List<Exception> exceptions = new ArrayList<>();

    /**
     * 创建分布式锁,使用前需确认config配置的zookeeper服务可用
     * @param config 127.0.0.1:2181
     * @param lockName 竞争资源标志,lockName中不能包含单词lock
     */
    public DistributedLock(String config, String lockName) {
        this.lockName = lockName;
        //创建一个与服务器的连接
        try {
            zk = new ZooKeeper(config, sessionTimeout, this);
            Stat stat = zk.exists(root, false);
            if (stat == null) {
                // 创建根节点
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
        if(exceptions.size() > 0){
            throw new LockException(exceptions.get(0));
        }
        try {
            if (this.tryLock()) {
                System.out.println("Thread " + Thread.currentThread().getId() + " " + myZnode + " get lock true");
                return;
            } else {
                // 等待锁
                waitForLock(waitNode, sessionTimeout);
            }
        } catch (InterruptedException e) {
            throw new LockException(e);
        } catch (KeeperException e) {
            throw new LockException(e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.lock();
    }

    @Override
    public boolean tryLock() {
        try {
            String splitStr = "_lock_";
            if (lockName.contains(splitStr))
                throw new LockException("lockName can not contains \\u000B");
            // 创建临时子节点
            myZnode = zk.create(root +"/" + lockName + splitStr, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(myZnode + " is created");
            //取出所有子节点
            List<String> subNodes = zk.getChildren(root, false);
            //取出所有lockName的锁
            List<String> lockObjNodes = new ArrayList<>();
            for (String node : subNodes) {
                String _node = node.split(splitStr)[0];
                if (_node.equals(lockName)) {
                    lockObjNodes.add(node);
                }
            }
            Collections.sort(lockObjNodes);
            System.out.println(myZnode + "==" + lockObjNodes.get(0));
            if (myZnode.equals(root + "/" + lockObjNodes.get(0))) {
                //如果是最小的节点,则表示取得锁
                return true;
            }
            //如果不是最小的节点,找到比自己小1的节点
            String subMyZnode = myZnode.substring(myZnode.lastIndexOf("/") + 1);
            waitNode = lockObjNodes.get(Collections.binarySearch(lockObjNodes, subMyZnode) - 1);
        } catch (InterruptedException e) {
            throw new LockException(e);
        } catch (KeeperException e) {
            throw new LockException(e);
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        try {
            if (this.tryLock()) {
                return true;
            }
            return waitForLock(waitNode, time);
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void unlock() {
        try {
            System.out.println("unlock " + myZnode);
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

    /**
     * zookeeper节点的监视器
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (this.latch != null) {
            this.latch.countDown();
        }
    }

    private boolean waitForLock(String lower, long waitTime) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(root + "/" + lower, true);
        if (stat != null) {
            System.out.println("Thread " + Thread.currentThread().getId() + " waiting for " + root + "/" + lower);
            this.latch = new CountDownLatch(1);
            this.latch.await(waitTime, TimeUnit.MILLISECONDS);
            this.latch = null;
        }
        return true;
    }
}
