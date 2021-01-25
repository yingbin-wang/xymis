package com.cn.wti.entity.parms;

import com.cn.wti.util.app.AppUtils;

/**
 * Created by wyb on 2016/12/30.
 */

public class ListParms {

    private String parms = "";
    private String pageIndex,start,limit,bussinessType = "shouji",user_id = AppUtils.app_username,jsrid,menu_code,parms2,userid=AppUtils.user.get_id();


   /* public ListParms(String menu_code,String parms2){
        this.menu_code = menu_code;
        this.parms2 = parms2;
        this.parms = "{pageIndex:"+pageIndex+",start:"+start+",limit:"+limit+",user_id:"+ user_id+",menu_code:"+menu_code+"}";
    }*/

    /**
     * 列表参数
     * @param pageIndex
     * @param start
     * @param limit
     * @param menu_code
     */
    public ListParms(String pageIndex, String start, String limit, String menu_code){
        this.pageIndex = pageIndex;
        this.start = start;
        this.limit = limit;
        this.menu_code = menu_code;
        //pars = "{pageIndex:0,start:0,limit:10,userId:"+AppUtils.app_username+"}";
        if ("khdn".equals(menu_code)){
            this.parms = "{pageIndex:"+pageIndex+",start:"+start+",limit:"+limit+",user_id:"+ user_id+",userid:"+ userid+",ip:"+AppUtils.app_ip+",device:PHONE,cxlx:1,cs:isdn = '档案',isclose:1,isNotlz:1,menu_code:"+menu_code+"}";
        }else{
            this.parms = "{pageIndex:"+pageIndex+",start:"+start+",limit:"+limit+",user_id:"+ user_id+",userid:"+ userid+",ip:"+AppUtils.app_ip+",device:PHONE,isphone:1,isclose:1,isNotlz:1,menu_code:"+menu_code+"}";
        }

    }
    //带过滤条件 的 查询参数
    public ListParms(String pageIndex, String start, String limit, String menu_code,String parms2){
        this.pageIndex = pageIndex;
        this.start = start;
        this.limit = limit;
        this.menu_code = menu_code;
        this.parms2 = parms2;
        if (AppUtils.user != null){
            this.jsrid=AppUtils.user.get_zydnId();
        }else{
            if (AppUtils.user != null){
                this.jsrid="0";
            }
        }
        if ("customer".equals(menu_code)){
            this.parms = "{pageIndex:"+pageIndex+",start:"+start+",limit:"+limit+",user_id:"+ user_id+",userid:"+ userid+",ip:"+AppUtils.app_ip+",device:PHONE,jsrid:"+ jsrid+",menu_code:"+menu_code
                         +",cxlx:1,parms:isdn = '档案',isphone:1,isclose:1,isNotlz:1,"+parms2+"}";
        }else{
            this.parms = "{pageIndex:"+pageIndex+",start:"+start+",limit:"+limit+",userid:"+ userid+",ip:"+AppUtils.app_ip+",device:PHONE,username:"+ AppUtils.app_username+",menu_code:"+menu_code+",isphone:1,isclose:1,isNotlz:1,"+parms2+"}";
        }

    }

    //不分页 带过滤条件 的 查询参数 pars = "{menu_code:"+menu_code+","+parms2+"}";
    public ListParms(String menu_code,String parms2){
        this.menu_code = menu_code;
        this.parms2 = parms2;
        if ("khdn".equals(menu_code)){
            this.parms = "{user_id:"+ user_id+",auditby:"+ AppUtils.app_username+",userid:"+AppUtils.app_username+",user_code:"+AppUtils.app_username
                    +",userId:"+ AppUtils.user.get_zydnId()+",user_name:"+ AppUtils.user.get_zydnName()+",menucode:"+menu_code
                    +",zydnid:"+ AppUtils.user.get_zydnId()+",zydn:"+ AppUtils.app_username
                    +",menu_code:"+menu_code+",userid:"+ userid+",ip:"+AppUtils.app_ip+",device:PHONE,cxlx:1,parms:isdn = '档案',isphone:1,isclose:1,isNotlz:1,"+parms2+"}";
        }else{
            this.parms = "{user_id:"+ user_id+",auditby:"+ AppUtils.app_username+",userid:"+AppUtils.app_username+",user_code:"+AppUtils.app_username
                    +",userId:"+ AppUtils.user.get_zydnId()+",user_name:"+ AppUtils.user.get_zydnName()+",menucode:"+menu_code
                    +",zydnid:"+ AppUtils.user.get_zydnId()+",zydn:"+ AppUtils.app_username
                    +",menu_code:"+menu_code+",userid:"+ userid+",ip:"+AppUtils.app_ip+",device:PHONE,isphone:1,isclose:1,isNotlz:1,"+parms2+"}";
        }
    }

    //带过滤条件 的 查询参数 按类型查询
    public ListParms(String pageIndex, String start, String limit, String menu_code,String parms2,int type){
        this.pageIndex = pageIndex;
        this.start = start;
        this.limit = limit;
        this.menu_code = menu_code;
        this.parms2 = parms2;
        if (AppUtils.user != null){
            this.jsrid=AppUtils.user.get_zydnId();
        }else{
            this.jsrid="0";
        }

        if (type == 1){
            this.parms = "{pageIndex:"+pageIndex+",start:"+start+",limit:"+limit+",username:"+ AppUtils.app_username+
                    ",jsrid:"+ jsrid+",menu_code:"+menu_code+",userid:"+ userid+",ip:"+AppUtils.app_ip+"," +
                    "device:PHONE,isList:1,isclose:1,isNotlz:1,"+parms2+"}";
        }else{
            this.parms = "{pageIndex:"+pageIndex+",start:"+start+",limit:"+limit+",username:"+ AppUtils.app_username+
                    ",jsrid:"+ jsrid+",menu_code:"+menu_code+",userid:"+ userid+",ip:"+AppUtils.app_ip+
                    ",device:PHONE,isclose:1,isNotlz:1,"+parms2+"}";
        }

    }


    public ListParms(String menu_code,String parms2,String type){
        this.menu_code = menu_code;
        this.parms2 = parms2;

        switch (type){
            case "check":
                this.parms = "{user_id:"+ user_id+",auditby:"+ AppUtils.app_username+",user_code:"+AppUtils.app_username
                        +",userId:"+ AppUtils.user.get_zydnId()+",user_name:"+ AppUtils.user.get_zydnName()
                        +",zydnid:"+ AppUtils.user.get_zydnId()+",zydn:"+ AppUtils.app_username
                        +",menu_code:"+menu_code+",userid:"+ userid+",ip:"+AppUtils.app_ip+",device:PHONE,isphone:1,isclose:1,isNotlz:1,"+parms2+"}";
                break;
            case "deleteAll":
                this.parms = "{menu_code:"+menu_code+",userid:"+ userid+",ip:"+AppUtils.app_ip+",device:PHONE,isphone:1,isclose:1,isNotlz:1,"+parms2+"}";
                break;
            case "showQx":
                this.parms = "{username:"+AppUtils.app_username+",staff_id:"+ AppUtils.user.get_zydnId()+",staff_name:"+ AppUtils.user.get_zydnName()
                        +",menu_code:"+menu_code+",userid:"+ userid+",ip:"+AppUtils.app_ip+",device:PHONE,isphone:1,isclose:1,isNotlz:1,"+parms2+"}";
                break;
            default:
                this.parms = "{user_id:"+ user_id+",ip:"+AppUtils.app_ip+",auditby:"+ AppUtils.app_username+",userid:"+AppUtils.app_username+",user_code:"+AppUtils.app_username
                        +",userId:"+ AppUtils.user.get_zydnId()+",user_name:"+ AppUtils.user.get_zydnName()
                        +",zydnid:"+ AppUtils.user.get_zydnId()+",zydn:"+ AppUtils.app_username
                        +",menu_code:"+menu_code+",userid:"+ userid+",device:PHONE,isphone:1,isclose:1,isNotlz:1,"+parms2+"}";
                break;
        }


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

    /*pars = "{pageIndex:0,start:0,limit:10,name:DeptSellDaily,bussinessType:shouji,user_id:"+ AppUtils.app_username+",pars:dt<m>1}";*/
}
