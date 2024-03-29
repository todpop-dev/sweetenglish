package com.todpop.sweetenglish;

import java.util.HashMap;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;

public class SweetEnglish extends Application {
	private static final String PROPERTY_ID = "UA-53166975-1";

    public static int GENERAL_TRACKER = 0;
    
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }
    
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public SweetEnglish(){
    	super();
    }
    
    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

          GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
          Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
              : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                  : analytics.newTracker(R.xml.ecommerce_tracker);
          mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}
