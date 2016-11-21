package com.taobao.openimui.demo;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.FeedbackAPI;
import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.WxLog;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.conversation.EServiceContact;
import com.alibaba.mobileim.conversation.IYWMessageListener;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.fundamental.widget.YWAlertDialog;
import com.alibaba.mobileim.login.YWLoginState;
import com.alibaba.mobileim.utils.IYWCacheService;
import com.alibaba.openIMUISJTU.LoginActivity;
import com.alibaba.openIMUISJTU.R;
import com.taobao.openimui.common.Notification;
import com.taobao.openimui.common.SimpleWebViewActivity;
import com.taobao.openimui.sample.DemoSimpleKVStore;
import com.taobao.openimui.sample.LoginSampleHelper;
import com.taobao.openimui.sample.NotificationInitSampleHelper;
import com.taobao.openimui.test.MultiAccountTestActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HeadlineFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = HeadlineFragment.class.getSimpleName();
    private static final String PAGE1 = "http://chuye.cloud7.com.cn/7086991";
    private Activity mContext;
    private String mUserId;
    private View mView;
    private YWIMKit mIMKit;
    private IYWCacheService mCacheService;

    public HeadlineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIMKit = LoginSampleHelper.getInstance().getIMKit();
        if (mIMKit == null) {
            return;
        }
        mCacheService = mIMKit.getCacheService();
        mUserId = mIMKit.getIMCore().getLoginUserId();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.demo_fragment_more, container, false);
        init();
        return mView;
    }

    protected void init() {
        mContext.getWindow().setWindowAnimations(0);
        initTitle();
        initTest();
    }

    private void initTitle() {
        RelativeLayout titleBar = (RelativeLayout) mView.findViewById(R.id.title_bar);
        TextView titleView = (TextView) mView.findViewById(R.id.title_self_title);
        TextView leftButton = (TextView) mView.findViewById(R.id.left_button);

        titleBar.setBackgroundColor(Color.parseColor("#00b4ff"));
        titleView.setTextColor(Color.WHITE);
        titleView.setText("党建课堂");
        titleBar.setVisibility(View.VISIBLE);
        leftButton.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        boolean oldCheck;
        switch (v.getId()) {
            case R.id.more_logout:
                showAlertDialog();
                break;
            case R.id.more1:
                viewUrlInSimpleWebView(PAGE1, "关于我们");
                break;
            default:
                break;
        }
    }


    private void showAlertDialog(){
        AlertDialog.Builder builder = new YWAlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.quit_confirm))
                .setCancelable(false)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                // logout();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.dismiss();
                            }
                        });
        AlertDialog dialog = builder.create();
        if (!dialog.isShowing()){
            dialog.show();
        }
    }

    /**
     * 内置WebView打开（如果不支持，再用系统的浏览器打开）
     *
     * @param url
     * @param title
     */
    private void viewUrlInSimpleWebView(String url, String title) {
        Intent intent = new Intent(getActivity(), SimpleWebViewActivity.class);
        intent.putExtra(SimpleWebViewActivity.URL, url);
        intent.putExtra(SimpleWebViewActivity.TITLE, title);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        YWLog.e(TAG, "onDestroy");
    }

    private void initTest(){
        TextView test = (TextView) mView.findViewById(R.id.more_test_multi_account);
        test.setVisibility(View.VISIBLE);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MultiAccountTestActivity.class);
                startActivity(intent);
            }
        });
    }

}
