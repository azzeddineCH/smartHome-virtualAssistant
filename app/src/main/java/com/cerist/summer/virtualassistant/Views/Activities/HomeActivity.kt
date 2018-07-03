package com.cerist.summer.virtualassistant.Views.Activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Extentions.getViewModel
import com.cerist.summer.virtualassistant.R
import com.cerist.summer.virtualassistant.Utils.BleDevices
import com.cerist.summer.virtualassistant.Utils.Repositories
import com.cerist.summer.virtualassistant.Utils.ServiceLocator
import com.cerist.summer.virtualassistant.ViewModels.LampViewModel
import com.polidea.rxandroidble2.RxBleConnection

class HomeActivity:AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

       val model :LampViewModel =  getViewModel(this,Repositories.LAMP_REPOSITORY) as LampViewModel



        model.lampBleConnectionState?.observe(this, Observer<RxBleConnection.RxBleConnectionState>{
           Log.d("HomeActivity","connection ${it?.name}")
        })


        model.lampBleLightningState?.observe(this, Observer<LampProfile.LAMP_STATE>{
            Log.d("HomeActivity","state ${it?.name}")
        })

    }
}