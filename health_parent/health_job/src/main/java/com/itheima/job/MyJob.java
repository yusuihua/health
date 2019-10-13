package com.itheima.job;

import com.itheima.constant.RedisConstant;
import com.itheima.dao.OrderSettingDao;
import com.itheima.service.OrderSettingService;
import com.itheima.util.QiNiuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class MyJob {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private OrderSettingDao orderSettingDao;

    public volatile boolean flag = true;

    public void doAbc(){
        while(flag){
            System.out.println("I'm here.....");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //删除昨天的order表记录
            Calendar car = Calendar.getInstance();
            car.add(Calendar.DATE,-1 );
            SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
            String date = sdf.format(car.getTime());
            date = "2019-04-30";
            orderSettingDao.deleteBeforeDate(date);
        }


        Jedis jedis = jedisPool.getResource();
        // 1. 取出redis中所有图片集合-保存到数据库的图片集合
        Set<String> need2Delete = jedis.sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        // 2. 调用QiNiuUitl删除服务器上的图片
        QiNiuUtil.removeFiles(need2Delete.toArray(new String[]{}));
        // 3. 成功要删除redis中的缓存，所有的，保存到数据库
        jedis.del(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);




    }

    public static void main(String[] args) {
//        ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-job.xml");

    }
}
