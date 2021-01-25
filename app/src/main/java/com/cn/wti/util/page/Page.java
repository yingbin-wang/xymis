package com.cn.wti.util.page;

import com.cn.wti.util.app.AppUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2016/12/20.
 */

public class Page {

    //成员变量
    //当前页
    private int nowpage;
	//总记录数
    private int countrecord;
    //总页数
    private int countpage;
    // 当前页记录开始的位置
    private int pageindex;
    // 每页显示的记录数
    public static int PAGESIZE = Integer.parseInt(AppUtils.limit);
    // 索引的sum值 代表的是 google页面中最大显示页数
    private int sumindex = 6;
    // 开始的索引值
    private int startindex;
    // 结束的索引值
    private int endindex;
    //当前页信息
    private List<Map<String,Object>> allentities;

    //构造器
    public Page() { }

    public Page(int countrecord, int nowpage,int pagesize) {
        //页行数
        this.PAGESIZE = pagesize;
        // 计算当前页
        this.nowpage = nowpage;
        // 计算出当前页开始的位置
        this.pageindex = (nowpage - 1) * PAGESIZE;
        // 计算总页数
        this.countrecord = countrecord;
        try{
            if (this.countrecord % this.PAGESIZE == 0) {
                this.countpage = this.countrecord / this.PAGESIZE;
            } else {
                this.countpage = this.countrecord / this.PAGESIZE + 1;
            }
        }catch (Exception e){
            this.countpage = 0;
        }


        //计算开始和结束的索引值
        //当当前页小于等于四时开始的索引值等于一,而结束的索引值分两种情况
        if (this.nowpage <= 4) {
            this.startindex = 1;
            if (this.endindex > this.countpage) {
                this.endindex = this.countpage;
            }
            this.endindex = this.nowpage + 2;
        }
        // 当当前页大于四时开始的索引值和结束的索引值均分三种情况
        else if (this.nowpage > 4) {
            if (this.endindex > this.countpage&& this.countpage < this.sumindex) {
                this.startindex = 1;
                this.endindex = this.countpage;
            }
            else if (this.countpage > this.sumindex) {
                this.startindex = this.countpage - 5;
                this.endindex = this.countpage;
            }
            else{
                this.startindex = this.nowpage - 3;
                this.endindex = this.nowpage + 2;
            }
        }
    }
    
    public int getNowpage() {
		return nowpage;
	}
	public void setNowpage(int nowpage) {
		this.nowpage = nowpage;
	}
	public int getCountrecord() {
		return countrecord;
	}
	public void setCountrecord(int countrecord) {
		this.countrecord = countrecord;
	}
	public int getCountpage() {
		return countpage;
	}
	public void setCountpage(int countpage) {
		this.countpage = countpage;
	}
	public int getPageindex() {
		return pageindex;
	}
	public void setPageindex(int pageindex) {
		this.pageindex = pageindex;
	}
	public int getSumindex() {
		return sumindex;
	}
	public void setSumindex(int sumindex) {
		this.sumindex = sumindex;
	}
	public int getStartindex() {
		return startindex;
	}
	public void setStartindex(int startindex) {
		this.startindex = startindex;
	}
	public int getEndindex() {
		return endindex;
	}
	public void setEndindex(int endindex) {
		this.endindex = endindex;
	}
	public List<Map<String, Object>> getAllentities() {
		return allentities;
	}
	public void setAllentities(List<Map<String, Object>> allentities) {
		this.allentities = allentities;
	}
	public static int getPagesize() {
		return PAGESIZE;
	}
}