package com.taobao.openimui.demo;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.utils.IYWCacheService;
import com.alibaba.openIMUISJTU.R;
import com.taobao.openimui.common.SimpleWebViewActivity;
import com.taobao.openimui.sample.LoginSampleHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = NewsFragment.class
            .getSimpleName();
    private static final String PAGE_DANGJIAN_PEOPLE = "http://dangjian.people.com.cn/";
    private Activity mContext;
    private String mUserId;
    private View mView;
    private YWIMKit mIMKit;
    private IYWCacheService mCacheService;
    private WebView mWebView;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    private ProgressBar progressbar;

    public NewsFragment() {
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
        mView = inflater.inflate(R.layout.sjtu_fragment_news, container, false);
        init();
        return mView;
    }

    protected void init() {
        mContext.getWindow().setWindowAnimations(0);
        initTitle();

        mWebView = (WebView) mView.findViewById(R.id.webview_news);

        WebSettings settings = mWebView.getSettings();
        settings.setSavePassword(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            settings.setPluginState(WebSettings.PluginState.ON);
        }
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDisplayZoomControls(false);
        settings.setBuiltInZoomControls(true);
        settings.setDomStorageEnabled(true);

        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染优先级

        //进度条
        progressbar = new ProgressBar(mView.getContext(), null, android.R.attr.progressBarStyleHorizontal);

        progressbar.setMax(100);
        progressbar.setProgressDrawable(this.getResources().getDrawable(
                R.drawable.demo_progress_bar_states));
        progressbar.setProgress(5); //先加载5%，以使用户觉得界面没有卡死
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, 6);
        params.addRule(RelativeLayout.ABOVE, R.id.simple_webview);
        ViewParent parent = mWebView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).addView(progressbar, params);
        }

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                progressbar.setVisibility(View.GONE);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress >= 5){
                    progressbar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        mWebView.loadUrl(PAGE_DANGJIAN_PEOPLE);
    }

    private void initTitle() {
        RelativeLayout titleBar = (RelativeLayout) mView.findViewById(R.id.title_bar);
        TextView titleView = (TextView) mView.findViewById(R.id.title_self_title);
        TextView leftButton = (TextView) mView.findViewById(R.id.left_button);

        titleBar.setBackgroundColor(Color.parseColor("#00b4ff"));
        titleView.setTextColor(Color.WHITE);
        titleView.setText("今日新闻");
        titleBar.setVisibility(View.VISIBLE);
        leftButton.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
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

}
