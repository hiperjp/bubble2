package com.nkanaev.comics.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.nkanaev.comics.BuildConfig;
import com.nkanaev.comics.R;
import com.nkanaev.comics.fragment.ReaderFragment;
import com.nkanaev.comics.managers.Utils;

import java.io.File;


public class ReaderActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (false && BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
                            //.detectDiskReads()
                            //.detectDiskWrites()
                            //.detectNetwork()
                            // or for all detectable problems
                            .detectAll()
                            .penaltyFlashScreen()
                            .penaltyLog()
                            .build()
            );
            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder()
                            .detectLeakedSqlLiteObjects()
                            .detectLeakedClosableObjects()
                            .penaltyLog()
                            .penaltyDeath()
                            .build()
            );
        }
        super.onCreate(savedInstanceState);
        
        // Android 16 optimization: disable all animations for instant page navigation
        Utils.disableAllAnimations(this);
        
        setContentView(R.layout.layout_reader);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_reader);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
                ReaderFragment fragment = ReaderFragment.create( getIntent() );
                setFragment(fragment);
            }
            else {
                Bundle extras = getIntent().getExtras();
                ReaderFragment fragment = null;
                ReaderFragment.Mode mode = (ReaderFragment.Mode) extras.getSerializable(ReaderFragment.PARAM_MODE);

                if (mode == ReaderFragment.Mode.MODE_LIBRARY)
                    fragment = ReaderFragment.create(extras.getInt(ReaderFragment.PARAM_HANDLER));
                else
                    fragment = ReaderFragment.create((File) extras.getSerializable(ReaderFragment.PARAM_HANDLER));
                setFragment(fragment);
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.action_bar_title_layout);
            actionBar.setTitle("");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        TextView titleView = findViewById(R.id.action_bar_title);
        if (titleView!=null)
            titleView.setText(title);
        else
            getSupportActionBar().setTitle(title);
    }

    public void setSubTitle(CharSequence title) {
        TextView subtitle = (TextView) findViewById(R.id.action_bar_subtitle);
        if (subtitle==null)
            return;

        if (title==null||title.toString().isEmpty()) {
            subtitle.setVisibility(View.GONE);
            title="";
        } else {
            subtitle.setVisibility(View.VISIBLE);
        }
        subtitle.setText(title);
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame_reader, fragment)
                .commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
