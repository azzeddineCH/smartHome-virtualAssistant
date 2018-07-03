package com.cerist.summer.virtualassistant.Utils

import  com.polidea.rxandroidble2.scan.ScanSettings
import android.content.Context
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Repositories.BroadLinkRepository
import com.cerist.summer.virtualassistant.Repositories.IRepository
import com.cerist.summer.virtualassistant.Repositories.LampRepository
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.scan.ScanFilter
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.log

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
        private var instance:ServiceLocator?=null
        public fun instance(context: Context):ServiceLocator{
            if(instance == null) instance = DefaultServiceLocator(context)
            return instance!!
        }
    }

    fun getBlueToothExecutor():Executor
    fun getNetworkExecutor():Executor
    fun getBleDevice(key:BleDevices):Observable<RxBleDevice>
    fun getRepository(key:Repositories):IRepository
}

class DefaultServiceLocator (val context:Context): ServiceLocator {
    companion object {
        val TAG = "DefaultServiceLocator"
    }
    var blueToothClient: RxBleClient
    private val BLUETOOTH_IO = Executors.newFixedThreadPool(2)
    private val NETWORK_IO = Executors.newFixedThreadPool(2)
    private var lampBleDevice: Observable<RxBleDevice>
    private var broadLinkBleDevice: Observable<RxBleDevice> ?= null

    init {
        blueToothClient = RxBleClient.create(context)

        lampBleDevice = blueToothClient.scanBleDevices(
                ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build(),
                ScanFilter.Builder()
                        .setDeviceAddress(LampProfile.DEVICE_MAC_ADDRESS)
                        .build()
        ).observeOn(Schedulers.from(getBlueToothExecutor()))
                .firstElement()
                .toObservable()
                .flatMap { result ->
                    Log.d(TAG,"the device named ${result.bleDevice.name} is found")
                    Observable.just(result.bleDevice)
                }


    }


    override fun getBlueToothExecutor() = BLUETOOTH_IO

    override fun getNetworkExecutor() = NETWORK_IO

    override fun getBleDevice(key: BleDevices): Observable<RxBleDevice> {

        return when (key) {
            BleDevices.LAMP -> lampBleDevice
            BleDevices.BROAD_LINK -> lampBleDevice
        }
    }

    override fun getRepository(key: Repositories): IRepository {
        return when (key) {
            Repositories.LAMP_REPOSITORY -> LampRepository(
                    lampBleDevice = getBleDevice(BleDevices.LAMP),
                    bluetoothExecutor = getBlueToothExecutor()
            )

            Repositories.BROAD_LINK_REPOSITORY -> BroadLinkRepository()
        }
    }
}