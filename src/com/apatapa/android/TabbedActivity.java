package com.apatapa.android;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TabbedActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        
        TabHost tabHost = getTabHost();
        
        // "Applaud" tab
        TabSpec applaudSpec = tabHost.newTabSpec("Applaud");
        applaudSpec.setIndicator("Applaud");
        Intent applaudIntent = new Intent(this, ApplaudActivity.class);
        applaudSpec.setContent(applaudIntent);
        
        // "Newsfeed" tab
        TabSpec newsfeedSpec = tabHost.newTabSpec("News");
        newsfeedSpec.setIndicator("News");
        Intent newsIntent = new Intent(this, NewsfeedActivity.class);
        newsfeedSpec.setContent(newsIntent);
        
        // "Questions" tab
        TabSpec questionsSpec = tabHost.newTabSpec("Questions");
        questionsSpec.setIndicator("Questions");
        Intent questionsIntent = new Intent(this, QuestionsActivity.class);
        questionsSpec.setContent(questionsIntent);
        
        tabHost.addTab(applaudSpec);
        tabHost.addTab(questionsSpec);
        tabHost.addTab(newsfeedSpec);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tabbed, menu);
        return true;
    }

    
}
