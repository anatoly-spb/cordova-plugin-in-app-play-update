package ru.rlisystems.cordova.plugins.inappplayupdate;

import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.atomic.AtomicReference;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;

public class InAppPlayUpdate extends CordovaPlugin {
    private static final int MY_REQUEST_CODE = 777;
    private final AtomicReference<CallbackContext> currentCallbackContext = new AtomicReference<CallbackContext>();

    public InAppPlayUpdate() {
        Log.i(InAppPlayUpdate.class.getSimpleName(), "Constructed");
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        cordova.setActivityResultCallback(this);
        Log.i(InAppPlayUpdate.class.getSimpleName(), "Initialized");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i(InAppPlayUpdate.class.getSimpleName(), String.format("Executing the %s action started", action));
        if ("update".equals(action)) {
            update(callbackContext);
            return true;
        }
        return false;
    }

    private void update(CallbackContext callbackContext) {
        currentCallbackContext.set(callbackContext);

        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // Creates instance of the manager.
                AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(cordova.getContext());

                // Returns an intent object that you use to check for an update.
                Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

                // Checks that the platform will allow the specified type of update.
                appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            // For a flexible update, use AppUpdateType.FLEXIBLE
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        // Request the update.
                        try {
                            if (appUpdateManager.startUpdateFlowForResult(
                                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                    appUpdateInfo,
                                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                    AppUpdateType.IMMEDIATE,
                                    // The current activity making the update request.
                                    cordova.getActivity(),
                                    // Include a request code to later monitor this update request.
                                    MY_REQUEST_CODE)) {
                            } else {
                                callbackContext.error("error");
                            }
                        } catch (IntentSender.SendIntentException e) {
                            callbackContext.error("error");
                            e.printStackTrace();
                        }
                    } else {
                        callbackContext.success("uptodate");
                    }
                });

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == MY_REQUEST_CODE) {
            final CallbackContext callbackContext = currentCallbackContext.getAndSet(null);
            if (resultCode != RESULT_OK) {
                if (callbackContext != null) {
                    switch(resultCode) {
                        case RESULT_CANCELED:
                            callbackContext.error("canceled");
                            break;
                        case RESULT_IN_APP_UPDATE_FAILED:
                            callbackContext.error("failed");
                            break;
                        default:
                            callbackContext.error("error");
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }
}