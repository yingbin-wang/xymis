package com.cn.wti.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by wyb on 2016/11/10.
 */

public class Sys_user {
    private String _name;
    private String _id;
    private String _loginName;
    private String _zydnName;
    private String _zydnId;
    private String _bmdnId;
    private String _bmdnName;

    public String get_password() {
        return _password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    private String _password;

    public String get_bookName() {
        return _bookName;
    }

    public void set_bookName(String _bookName) {
        this._bookName = _bookName;
    }

    private String _bookName;

    public String get_version() {
        return _version;
    }

    public void set_version(String _version) {
        this._version = _version;
    }

    private String _version;
    private String sjjs/*计算含税否*/,gsxz,ispricecontroll/*是否价格管控*/;
    private List<Map<String,Object>> _qxfwList;

    public Sys_user(String _id,String _loginName,String _zydnName,String _zydnId,
                    String _bmdnId, String _bmdnName,String version,String bookName){
        this._id = _id;
        this._loginName = _loginName;
        this._zydnId = _zydnId;
        this._zydnName = _zydnName;
        this._bmdnId = _bmdnId;
        this._bmdnName = _bmdnName;
        this._version = version;
        this._bookName = bookName;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_loginName() {
        return _loginName;
    }

    public void set_loginName(String _loginName) {
        this._loginName = _loginName;
    }

    public String get_zydnName() {
        return _zydnName;
    }

    public void set_zydnName(String _zydnName) {
        this._zydnName = _zydnName;
    }

    public String get_zydnId() {
        return _zydnId;
    }

    public void set_zydnId(String _zydnId) {
        this._zydnId = _zydnId;
    }

    public String get_bmdnId() {
        return _bmdnId;
    }

    public void set_bmdnId(String _bmdnId) {
        this._bmdnId = _bmdnId;
    }

    public String get_bmdnName() {
        return _bmdnName;
    }

    public void set_bmdnName(String _bmdnName) {
        this._bmdnName = _bmdnName;
    }

    public List<Map<String, Object>> get_qxfwList() {
        return _qxfwList;
    }

    public void set_qxfwList(List<Map<String, Object>> _qxfwList) {
        this._qxfwList = _qxfwList;
    }

    public String getSjjs() {
        return sjjs;
    }

    public void setSjjs(String sjjs) {
        this.sjjs = sjjs;
    }

    public String getGsxz() {
        return gsxz;
    }

    public void setGsxz(String gsxz) {
        this.gsxz = gsxz;
    }

    public String getIspricecontroll() {
        return ispricecontroll;
    }

    public void setIspricecontroll(String ispricecontroll) {
        this.ispricecontroll = ispricecontroll;
    }
}
