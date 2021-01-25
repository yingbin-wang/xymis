package com.ease.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import com.cn.wti.entity.System_one;
import com.cn.wti.entity.view.custom.badger.BadgeView;
import com.cn.wti.util.app.ActivityController;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.FastJsonUtils;
import com.cn.wti.util.db.ReflectHelper;
import com.cn.wti.util.db.SharePreferencesUtils;
import com.cn.wti.util.other.DateUtil;
import com.cn.wti.util.page.PageDataSingleton;
import com.ease.utils.HuanxinUtils;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseConversationList;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.EMLog;
import com.wticn.wyb.wtiapp.R;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 环信会话界面
 *
 */
public class EaseConversationListFragment extends com.hyphenate.easeui.ui.EaseConversationListFragment implements OnClickListener{

    private EaseTitleBar titleBar;
    private List<Map<String,Object>> lxrList,groupList;
    private static PageDataSingleton _catch = PageDataSingleton.getInstance();
    private EMMessageListener messageListener;
    private BadgeView badgeView;
    private Context mContext;
    private Map<String,EaseUser> contactList;

    public EaseConversationList getConversationListView(){
        return  conversationListView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.easeui_fragment_conversation_list, container, false);
    }

    @Override
    protected void initView() {

        super.initView();
        conversationListView = (com.hyphenate.easeui.widget.EaseConversationList) getView().findViewById(R.id.list);

        titleBar = (EaseTitleBar) getView().findViewById(R.id.title_bar);
        titleBar.setRightImageResource(R.drawable.ease_white_add);
        titleBar.setRightLayoutClickListener(this);

        titleBar.setTitle("消息");

        lxrList = (List<Map<String, Object>>) _catch.get("lxrList");
        groupList = (List<Map<String, Object>>) _catch.get("bmList");

        conversationListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);

                //取得数据
                // start chat acitivity
                Intent intent = new Intent(getActivity(), EaseChartActivity.class);
                if(conversation.isGroup()){
                    if(conversation.getType() == EMConversation.EMConversationType.ChatRoom){
                        // it's group chat
                        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_CHATROOM);
                    }else{
                        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                    }
                }else{
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
                }

                String content = conversation.getExtField();
                String[] user_content = null;
                if (!content.equals("")) {
                    user_content = content.split(",");
                    if (user_content[0].equals(EMClient.getInstance().getCurrentUser()))
                        Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();

                    intent.putExtra(EaseConstant.EXTRA_USER_ID, user_content[0]);
                    intent.putExtra(EaseConstant.EXTRA_USER_NAME,user_content[1]);

                }else {
                    intent.putExtra(EaseConstant.EXTRA_USER_ID,  conversation.conversationId());
                    intent.putExtra(EaseConstant.EXTRA_USER_NAME,"");
                }

                intent.putExtra(EaseConstant.EXTRA_CHAT_FROM,"conversation");

                startActivity(intent);
            }
        });

        mContext = getActivity();
        //注册消息接收事件
        /*registerMessageListener();*/
        EMClient.getInstance().chatManager().addConversationListener(listener);

        EaseUI.getInstance().setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });
    }

    private EaseUser getUserInfo(String username){
        //获取 EaseUser实例, 这里从内存中读取
        //如果你是从服务器中读读取到的，最好在本地进行缓存
        EaseUser user = null;
        //如果用户是本人，就设置自己的头像
        if(username.equals(EMClient.getInstance().getCurrentUser())){
            user=new EaseUser(username);
            user.setAvatar((String) SharePreferencesUtils.getVal((Activity) mContext,"photo_16").replace("data:image/png;base64,",""));
            user.setNick((String)SharePreferencesUtils.getVal((Activity) mContext,"name"));
            return user;
        }

        //收到别人的消息，设置别人的头像
        if (contactList!=null && contactList.containsKey(username)){
            user=contactList.get(username);
        }else { //如果内存中没有，则将本地数据库中的取出到内存中
            contactList=getContactList();
            user=contactList.get(username);
        }
        //如果用户不是你的联系人，则进行初始化
        if(user == null){
            user = new EaseUser(username);
            EaseCommonUtils.setUserInitialLetter(user);
        }else {
            if (TextUtils.isEmpty(user.getAvatar())){//如果名字为空，则显示环信号码
                user.setNick(user.getNick());
            }
            user.setNick(user.getNick());
        }
        return user;
    }

    /**
     *获取所有的联系人信息
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        if (HuanxinUtils.isLoggedIn()) {
            lxrList = (List<Map<String, Object>>) _catch.get("lxrList");
            contactList = HuanxinUtils.getContacts(lxrList);
        }
        // return a empty non-null object to avoid app crash
        if(contactList == null){
            return new Hashtable<String, EaseUser>();
        }
        return contactList;
    }

    
    /**
     * load conversation list
     * 
     * @return
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        +    */
    @Override
    public List<EMConversation> loadConversationList(){
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            EMConversation item = sortItem.second;
            item.setExtField(getHuihuaName(item.conversationId()));
            list.add(item);
        }
        return list;
    }

    public String getHuihuaName(String id){
        String name = "";
        if (lxrList != null && lxrList.size() >0){
            for (Map<String,Object> userMap:lxrList){
                if (userMap.get("huanxincode") != null && userMap.get("huanxincode").equals(id)){
                    name = userMap.get("huanxincode").toString()+","+userMap.get("name").toString()+",user";
                    break;
                }
            }
        }else{
            name = "";
        }

        if (groupList != null && groupList.size() >0){
            for (Map<String,Object> groupMap:groupList){
                if (groupMap.get("huanxincode") != null && groupMap.get("huanxincode").equals(id)){
                    name = groupMap.get("huanxincode")+","+groupMap.get("name").toString()+",group";
                    break;
                }
            }
        }
        return  name;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        //打开联系人
        if (i == R.id.right_layout) {
            _catch.put("lxrList",lxrList);
            _catch.put("bmList",groupList);
            HuanxinUtils.showTextViewDialog(getActivity(),0,v);
        }
    }

   /* protected void registerMessageListener() {
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                if (messages != null){
                    conversationListView.refresh();
                }
            }
            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                }
            }
            @Override
            public void onMessageRead(List<EMMessage> messages) {
            }
            @Override
            public void onMessageDelivered(List<EMMessage> message) {
            }
            @Override
            public void onMessageChanged(EMMessage message, Object change) {
            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }*/

    EMConversationListener listener = new EMConversationListener() {
        @Override
        public void onCoversationUpdate() {
            Toast.makeText(mContext,"test",Toast.LENGTH_SHORT).show();
        }
    };

    public void reshView(){
        conversationList.clear();
        conversationList.addAll(loadConversationList());
    }

}
