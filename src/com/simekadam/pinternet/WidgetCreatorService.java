package com.simekadam.pinternet;

import android.*;
import android.R;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: simekadam
 * Date: 12/1/12
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class WidgetCreatorService extends IntentService {

    private static final String TAG = WidgetCreatorService.class.getSimpleName();
    private String iconTitle;
    private Notification statusNotification;
    private Handler mHandler;
    private Intent shareIntent;
    private Bitmap lastTaskIcon;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private Bitmap notificationIcon;

    public WidgetCreatorService() {
        super("WidgetCreatorService");
    }

    @Override
    public void onCreate() {
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.
        mBuilder =
                new NotificationCompat.Builder(this);
        mHandler = new Handler();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.iconTitle =   intent.getStringExtra(Intent.EXTRA_SUBJECT);

        //create the notification
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), com.simekadam.pinternet.R.drawable.ic_stat_pinternet_ico_white);

                        mBuilder.setSmallIcon(R.drawable.stat_sys_download)
                                .setLargeIcon(largeIcon)
                        .setContentTitle("Adding the shorctut")
                        .setContentText("Downloading the icon, please wait.")
                        .setOngoing(true)
                .setProgress(0,0, true)
                ;

        //show the notification

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());


        //get last task icon
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> recentTasks =  am.getRecentTasks(5, 0);
        PackageManager pm = getPackageManager();
        try {

            Resources resources = pm.getResourcesForActivity(recentTasks.get(0).baseIntent.getComponent());
            ApplicationInfo ai = pm.getApplicationInfo(recentTasks.get(0).baseIntent.getComponent().getPackageName(), 0);
            Drawable appIcon = resources.getDrawableForDensity(ai.icon, am.getLauncherLargeIconDensity());

                    lastTaskIcon = ((BitmapDrawable) appIcon).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



        shareIntent = intent;
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        String url = "";
        Pattern urlpattern = Patterns.WEB_URL;
        Matcher m = urlpattern.matcher(text);
        if(m.find())
        {
           url = m.group();
        }
        else
        {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    showToast("There is no url in shared text, sorry.", Toast.LENGTH_SHORT);
                }
            });

            return;
        }

        Intent openUrlIntent = new Intent();
        openUrlIntent.setAction(Intent.ACTION_VIEW);
        openUrlIntent.setData(Uri.parse(url));

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, openUrlIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,iconTitle );
        Bitmap icon =  getFavicon(url);
        notificationIcon = icon;


        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);


        mHandler.post(new Runnable() {
            @Override
            public void run() {


//                showToast("Shortcut is now on your homescreen.", Toast.LENGTH_SHORT);
                cancelNotification(0);

                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);


                PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, startMain, PendingIntent.FLAG_ONE_SHOT);

//                mBuilder.setContentIntent(resultPendingIntent);


                mBuilder.setContentText("Touch to view it on your homescreen.")
                        .setContentTitle("Shortcut added")
                        .setProgress(0,0,false)
                        .setOngoing(false)
                        .setSmallIcon(R.drawable.stat_sys_download_done)
                        .setTicker("Shortcut is now on your homescreen.")
                        .setContentIntent(resultPendingIntent)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(notificationIcon).setSummaryText("Touch to view it on your homescreen."))
                .setAutoCancel(true)
                        ;


                showNotification(0, mBuilder);

            }
        });


    }


    private Bitmap getFavicon(String urlstring) {

        HtmlCleaner cleaner = new HtmlCleaner();


        URL url;
        try {
            url = new URL(urlstring);
        } catch (IOException e) {
            return null;
        }
        TagNode node = new TagNode("");
        try {


            InputStream is = OpenHttpConnection(urlstring);
            node = cleaner.clean(is);
        } catch (IOException e) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    showToast("Problem with connection, default icon will be used", Toast.LENGTH_SHORT);
                }
            });

        }
        TagNode tags[];
        String iconUri = "";
        tags = node.getElementsByAttValue("rel", "apple-touch-icon", true, true);
        if (tags.length == 0) {
            tags = node.getElementsByAttValue("rel", "apple-touch-icon-precomposed", true, true);
        }
        if (tags.length == 0) {
            tags = node.getElementsByAttValue("rel", "shortcut icon", true, true);
        }
        if (tags.length == 0) {
            iconUri = "/favicon.ico";

        }
        else iconUri = tags[0].getAttributeByName("href");
        if (iconUri.indexOf("http") == -1) {

            iconUri = url.getProtocol() + "://" + url.getHost() + iconUri;
        }


        InputStream is = null;
        try {
            is = OpenHttpConnection(iconUri);

        } catch (IOException e) {
            Log.d("icon uri", e.getLocalizedMessage());
        }

        Bitmap bitmap = imageReader(is);

        //get the size of the launcher icon
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int iconSize = am.getLauncherLargeIconSize();

        bitmap = Bitmap.createScaledBitmap(bitmap, iconSize, iconSize, true);
        return bitmap;
    }

    private InputStream OpenHttpConnection(String strURL) throws IOException {
        InputStream inputStream = null;

        HttpGet request = new HttpGet(strURL);


        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(request);

        HttpEntity entity = response.getEntity();

        inputStream = entity.getContent();

        return inputStream;

    }


    private Bitmap imageReader(InputStream inputStream)
    {
        ByteArrayOutputStream bais = new ByteArrayOutputStream();



        try {

            byte[] byteChunk = new byte[1024]; // Or whatever size you want to read in at a time.
            int n;

            while ( (n = inputStream.read(byteChunk)) > 0 ) {
                bais.write(byteChunk, 0, n);
            }
            bais.flush();
        }
        catch (IOException e) {

        }
        catch(NullPointerException ex)
        {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    showToast("There was problem with the connection, default icon will be used", Toast.LENGTH_SHORT);
                }
            });

        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        byte[] byteArray = bais.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


        if(bitmap == null)
        {
            bitmap = lastTaskIcon;
//            Canvas canvas = new Canvas(bitmap);
//            Paint paint = new Paint();
//            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(Color.WHITE);
//            paint.setTextSize(20);
//            paint.setFakeBoldText(true);
//            paint.setTypeface(Typeface.SANS_SERIF);
//            canvas.drawText(iconTitle, 10, (bitmap.getHeight()/(float)1.5), paint );

        }
        return bitmap;
    }


    private void showToast(String text, int length)
    {
        Toast toast = Toast.makeText(WidgetCreatorService.this,text, length );
        toast.show();


    }

    private void showNotification(int id, NotificationCompat.Builder builder)
    {
        mNotificationManager.notify(id, builder.build());

    }

    private void cancelNotification(int id)
    {
        mNotificationManager.cancel(id);
    }


}
