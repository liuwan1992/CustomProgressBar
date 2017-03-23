package com.liuwan.customprogressbar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity implements View.OnClickListener {

    private CustomProgressBar mCustomProgressBar;
    private float mProgress;
    private int mStateType;
    private DownloadHandler mDownloadHandler;

    private static final int STATE_DEFAULT = 101;
    private static final int STATE_DOWNLOADING = 102;
    private static final int STATE_PAUSE = 103;
    private static final int STATE_DOWNLOAD_FINISH = 104;

    private static class DownloadHandler extends Handler {

        private WeakReference<Context> reference;

        DownloadHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity downloadActivity = (MainActivity) reference.get();
            if (downloadActivity != null) {
                switch (msg.what) {
                    case 0:
                        if (downloadActivity.mProgress < 100) {
                            downloadActivity.mProgress += 2.0;
                            downloadActivity.mCustomProgressBar.setProgress(downloadActivity.mProgress);
                            downloadActivity.mDownloadHandler.sendEmptyMessageDelayed(0, 300);
                        } else {
                            downloadActivity.mStateType = STATE_DOWNLOAD_FINISH;
                            downloadActivity.mCustomProgressBar.setState(downloadActivity.mStateType);
                        }
                        break;

                    default:
                        break;
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownloadHandler = new DownloadHandler(this);
        mCustomProgressBar = (CustomProgressBar) findViewById(R.id.icon_text_progressbar);
        mCustomProgressBar.setOnClickListener(this);
        mStateType = STATE_DEFAULT;
        mCustomProgressBar.setState(mStateType);
    }

    @Override
    public void onClick(View v) {
        switch (mStateType) {
            case STATE_DEFAULT:
                mProgress = 0;
                mCustomProgressBar.setProgress(mProgress);
                mStateType = STATE_DOWNLOADING;
                mCustomProgressBar.setState(mStateType);
                mDownloadHandler.sendEmptyMessageDelayed(0, 500);
                break;

            case STATE_DOWNLOADING:
                mStateType = STATE_PAUSE;
                mCustomProgressBar.setState(mStateType);
                mDownloadHandler.removeMessages(0);
                break;

            case STATE_PAUSE:
                mStateType = STATE_DOWNLOADING;
                mCustomProgressBar.setState(mStateType);
                mDownloadHandler.sendEmptyMessageDelayed(0, 500);
                break;

            case STATE_DOWNLOAD_FINISH:
                mStateType = STATE_DEFAULT;
                mCustomProgressBar.setState(mStateType);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDownloadHandler.removeMessages(0);
    }

}
