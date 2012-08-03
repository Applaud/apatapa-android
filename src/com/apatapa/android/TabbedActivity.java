package com.apatapa.android;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.Menu;

public class TabbedActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tabbed, menu);
        return true;
    }

    
}
