package com.cerist.summer.virtualassistant.Utils

import android.app.Activity
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
        fun instance(context: Activity):ServiceLocator{
            if(instance == null) instance = DefaultServiceLocator(context)
            return instance!!
        }
    }

    fun getBlueToothExecutor():Executor
    fun getNetworkExecutor():Executor
    fun getBleDevice(key:BleDevices):Observable<RxBleDevice>
    fun getRepository(key:Repositories):IRepository
}

    class DefaultServiceLocator (val activity:Activity): ServiceLocator {
    companion object {
        val TAG = "DefaultServiceLocator"
    }


    var blueToothClient = RxBleClient.create(activity)
    var rxPermissions = RxPermissions(activity)

    private val BLUETOOTH_IO = Executors.newFixedThreadPool(2)
    private val NETWORK_IO = Executors.newFixedThreadPool(2)
    private var lampBleDevice: Observable<RxBleDevice>
    private var broadLinkBleDevice: Observable<RxBleDevice> ?= null

    init {

        lampBleDevice =  blueToothClient.observeStateChanges()
                .switchMap {
                    when(it){
                        RxBleClient.State.READY->
                                Observable.just(true)
                        RxBleClient.State.LOCATION_PERMISSION_NOT_GRANTED ->
                            requestLocationPermissions(activity = activity)
                        RxBleClient.State.BLUETOOTH_NOT_ENABLED ->
                            Observable.just(false)
                        RxBleClient.State.LOCATION_SERVICES_NOT_ENABLED ->
                            Observable.just(false)
                        else -> Observable.just(false)
                    }}
                .filter { it == true }
                .flatMap {    blueToothClient.scanBleDevices(
                        ScanSettings.Builder()
                                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                .build(),
                        ScanFilter.Builder()
                                .setDeviceAddress(LampProfile.DEVICE_MAC_ADDRESS)
                                .build())}
                .toFlowable(BackpressureStrategy.LATEST)
                .toObservable()
                .observeOn(Schedulers.from(getBlueToothExecutor()))
                .take(1)
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