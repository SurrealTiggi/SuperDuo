package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.service.myFetchService;

/**
 * Created by Tiggi on 1/7/2016.
 */
public class FSWidgetProvider extends AppWidgetProvider {

    private static final String TAG = AppWidgetProvider.class.getSimpleName();

    private static Handler sWorkerQueue;
    private static HandlerThread sWorkerThread;
    private static UpdateWidgetObserver sUpdateObserver;

    private AppWidgetManager mAppWidgetManager;

    public FSWidgetProvider() {
        Log.d(TAG, "MyWidgetProvider()");

        sWorkerThread = new HandlerThread("ScoresProvider-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled()");

        final ContentResolver contentResolver = context.getContentResolver();

        if(sUpdateObserver == null) {
            Log.d(TAG, "No widget observer found, going to create one...");

            try {
                mAppWidgetManager = AppWidgetManager.getInstance(context);
                sUpdateObserver = new UpdateWidgetObserver(mAppWidgetManager, context, sWorkerQueue);
                contentResolver.registerContentObserver(DatabaseContract.BASE_CONTENT_URI, true, sUpdateObserver);
            }
            catch (Exception e) {
                Log.e(TAG, "ERROR: Unable to register ContentObserver", e);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate()");

        // Get all available widgets
        ComponentName thisWidget = new ComponentName(context, FSWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // Background intent to call main asynctask service
        Intent intent = new Intent(context, myFetchService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

        // Update widgets via above intent
        context.startService(intent);
    }



}
