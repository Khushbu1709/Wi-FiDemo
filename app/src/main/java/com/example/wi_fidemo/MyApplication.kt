package com.example.wi_fidemo

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication:Application(),Application.ActivityLifecycleCallbacks{

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        registerActivityLifecycleCallbacks(this)
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        //Log.d("data_information", "Call onActivityCreated")
    }

    override fun onActivityStarted(activity: Activity) {
     Log.d("Activity", "Call onActivityStarted ${activity.javaClass.simpleName}")
    }

    override fun onActivityResumed(activity: Activity) {
        //Log.d("data_information", "Call onActivityResumed")
        }

    override fun onActivityPaused(activity: Activity) {
        //Log.d("data_information", "Call onActivityPaused")
        }

    override fun onActivityStopped(activity: Activity) {
        //Log.d("data_information", "Call onActivityStopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        //Log.d("data_information", "Call onActivitySaveInstanceState")

    }

    override fun onActivityDestroyed(activity: Activity) {
        //Log.d("data_information", "Call onActivityDestroyed")

    }

}