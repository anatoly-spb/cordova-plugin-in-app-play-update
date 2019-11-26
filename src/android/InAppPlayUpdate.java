package ru.rlisystems.mobile.plugins.inappplayupdate;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Context;

import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.install.model.UpdateAvailability;

public class InAppPlayUpdate extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("update".equals(action)) {
            update(callbackContext);
            return true;
        }
        return false;
    }

    private void update(CallbackContext callbackContext) {
        /*
        if (msg == null || msg.length() == 0) {
            callbackContext.error("Empty message!");
        } else {
            //Toast.makeText(webView.getContext(), msg, Toast.LENGTH_LONG).show();
            callbackContext.success(msg);
        }
        */
        // Creates instance of the manager.
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(webView.getContext());

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
              // For a flexible update, use AppUpdateType.FLEXIBLE
              && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
             // Request the update.
            }
        });
    }
}