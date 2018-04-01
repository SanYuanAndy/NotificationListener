package com.sy.notificationlistener.ui;

import android.content.Context;
import android.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sy.notificationlistener.R;
import com.sy.notificationlistener.notification.WeChatListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by ASUS User on 2018/2/25.
 */
public class ConfigInfoAdapter extends BaseAdapter{
    private final static String TAG = ConfigInfoAdapter.class.getSimpleName();
    private WeChatListener.WeChatConfigs mConfigs;
    private Context mContext = null;
    private EventOperations mOperations = null;
    public  ConfigInfoAdapter(Context context, WeChatListener.WeChatConfigs config, EventOperations operations){
        mContext = context;
        mConfigs = config;
        mOperations = operations;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        WeChatListener.WeChatNotificationConfig config = mConfigs.configMap.get(mConfigs.configNames.get(i));
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.config_info_item, null);
            viewHolder = new ViewHolder();
            viewHolder.nameEditView = (EditText)convertView.findViewById(R.id.name_edittext);
            viewHolder.moreEditBtn = (Button)convertView.findViewById(R.id.more_edit_selector);
            viewHolder.tipRadioGroup = (RadioGroup)convertView.findViewById(R.id.rg_setting);
            viewHolder.rbSilence = (RadioButton) convertView.findViewById(R.id.rb_silence);
            viewHolder.rbSound = (RadioButton) convertView.findViewById(R.id.rb_sound);
            viewHolder.rbTts = (RadioButton) convertView.findViewById(R.id.rb_tts);
            viewHolder.vibrateCheckBox = (CheckBox) convertView.findViewById(R.id.vibrate_cb);

            viewHolder.moreEditBtn.setOnClickListener(viewHolder.mOnClickListener);
            viewHolder.tipRadioGroup.setOnCheckedChangeListener(viewHolder.mRGOnCheckedChangeListener);
            viewHolder.vibrateCheckBox.setOnCheckedChangeListener(viewHolder.mCBOnCheckedChangeListener);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.mOperations = mOperations;
        viewHolder.vibrateCheckBox.setChecked(config.mNeedVibrate);

        boolean bEditEnbale = false;
        if (config.mEditStatus != WeChatListener.ConfigStatus.STATUS_NONE){
            bEditEnbale = true;
        }
        viewHolder.mConfigStatus = config.mEditStatus;
        viewHolder.nameEditView.setEnabled(bEditEnbale);
        if (bEditEnbale) {
            viewHolder.nameEditView.requestFocus();
        }else{
            viewHolder.nameEditView.clearFocus();
        }
        viewHolder.tipRadioGroup.setEnabled(bEditEnbale);
        viewHolder.vibrateCheckBox.setEnabled(bEditEnbale);
        viewHolder.rbSilence.setEnabled(bEditEnbale);
        viewHolder.rbSound.setEnabled(bEditEnbale);
        viewHolder.rbTts.setEnabled(bEditEnbale);

        viewHolder.index = i;
        viewHolder.nameEditView.setText(config.mName);

        if ("global".equals(config.mName)){
            viewHolder.nameEditView.setEnabled(false);
        }

        int rb_id = R.id.rb_silence;
        do{
            if (config.mNeedTTS){
                rb_id = R.id.rb_tts;
                break;
            }
            if (config.mNeedSound) {
                rb_id = R.id.rb_sound;
                break;
            }
        }while(false);
        viewHolder.tipRadioGroup.check(rb_id);
        return convertView;
    }

    @Override
    public Object getItem(int i) {
        return mConfigs.configMap.get(mConfigs.configNames.get(i));
    }

    @Override
    public int getCount() {
        return mConfigs.configNames.size();
    }

    public static interface  EventOperations{
        public static class MenuItemData{
            public String mText;
            public int mItemId;
            private menu_opt mOpt;
            public MenuItemData(String tag, int id, menu_opt opt){
                mText = tag;
                mItemId = id;
                mOpt = opt;
            }
        }
        public static interface  menu_opt{
            public void opt();
        }

        public boolean save(int index, WeChatListener.WeChatNotificationConfig config);
        public boolean cancel(int index);
        public boolean delect(int index);
        public boolean edit(int index);
        public boolean menu_select(View view, List<MenuItemData> data, PopupMenu.OnMenuItemClickListener itemlistener);
    };

    private static class ViewHolder{
        public EditText nameEditView;
        public Button moreEditBtn;
        public RadioGroup tipRadioGroup;
        public RadioButton rbSound;
        public RadioButton rbSilence;
        public RadioButton rbTts;
        public CheckBox vibrateCheckBox;
        public int index;
        public WeChatListener.ConfigStatus mConfigStatus = WeChatListener.ConfigStatus.STATUS_NONE;
        private View.OnClickListener mOnClickListener;
        private RadioGroup.OnCheckedChangeListener mRGOnCheckedChangeListener;
        private CheckBox.OnCheckedChangeListener mCBOnCheckedChangeListener;
        private WeChatListener.WeChatNotificationConfig mConfig;
        public EventOperations mOperations = null;
        private final static int MENU_ITEM_ID_NONE = 10000;
        private final static int MENU_ITEM_ID_EDIT = 10001;
        private final static int MENU_ITEM_ID_SAVE = 10002;
        private final static int MENU_ITEM_ID_CANCEL = 10003;
        private final static int MENU_ITEM_ID_MORE = 10004;
        private final static int MENU_ITEM_ID_DEL = 10005;
        List<EventOperations.MenuItemData> mItemData;

        private void notifySave(){
            EventOperations operations = mOperations;
            mConfig.mName = nameEditView.getText().toString();
            if (operations != null){
                operations.save(index, mConfig);
            }
        }

        private void notifyEdit(){
            EventOperations operations = mOperations;
            if (operations != null){
                operations.edit(index);
            }
        }

        private void notifyCancel(){
            EventOperations operations = mOperations;
            if (operations != null){
                operations.cancel(index);
            }
        }

        private void notifyDel(){
            EventOperations operations = mOperations;
            if (operations != null){
                operations.delect(index);
            }
        }

        private void notifySelectItem(View view){
            EventOperations operations = mOperations;
            PopupMenu.OnMenuItemClickListener itemListener = new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Log.d(TAG, "item_id:" + item.getItemId());
                    for (EventOperations.MenuItemData data : mItemData){
                        if (data.mItemId == item.getItemId()){
                            if (data.mOpt != null){
                                data.mOpt.opt();
                            }
                        }
                    }
                    return false;
                }
            };

            if (operations != null){
                List<EventOperations.MenuItemData> data = new ArrayList<EventOperations.MenuItemData>();
                for(int i = 0; i < mItemData.size(); ++i){
                    EventOperations.MenuItemData item = mItemData.get(i);
                    if (mConfigStatus == WeChatListener.ConfigStatus.STATUS_NONE){
                        switch (item.mItemId){
                            case MENU_ITEM_ID_MORE:
                            case MENU_ITEM_ID_EDIT:
                                data.add(item);
                                break;
                            case MENU_ITEM_ID_DEL:
                                //默认的不能删除
                                if (index > 0) {
                                    data.add(item);
                                }
                                break;
                        }
                    } else if (mConfigStatus == WeChatListener.ConfigStatus.STATUS_ADD){
                        switch (item.mItemId){
                            case MENU_ITEM_ID_SAVE:
                            case MENU_ITEM_ID_CANCEL:
                            case MENU_ITEM_ID_MORE:
                                data.add(item);
                                break;
                        }
                    }else {
                        switch (item.mItemId){
                            case MENU_ITEM_ID_SAVE:
                            case MENU_ITEM_ID_CANCEL:
                            case MENU_ITEM_ID_MORE:
                                data.add(item);
                                break;
                            case MENU_ITEM_ID_DEL:
                                //默认的不能删除
                                if (index > 0) {
                                    data.add(item);
                                }
                                break;
                        }
                    }
                }
                operations.menu_select(view, data, itemListener);
            }
        }


        public ViewHolder(){
            mItemData = new ArrayList<EventOperations.MenuItemData>();

            mItemData.add(new EventOperations.MenuItemData("编辑", MENU_ITEM_ID_EDIT, new EventOperations.menu_opt(){
                @Override
                public void opt() {
                    notifyEdit();
                }
            }));

            mItemData.add(new EventOperations.MenuItemData("保存", MENU_ITEM_ID_SAVE, new EventOperations.menu_opt(){
                @Override
                public void opt() {
                    notifySave();
                }
            }));
            mItemData.add(new EventOperations.MenuItemData("取消", MENU_ITEM_ID_CANCEL, new EventOperations.menu_opt(){
                @Override
                public void opt() {
                    notifyCancel();
                }
            }));
            mItemData.add(new EventOperations.MenuItemData("删除", MENU_ITEM_ID_DEL, new EventOperations.menu_opt(){
                @Override
                public void opt() {
                    notifyDel();
                }
            }));
            mItemData.add(new EventOperations.MenuItemData("更多", MENU_ITEM_ID_MORE, new EventOperations.menu_opt(){
                @Override
                public void opt() {
                    ;
                }
            }));

            mConfig = new WeChatListener.WeChatNotificationConfig();

            mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()){
                        case R.id.more_edit_selector:
                            notifySelectItem(view);
                            break;
                    }
                }
            };

            mRGOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener(){
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    mConfig.mNeedSound = false;
                    mConfig.mNeedTTS = false;
                    switch(checkedId){
                        case R.id.rb_tts:
                            mConfig.mNeedTTS = true;
                            break;
                        case R.id.rb_sound:
                            mConfig.mNeedSound = true;
                            break;
                        case R.id.rb_silence:
                            break;
                    }
                }
            };

            mCBOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    mConfig.mNeedVibrate = b;
                }
            };

        }
    }

}
