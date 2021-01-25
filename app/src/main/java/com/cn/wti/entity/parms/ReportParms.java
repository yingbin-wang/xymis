package com.cn.wti.entity.parms;

import com.cn.wti.util.app.AppUtils;

/**
 * Created by wyb on 2016/12/30.
 */

public class ReportParms {

    private String parms = "";
    private String pageIndex,start,limit,name,bussinessType = "shouji",user_id = AppUtils.app_username,pars,menuid;

    public ReportParms(String pageIndex,String start,String limit,String name,String pars,String menuid){
        this.pageIndex = pageIndex;
        this.start = start;
        this.limit = limit;
        this.name = name;
        this.pars = pars;
        this.menuid = menuid;
        /*pageIndex:"+pageIndex+",start:"+start+",limit:"+limit+",*/
        this.parms = "{name:"+name+",bussinessType:"+bussinessType+",user_id:"+ user_id+",menuid:"+menuid+",pars:"+pars+"}";
    }

    public ReportParms(String name,String pars,String menuid){
        this.name = name;
        this.pars = pars;
        this.menuid = menuid;
        this.parms = "{name:"+name+",bussinessType:"+bussinessType+",user_id:"+ user_id+",pars:"+pars+",menuid:"+menuid+"}";
    }

    public String getParms() {
        return parms;
    }

    public void setParms(String parms) {
        this.parms = parms;
    }

    public String getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(String pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPars() {
        return pars;
    }

    public void setPars(String pars) {
        this.pars = pars;
    }

    /*pars = "{pageIndex:0,start:0,limit:10,name:DeptSellDaily,bussinessType:shouji,user_id:"+ AppUtils.app_username+",pars:dt<m>1}";*/
}
