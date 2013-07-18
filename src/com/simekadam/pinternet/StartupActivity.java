package com.simekadam.pinternet;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class StartupActivity extends Activity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_about:
                goToAbout();
                return true;
            case R.id.menu_settings:
                Toast toast = Toast.makeText(getApplicationContext(),"There are no preferences..If you want to add some, let me know..:)", Toast.LENGTH_SHORT );
                toast.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void goToAbout() {
        Intent mIntent = new Intent(this, AboutActivity.class);
        startActivity(mIntent);
    }

    private void goToSettings() {
        Intent mIntent = new Intent(this, SettingsActivity.class);
        startActivity(mIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final ActionBar actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);







    }


}



