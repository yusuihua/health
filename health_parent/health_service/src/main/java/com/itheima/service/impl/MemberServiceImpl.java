package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service(interfaceClass = MemberService.class)
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;

    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    @Override
    public void add(Member member) {
        memberDao.add(member);
    }

    /**
     * 会员数量统计
     * @return
     */
    @Override
    public Map<String, List<Object>> getMemberReport() {
// {flag,message,data{
//    months:[],
//    memberCount: []
// }}
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>(12);
        //1. 获取上一年份的数据
        // 日历对象，java中来操作日期时间，当前系统时间
        Calendar car = Calendar.getInstance();
        // 回到12个月以前
        car.add(Calendar.MONTH, -12);
        //2. 循环12个月，每个月要查询一次
        List<Object> months = new ArrayList<>();
        // 返回的会员数量集合
        List<Object> memberCount = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        for (int i = 1; i<=12; i++){
            // 计算当前月的值
            car.add(Calendar.MONTH,1);
            // 月份的字符串 2019-01
            String monthStr = sdf.format(car.getTime());
            months.add(monthStr);

            // 查询
            Integer monthCount = memberDao.findMemberCountBeforeDate(monthStr + "-31");
            memberCount.add(monthCount);
        }
        //3. 再查询得到的数据封装到months list 月份, memberCount list 到这个月份为止会员总数量
        Map<String,List<Object>> result = new HashMap<>();
        result.put("months", months);
        result.put("memberCount", memberCount);
        return result;
    }

    @Override
    public Map<String, List<Object>> findMemberCountBetweenDate(String startDate, String endDate) {
// {flag,message,data{
//    months:[],
//    memberCount: []
// }}


        String[] startSplit = startDate.split("-");
        String[] endSplit = endDate.split("-");

        //相差年份
        int diffYear =  Integer.valueOf(endSplit[0])-Integer.valueOf(startSplit[0]);
        int totalMonth = diffYear *12;

        //当年相差月数
        int diffMonth = Integer.valueOf(endSplit[1])- Integer.valueOf(startSplit[1]);
        totalMonth = totalMonth + diffMonth;

        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();


        //1. 获取上一年份的数据
        // 日历对象，java中来操作日期时间，当前系统时间
        Calendar car = Calendar.getInstance();
        // 设置最初时间
        car.set(Integer.valueOf(startSplit[0]) ,Integer.valueOf(startSplit[1])-2,1 ,0 ,0 ,0 );
        //2. 循环12个月，每个月要查询一次
        List<Object> months = new ArrayList<>();
        // 返回的会员数量集合
        List<Object> memberCount = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        for (int i = 1; i<=totalMonth+1; i++){
            // 计算当前月的值
            car.add(Calendar.MONTH,1);
            // 月份的字符串 2019-01
            String monthStr = sdf.format(car.getTime());
            months.add(monthStr);

            // 查询
            Integer monthCount = memberDao.findMemberCountBeforeDate(monthStr + "-31");
            memberCount.add(monthCount);
        }
        //3. 再查询得到的数据封装到months list 月份, memberCount list 到这个月份为止会员总数量
        Map<String,List<Object>> result = new HashMap<>();
        result.put("months", months);
        result.put("memberCount", memberCount);
        return result;

    }




}
