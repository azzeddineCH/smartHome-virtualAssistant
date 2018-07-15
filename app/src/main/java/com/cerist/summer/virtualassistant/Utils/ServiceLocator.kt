package com.cerist.summer.virtualassistant.Utils

import android.support.v4.app.FragmentActivity
import com.cerist.summer.virtualassistant.Repositories.IRepository
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import java.util.concurrent.Executor

enum class BleDevices{
    BROAD_LINK,
    LAMP
}

enum class Repositories{
    LAMP_REPOSITORY,
    TV_REPOSITORY,
    AIR_CONDITIONER_REPOSITORY,
    DIALOG_REPOSITORY
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

