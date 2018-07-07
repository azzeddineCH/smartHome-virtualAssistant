package com.cerist.summer.virtualassistant.Utils

import android.app.Activity
import android.support.v4.app.FragmentActivity
import  com.polidea.rxandroidble2.scan.ScanSettings
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Repositories.BroadLinkRepository
import com.cerist.summer.virtualassistant.Repositories.IRepository
import com.cerist.summer.virtualassistant.Repositories.LampRepository
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.scan.ScanFilter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor
import java.util.concurrent.Executors

enum class BleDevices{
    BROAD_LINK,
    LAMP
}

enum class Repositories{
    LAMP_REPOSITORY,
    BROAD_LINK_REPOSITORY,
}

interface ServiceLocator{
    companion object {
        val TAG = "ServiceLocator"


        private var instance:ServiceLocator?=null
        fun instance(context: FragmentActivity):ServiceLocator{
            if(instance == null) instance = DefaultServiceLocator(context)
            return instance!!
        }
    }

    fun getBlueToothExecutor():Executor
    fun getNetworkExecutor():Executor
    fun getBleDevice(key:BleDevices):Observable<RxBleDevice>
    fun getRepository(key:Repositories):IRepository
}

