package com.simekadam.pinternet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: simekadam
 * Date: 12/1/12
 * Time: 8:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class RouteActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
//        Log.d("intent", intent.getStringExtra(Intent.EXTRA_TEXT));
        intent.setClass(this, WidgetCreatorService.class);
//        intent.setAction(Intent.ACTION_SEND);
//        Intent createShortcutIntent = new Intent(this, WidgetCreatorService.class);
//        createShortcutIntent.setData(intent.getData());
        startService(intent);
        finish();
    }
}