package com.ease.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.cn.wti.entity.System_one;
import com.cn.wti.util.app.AppUtils;
import com.cn.wti.util.db.SharePreferencesUtils;
import com.cn.wti.util.page.PageDataSingleton;
import com.ease.utils.ChatRowVoiceCall;
import com.ease.utils.HuanxinUtils;
import com.ease.widget.EaseChatRowLocation;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.Constant;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseChatExtendMenu;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseVoiceRecorderView;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowText;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.wticn.wyb.wtiapp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * you can new an EaseChatFragment to use or you can inherit it to expand.
 * You need call setArguments to pass chatType and userId 
 * <br/>
 * <br/>
 * you can see ChatActivity in demo for your reference
 *
 */
public class EaseChatFragment extends com.hyphenate.easeui.ui.EaseChatFragment implements com.hyphenate.easeui.ui.EaseChatFragment.EaseChatFragmentHelper{

    private EaseUI easeUI;
    private Context appContext;
    private Map<String,EaseUser> contactList;
    private EMMessageListener messageListener;
    private boolean isRobot = false;
    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;

    static final int ITEM_TAKE_PICTURE = 1;
    static final int ITEM_PICTURE = 2;
    static final int ITEM_LOCATION = 3;

    private List<Map<String,Object>> lxrList = null;
    private static PageDataSingleton _catch = PageDataSingleton.getInstance();
    private EaseItemClickListener extendMenuItemClickListener = null;
    private EMLocationMessageBody locBody;

    /**
     * init view
     */
    @Override
    protected void initView() {
        // hold to record voice
        //noinspection ConstantConditions
        voiceRecorderView = (EaseVoiceRecorderView) getView().findViewById(com.hyphenate.easeui.R.id.voice_recorder);

        // message list layout
        messageList = (EaseChatMessageList) getView().findViewById(com.hyphenate.easeui.R.id.message_list);
        if(chatType != EaseConstant.CHATTYPE_SINGLE)
            messageList.setShowUserNick(true);
        listView = messageList.getListView();

        extendMenuItemClickListener = new EaseItemClickListener();
        inputMenu = (EaseChatInputMenu) getView().findViewById(com.hyphenate.easeui.R.id.input_menu);
        registerExtendMenuItem();
        // init input menu
        inputMenu.init(null);
        inputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {
                sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderView.EaseVoiceRecorderCallback() {

                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                        sendVoiceMessage(voiceFilePath, voiceTimeLength);
                    }
                });
            }

            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                sendBigExpressionMessage(emojicon.getName(), emojicon.getIdentityCode());
            }
        });

        swipeRefreshLayout = messageList.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(com.hyphenate.easeui.R.color.holo_blue_bright, com.hyphenate.easeui.R.color.holo_green_light,
                com.hyphenate.easeui.R.color.holo_orange_light, com.hyphenate.easeui.R.color.holo_red_light);

        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void registerExtendMenuItem(){
        for(int i = 0; i < itemStrings.length; i++){
            inputMenu.registerExtendMenuItem(itemStrings[i], itemdrawables[i], itemIds[i], extendMenuItemClickListener);
        }
    }

    protected void setUpView() {
        setChatFragmentListener(this);
        super.setUpView();
        init(getActivity());
    }

    @Override
    protected void toGroupDetails() {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group = EMClient.getInstance().groupManager().getGroup(toChatUserId);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found, Toast.LENGTH_SHORT).show();
                return;
            }
            if(chatFragmentHelper != null){
                chatFragmentHelper.onEnterToChatDetails();
            }

            Intent intent = new Intent();
            intent.setClass(getActivity(),GroupDetailsActivity.class);
            intent.putExtra("groupId",toChatUserId);
            startActivityForResult(intent,1);

        }else if(chatType == EaseConstant.CHATTYPE_CHATROOM){
            if(chatFragmentHelper != null){
                chatFragmentHelper.onEnterToChatDetails();
            }
        }
    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        if(isRobot){
            //set message extension
            message.setAttribute("em_robot_message", isRobot);
        }
        //设置要发送扩展消息用户昵称
        message.setAttribute(EaseConstant.EXTRA_USER_ID, toChatUserId);
        message.setAttribute(EaseConstant.EXTRA_USER_NAME, toChatUsername);
        //设置要发送扩展消息用户头像
        message.setAttribute(EaseConstant.EXTRA_HHOTO, SharePreferencesUtils.getVal((Activity) appContext,"photo_16"));
    }

    @Override
    public void onEnterToChatDetails() {

    }

    @Override
    public void onAvatarClick(String username) {

    }

    @Override
    public void onAvatarLongClick(String username) {

    }

    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //设置消息点击事件
        if (message.getType() == EMMessage.Type.LOCATION){
            Intent intent = new Intent(getContext(), EaseGaodeMapActivity.class);
            locBody = (EMLocationMessageBody) message.getBody();
            intent.putExtra("latitude", locBody.getLatitude());
            intent.putExtra("longitude", locBody.getLongitude());
            intent.putExtra("address", locBody.getAddress());
            getActivity().startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {
    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        return false;
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return new CustomChatRowProvider();
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
            String action = cmdMsgBody.action();//获取自定义action
            //接收并处理扩展消息
            String userId=message.getUserName();
            if (contactList == null){
                contactList=getContactList();
            }
            EaseUser user = contactList.get(userId);
            if (user != null){
                String userPic=user.getAvatar();
                String hxIdFrom=message.getFrom();
                EaseUser easeUser=new EaseUser(hxIdFrom);
                easeUser.setAvatar(userPic);
                easeUser.setNick(user.getNick());
                contactList.put(hxIdFrom,easeUser);
            }
        }
        messageList.refresh();
        /*//end of red packet code
        super.onCmdMessageReceived(messages);*/
    }

    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //音、视频通话发送、接收共4种
            return 4;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if(message.getType() == EMMessage.Type.TXT){
                //语音通话类型
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)){
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
                }else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
                    //视频通话
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
                }
            }else if (message.getType() == EMMessage.Type.LOCATION){
                message.setAttribute("className","com.ease.ui.EaseGaodeMapActivity");
            }
            return 0;
        }

        @Override
        public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
            EaseChatRow chatRow = null;
            if(message.getType() == EMMessage.Type.TXT){
                // 语音通话、视频通话
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) || message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
                    //ChatRowVoiceCall为一个继承自EaseChatRow的类
                    return new ChatRowVoiceCall(getActivity(), message, position, adapter);
                }else{
                    chatRow = new EaseChatRowText(appContext, message, position, adapter);
                }
            }
            return chatRow;
        }
    }

    public void init(Context context) {
        EMOptions options = initChatOptions();
        //use default options if options is null
        if (EaseUI.getInstance().init(context, options)) {
            appContext = context;
            //获取easeui实例
            easeUI = EaseUI.getInstance();
            //初始化easeui
            easeUI.init(appContext,options);
            //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
            EMClient.getInstance().setDebugMode(true);
            /*EaseUI.getInstance().setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
                @Override
                public EaseUser getUser(String username) {
                    return getUserInfo(username);
                }
            });*/
            //设置全局监听
            //setGlobalListeners();
            //获取联系人数据
            lxrList = (List<Map<String, Object>>) _catch.get("lxrList");
        }
    }
    private EMOptions initChatOptions(){
        EMOptions options = new EMOptions();
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(false);
        // set if you need read ack
        options.setRequireAck(true);
        // set if you need delivery ack
        options.setRequireDeliveryAck(false);
        return options;
    }

    /**
     *获取所有的联系人信息
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        if (isLoggedIn() && contactList == null) {
            contactList = HuanxinUtils.getContacts(lxrList);
        }
        // return a empty non-null object to avoid app crash
        if(contactList == null){
            return new Hashtable<String, EaseUser>();
        }
        return contactList;
    }
    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }
    /**
     * set global listener
     */
    /*protected void setGlobalListeners(){
        registerMessageListener();
    }
    *//**
     * Global listener
     * If this event already handled by an activity, you don't need handle it again
     * activityList.size() <= 0 means all activities already in background or not in Activity Stack
     *//*
    protected void registerMessageListener() {
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                    //接收并处理扩展消息
                    String userId=message.getUserName();
                    if (contactList == null){
                        contactList=getContactList();
                    }
                    EaseUser user = contactList.get(userId);
                    if (user != null){
                        String userPic=user.getAvatar();
                        String hxIdFrom=message.getFrom();
                        EaseUser easeUser=new EaseUser(hxIdFrom);
                        easeUser.setAvatar(userPic);
                        easeUser.setNick(user.getNick());
                        contactList.put(hxIdFrom,easeUser);
                    }
                }
                messageList.refresh();
            }
            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    EMLog.d(TAG, "receive command message");
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

    /**
     * handle the click event for extend menu
     *
     */
    class EaseItemClickListener implements EaseChatExtendMenu.EaseChatExtendMenuItemClickListener{

        @Override
        public void onClick(int itemId, View view) {
            if(chatFragmentHelper != null){
                if(chatFragmentHelper.onExtendMenuItemClick(itemId, view)){
                    return;
                }
            }
            switch (itemId) {
                case ITEM_TAKE_PICTURE:
                    //验证应用是否有拍照权限
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
                    try {
                        selectPicFromCamera();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getActivity(),"未打开相机权限",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case ITEM_PICTURE:
                    selectPicFromLocal();
                    break;
                case ITEM_LOCATION:
                    startActivityForResult(new Intent(getActivity(), EaseGaodeMapActivity.class), REQUEST_CODE_MAP);
                    break;

                default:
                    break;
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v("test","1");
    }
}
