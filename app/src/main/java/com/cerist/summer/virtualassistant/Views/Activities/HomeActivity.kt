package com.cerist.summer.virtualassistant.Views.Activities

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.cerist.summer.virtualassistant.Utils.getViewModel
import com.cerist.summer.virtualassistant.R
import com.cerist.summer.virtualassistant.Utils.Repositories
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


    }
}