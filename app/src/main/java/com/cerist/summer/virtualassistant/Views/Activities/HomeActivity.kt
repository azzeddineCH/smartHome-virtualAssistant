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
import com.cerist.summer.virtualassistant.Utils.Resource
import com.cerist.summer.virtualassistant.Utils.Status
import com.cerist.summer.virtualassistant.ViewModels.LampViewModel
import com.cerist.summer.virtualassistant.ViewModels.TvViewModel
import com.polidea.rxandroidble2.RxBleConnection

class HomeActivity:AppCompatActivity(){
    companion object {
        val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        Log.d(TAG,"HERE WE ARE !! ")

        val model :LampViewModel =  getViewModel(this,Repositories.LAMP_REPOSITORY) as LampViewModel
        val moreModel:TvViewModel  = getViewModel(this,Repositories.TV_REPOSITORY) as TvViewModel

        model.lampBleConnectionState.observe(this, Observer<Resource<RxBleConnection.RxBleConnectionState>>{
            if (it?.status == Status.SUCCESS)
                     Log.d(TAG,"connection ${it.data}")
            else
                Log.d(TAG,"connection error ${it?.message}")
        })

        model.getLampLightningState().observe(this, Observer<Resource<LampProfile.LAMP_STATE>>{
            if (it?.status == Status.SUCCESS)
                Log.d(TAG,"connection ${it.data}")
            else
                Log.d(TAG,"connection error ${it?.message}")
        })

        model.getLampLuminosityLevel().observe(this, Observer<Resource<LampProfile.LAMP_LUMINOSITY>>{
            if (it?.status == Status.SUCCESS)
                Log.d(TAG,"connection ${it.data}")
            else
                Log.d(TAG,"connection error ${it?.message}")
        })


        model.setLampLightningState(LampProfile.LAMP_STATE.OFF).observe(this, Observer<Resource<LampProfile.LAMP_STATE>>{
            if (it?.status == Status.SUCCESS)
                Log.d(TAG,"connection ${it.data}")
            else
                Log.d(TAG,"connection error ${it?.message}")
        })


        moreModel.tvBleConnectionState.observe(this, Observer<Resource<RxBleConnection.RxBleConnectionState>>{
            if (it?.status == Status.SUCCESS)
                Log.d(TAG,"connection ${it.data}")
            else
                Log.d(TAG,"connection error ${it?.message}")
        })
/*
       moreModel.tvPowerState.observe(this, Observer<Resource<BroadLinkProfile.TvProfile.State>>{
            if (it?.status == Status.SUCCESS)
                Log.d(TAG,"connection ${it.data}")
            else
                Log.d(TAG,"connection error ${it?.message}")
        })

        moreModel.tvVolumeLevel.observe(this, Observer<Resource<Int>>{
            if (it?.status == Status.SUCCESS)
                Log.d(TAG,"connection ${it.data}")
            else
                Log.d(TAG,"connection error ${it?.message}")
        })

*/

    }
}