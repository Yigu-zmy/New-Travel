package com.rookie.test;

import java.util.Date;
import java.text.SimpleDateFormat;

import redis.clients.jedis.Jedis;
import com.rookie.util.RedisPoolUtil;

public class TestPool {
    public static void main(String args[]) {
        RedisPoolUtil.initalPool();
        System.out.println(TestPool.class.getResource("./../../../").getFile());
        for (int i = 0; i < 100; ++i) {
            ClientThread clientThread = new ClientThread(i);
            clientThread.start();
        }
    }
}

class ClientThread extends Thread {
    int index = 0;

    public ClientThread(int index) {
        this.index = index;
    }

    public void run() {
        Jedis jedis = RedisPoolUtil.getConn();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SSS");
        String time = simpleDateFormat.format(date);
        jedis.set("rookie" + index, time);
        try {
            Thread.sleep((int) Math.random() * 5000);
            String value = jedis.get("rookie" + index);
            System.out.println("value:" + value + ", index:" + index);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            RedisPoolUtil.closePool();
        }
    }
}
