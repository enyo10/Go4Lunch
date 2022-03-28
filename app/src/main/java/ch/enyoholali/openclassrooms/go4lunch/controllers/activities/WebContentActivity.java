package ch.enyoholali.openclassrooms.go4lunch.controllers.activities;

import android.content.Intent;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import ch.enyoholali.openclassrooms.go4lunch.base.BaseActivity;
import ch.enyoholali.openclassrooms.go4lunch.databinding.ActivityWebContentBinding;

public class WebContentActivity extends BaseActivity<ActivityWebContentBinding> {

    // For Debug
    private static final String TAG = WebContentActivity.class.getSimpleName();

    @Override
    public void configureView() {
        Log.d(TAG," web content activity ");
        Intent intent = getIntent();
        String url = intent.getStringExtra(PlaceDetailsActivity.BUNDLE_CONTENT_URL);
        WebView webView = binding.activityWebViewLayout;

        Log.d(TAG," url  "+url);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Show the page of the news
        webView.loadUrl(url);

    }


    @Override
    public void loadData() {

    }

    @Override
    protected ActivityWebContentBinding getViewBinding() {
        return ActivityWebContentBinding.inflate(getLayoutInflater());
    }


}