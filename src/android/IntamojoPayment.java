package cordova.plugin.instamojopayment;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * This class echoes a string called from JavaScript.
 */
public class IntamojoPayment extends CordovaPlugin {
    private CallbackContext PUBLIC_CALLBACKS = null;

    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("pay")) {
            this.pay(args, callbackContext);
            return true;
        }
        return false;
    }

    private void pay(JSONArray args, CallbackContext callbackContext) {
        try{
            PUBLIC_CALLBACKS = callbackContext;
            Context context = this.cordova.getActivity().getApplicationContext();
            String url = args.getJSONObject(0).getString("longurl");
            Intent intent = new Intent(context, WebActivity.class);
            intent.putExtra("url", url);
            cordova.startActivityForResult((CordovaPlugin) this, intent, 0);
            Log.d("Instamojo", "Called");
            // Send no result, to execute the callbacks later
            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true); // Keep callback
        }catch(Exception e){
            callbackContext.error("Something went wrong");
        }
    }


    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        // if (resultCode == cordova.getActivity().RESULT_OK) {
            Log.d("Instamojo", "Return" + cordova.getActivity().RESULT_OK);
            Bundle extras = data.getExtras();// Get data sent by the Intent
            String orderId = extras.getString("orderId"); // data parameter will be send from the other activity.
            String status = extras.getString("status"); // data parameter will be send from the other activity.
            if(orderId != null && status != null){
                String information = "{'orderId': '" + orderId + "', 'status': '" + status + "'}";
                PluginResult result = new PluginResult(PluginResult.Status.OK, information);
                result.setKeepCallback(true);
                PUBLIC_CALLBACKS.sendPluginResult(result);
            }else{
                String information = "{'status':  'PAYMENT_CANCELED'}";
                PluginResult result = new PluginResult(PluginResult.Status.OK, information);
                result.setKeepCallback(true);
                PUBLIC_CALLBACKS.sendPluginResult(result);
            }
            
        // }
    }

}
