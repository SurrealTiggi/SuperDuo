package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilities;

/**
 * Created by Tiggi on 1/7/2016.
 */
public class UpdateWidgetObserver extends ContentObserver {

    private static final String TAG = UpdateWidgetObserver.class.getSimpleName();

    private AppWidgetManager mAppWidgetManager;
    private Context mContext;
    private RemoteViews mRemoteViews;

    public static final int COLUMN_DATE = 1;
    public static final int COLUMN_MATCHTIME = 2;
    public static final int COLUMN_HOME = 3;
    public static final int COLUMN_AWAY = 4;

    public UpdateWidgetObserver(AppWidgetManager appWidgetManager, Context context, Handler handler) {
        super(handler);
        Log.d(TAG, "UpdateWidgetObserver()");
        mAppWidgetManager = appWidgetManager;
        mContext = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.d(TAG, "onChange()");

        try {
            Cursor c = mContext.getContentResolver().query(
                    DatabaseContract.BASE_CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            // Find the next event and update the widget accordingly
            this.fetchMatches(c);
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to find an event." , e);
        }
    }

    private boolean fetchMatches(Cursor c) throws Exception {
        Log.d(TAG, "findTheNextEvent()");

        Calendar calendar = Calendar.getInstance();

        while (c.moveToNext()) {

            int homeGoals = c.getInt(6);
            int awayGoals = c.getInt(7);
            Calendar event = Calendar.getInstance();

            //Checks to see if this is the next event and if we do not already have scores.
            if(homeGoals == -1 && awayGoals == -1 && calendar.before(event)) {
                String eventDate = c.getString(COLUMN_DATE);

                if(calendar.get(Calendar.DAY_OF_YEAR) == event.get(Calendar.DAY_OF_YEAR)) {
                    eventDate = "Today";
                }
                else if((calendar.get(Calendar.DAY_OF_YEAR) + 1) == event.get(Calendar.DAY_OF_YEAR)) {
                    eventDate = "Tomorrow";
                }

                this.updateWidget(
                        c.getString(COLUMN_HOME),
                        Utilities.getTeamCrestByTeamName(c.getString(COLUMN_HOME)),
                        c.getString(COLUMN_AWAY),
                        Utilities.getTeamCrestByTeamName(c.getString(COLUMN_AWAY)),
                        eventDate,
                        c.getString(COLUMN_MATCHTIME));

                return true;
            }
        }
        return false;
    }

    private void updateWidget(String homeName, int homeLogo, String awayName, int awayLogo, String matchDate, String matchTime) {
        Log.d(TAG, "updateWidget()");

        mRemoteViews = new RemoteViews(this.mContext.getPackageName(), R.layout.layout_fs_widget);
        ComponentName thisHereWidget = new ComponentName(mContext, FSWidgetProvider.class);
        int[] allWidgetIds = mAppWidgetManager.getAppWidgetIds(thisHereWidget);

        mRemoteViews.setTextViewText(R.id.textViewHomeName, homeName);
        mRemoteViews.setImageViewResource(R.id.imageViewHomeIcon, homeLogo);
        mRemoteViews.setContentDescription(R.id.textViewHomeName, mContext.getString(R.string.home_desc_text) + homeName);
        mRemoteViews.setTextViewText(R.id.widgetAwayName, awayName);
        mRemoteViews.setImageViewResource(R.id.widgetAwayCrest, awayLogo);
        mRemoteViews.setContentDescription(R.id.widgetAwayName, mContext.getString(R.string.away_desc_text) + awayName);
        mRemoteViews.setTextViewText(R.id.textViewNextMatchDate, matchDate);
        mRemoteViews.setContentDescription(R.id.textViewNextMatchDate, mContext.getString(R.string.date_desc_text) + matchDate);
        mRemoteViews.setTextViewText(R.id.textViewNextMatchTime, matchTime);
        mRemoteViews.setContentDescription(R.id.textViewNextMatchTime, mContext.getString(R.string.time_desc_text) + matchTime);

        // Tell the widget manager to update the widgets
        for(int widgetId : allWidgetIds) {
            mAppWidgetManager.updateAppWidget(widgetId, mRemoteViews);
        }
    }
}
