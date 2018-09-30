package cordova.plugin.instamojopayment;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import io.ionic.cracker.R;

public class WebActivity extends Activity {

    WebView webView;
    String url;
    String orderId;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String package_name = getApplication().getPackageName();

        setContentView(getApplication().getResources().getIdentifier("activity_web", "layout", package_name));
        // getActionBar().hide();
        android.app.ActionBar toolbar = getActionBar();
        if (toolbar != null) {
            toolbar.hide();
        }
        webView = findViewById(R.id.webView);
        url = getIntent().getStringExtra("url");

        if (url != null){
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient() {
                ProgressDialog progressDialog = null;

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    // Toast.makeText(getContext(),"Page started",Toast.LENGTH_SHORT).show();
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(WebActivity.this);
                        progressDialog.setMessage("Please wait. Loading..");
                        progressDialog.show();
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    // Log.d("Another urls", view);
                    // Toast.makeText(getContext(),"Page Finished",Toast.LENGTH_SHORT).show();
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                        // progressDialog = null;
                    }
                }

                @Override
                @TargetApi(21)
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    Log.d("urls", request.getUrl().toString());
                    String url = request.getUrl().toString();

                    if (url.matches("https:\\/\\/cde-staging\\.instamojo\\.com\\/juspay\\/return\\/.+")) {
                        orderId = request.getUrl().getQueryParameter("order_id");
                        status = request.getUrl().getQueryParameter("status");
                    } else if (url.matches("https:\\/\\/test\\.instamojo\\.com\\/order\\/status.+")) {
                        onBackPressed();
                    }
                    return super.shouldOverrideUrlLoading(view, request);
                }
            });

            webView.loadUrl(url);
        }else{
            Toast.makeText(this, "Please provide url", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("orderId", orderId);
        intent.putExtra("status", status);
        setResult(0, intent);
        super.onBackPressed();
    }
}