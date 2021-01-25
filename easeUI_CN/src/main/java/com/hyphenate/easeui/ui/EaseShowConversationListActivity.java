package com.hyphenate.easeui.ui;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hyphenate.easeui.R;

/**
 * show the video
 * 
 */
public class EaseShowConversationListActivity extends EaseBaseActivity{
	private static final String TAG = "EaseShowConversationListActivity";
	
	private RelativeLayout loadingLayout;
	private ProgressBar progressBar;
	private String localFilePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.ease_showconversationlist_activity);

	}
	@Override
	public void onBackPressed() {
		finish();
	}
}
