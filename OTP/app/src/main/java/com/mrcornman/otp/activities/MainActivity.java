package com.mrcornman.otp.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.fragments.ClientListFragment;
import com.mrcornman.otp.fragments.GameFragment;
import com.mrcornman.otp.fragments.MakerListFragment;
import com.mrcornman.otp.fragments.ProfileFragment;
import com.mrcornman.otp.fragments.SettingsFragment;
import com.mrcornman.otp.fragments.NavFragment;

public class MainActivity extends Activity implements NavFragment.NavigationDrawerCallbacks, ClientListFragment.ClientListInteractionListener {

    /**
     * Navigation Identifiers
     */
    public static final int NAV_GAME = 0;
    public static final int NAV_SETTINGS = 1;
    public static final int NAV_PROFILE = 2;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavFragment mNavFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the drawer.
        mNavFragment = (NavFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavFragment.setUp((DrawerLayout) findViewById(R.id.drawer_layout));

        // start up progress dialog until MessageService is started
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setTitle("Please wait...");
        progressDialog.show();

        // init receiver for successful startup broadcast from MessageService
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("MainActivity", "We got yo broadcast");
                boolean success = intent.getBooleanExtra("success", false);
                progressDialog.dismiss();

                if(!success) {
                    Toast.makeText(getApplicationContext(),
                            "There was a problem with the activity_messaging service, please restart the app",
                            Toast.LENGTH_LONG).show();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("com.mrcornman.otp.activities.MainActivity"));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = null;

        switch(position) {
            case NAV_GAME:
                fragment = GameFragment.newInstance();
                break;
            case NAV_SETTINGS:
                fragment = SettingsFragment.newInstance();
                break;
            case NAV_PROFILE :
                fragment = ProfileFragment.newInstance();
                break;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                //.addToBackStack(null)
                .commit();
        onSectionAttached(position + 1);
        restoreActionBar();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = "Game";
                break;
            case 2:
                mTitle = "Settings";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = false;
        int id = item.getItemId();

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = null;

        switch(id) {
            case R.id.action_client_list:
                fragment = ClientListFragment.newInstance(1);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                                //.addToBackStack(null)
                        .commit();

                restoreActionBar();
                handled = true;
                break;
            case R.id.action_matchmaker_list:
                fragment = MakerListFragment.newInstance(1);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                                //.addToBackStack(null)
                        .commit();

                restoreActionBar();
                handled = true;
                break;
        }

        return handled || super.onOptionsItemSelected(item);
    }

    // fragment interface actions
    @Override
    public void onRequestOpenConversation(String recipientId) {
        openConversation(recipientId);
    }

    // helpers
    public void openConversation(String recipientId) {
        // TODO: Make sure the user exists when populating the list view in client list fragment so that there isn't the potential problem of the user not existing here
        Intent intent = new Intent(getApplicationContext(), MessagingActivity.class);
        intent.putExtra("recipient_id", recipientId);
        startActivity(intent);
        Log.i("MainActivity", "Beginning conversation with " + recipientId);
    }
}