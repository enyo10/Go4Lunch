package ch.enyoholali.openclassrooms.go4lunch.controllers.activities;

import android.content.Intent;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.viewbinding.ViewBinding;

import butterknife.BindView;
import ch.enyoholali.openclassrooms.go4lunch.R;
import ch.enyoholali.openclassrooms.go4lunch.base.BaseActivity;

public class WebContentActivity extends BaseActivity {

    // For Debug
    private static final String TAG = WebContentActivity.class.getSimpleName();

    @BindView(R.id.activity_web_view_layout)
    WebView mWebView;


    @Override
    public int getActivityLayout() {
        return R.layout.activity_web_content;
    }

    @Override
    public void configureView() {
        Log.d(TAG," web content activity ");
        Intent intent = getIntent();
        String url = intent.getStringExtra(PlaceDetailsActivity.BUNDLE_CONTENT_URL);

        Log.d(TAG," url  "+url);
        mWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Show the page of the news
        mWebView.loadUrl(url);

    }

    @Override
    public ViewBinding initViewBinding() {
        return null;
    }

    @Override
    public void loadData() {

    }
}