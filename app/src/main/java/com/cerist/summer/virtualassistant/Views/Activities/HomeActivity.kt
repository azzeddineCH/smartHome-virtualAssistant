package com.cerist.summer.virtualassistant.Views.Activities

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Utils.getViewModel
import com.cerist.summer.virtualassistant.R
import com.cerist.summer.virtualassistant.Utils.Repositories
import com.cerist.summer.virtualassistant.ViewModels.AirConditionerViewModel
import com.cerist.summer.virtualassistant.ViewModels.LampViewModel
import com.cerist.summer.virtualassistant.ViewModels.TvViewModel
import kotlinx.android.synthetic.main.home_activity.*

class HomeActivity:AppCompatActivity(){
    companion object {
        val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)




        val lampViewModel  =  getViewModel(this,Repositories.LAMP_REPOSITORY) as LampViewModel
        val tvViewModel  =  getViewModel(this,Repositories.TV_REPOSITORY) as TvViewModel
        val airConditionerViewModel  =  getViewModel(this,Repositories.AIR_CONDITIONER_REPOSITORY) as AirConditionerViewModel



        tvViewModel.getTvConnectionState().observe(this, Observer {
            Log.d(TAG,"tv connection ${it?.name} ")
        })

        airConditionerViewModel.getAirConditionerConnectionState().observe(this, Observer {
            Log.d(TAG,"air conditioner connection ${it?.name} ")
        })

        lampViewModel.getLampConnectionState().observe(this, Observer{
          Log.d(TAG,"lamp connection ${it?.name}")
        })




    }
}