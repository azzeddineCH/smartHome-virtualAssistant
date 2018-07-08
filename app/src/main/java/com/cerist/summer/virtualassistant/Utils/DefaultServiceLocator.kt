package com.cerist.summer.virtualassistant.Utils

import android.support.v4.app.FragmentActivity
import android.util.Log
import com.cerist.summer.virtualassistant.Entities.BroadLinkProfile
import com.cerist.summer.virtualassistant.Entities.LampProfile
import com.cerist.summer.virtualassistant.Repositories.AirConditionarRepository
import com.cerist.summer.virtualassistant.Repositories.IRepository
import com.cerist.summer.virtualassistant.Repositories.LampRepository
import com.cerist.summer.virtualassistant.Repositories.TvRepository
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanSettings
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

class DefaultServiceLocator (val activity: FragmentActivity): ServiceLocator {

    companion object {
        val TAG = "DefaultServiceLocator"
    }


    var blueToothClient = RxBleClient.create(activity)
    var rxPermissions = RxPermissions(activity)

    private val BLUETOOTH_IO = Executors.newFixedThreadPool(2)
    private val NETWORK_IO = Executors.newFixedThreadPool(2)
    private var lampBleDevice: Observable<RxBleDevice>
    private var broadLinkBleDevice: Observable<RxBleDevice>

    init {

        val permissionsCheck = blueToothClient.observeStateChanges()
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
                .filter { it }

        lampBleDevice = permissionsCheck
                .observeOn(Schedulers.from(getBlueToothExecutor()))
                .flatMap {
                    blueToothClient.scanBleDevices(
                        ScanSettings.Builder()
                                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                .build(),
                        ScanFilter.Builder()
                                .setDeviceAddress(LampProfile.DEVICE_MAC_ADDRESS)
                                .build())
                }
                .toFlowable(BackpressureStrategy.LATEST)
                .toObservable()
                .take(1)
                .flatMap { result ->
                    Log.d(TAG,"the device named ${result.bleDevice.name} is found")
                    Observable.just(result.bleDevice)
                }

        broadLinkBleDevice  = permissionsCheck
                .observeOn(Schedulers.from(getBlueToothExecutor()))
                .flatMap {
                    blueToothClient.scanBleDevices(
                            ScanSettings.Builder()
                                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                    .build(),
                            ScanFilter.Builder()
                                    .setDeviceAddress(BroadLinkProfile.DEVICE_MAC_ADDRESS)
                                    .build())
                }
                .toFlowable(BackpressureStrategy.LATEST)
                .toObservable()
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

            Repositories.TV_REPOSITORY -> TvRepository(
                    broadLink = broadLinkBleDevice,
                    bluetoothExecutor = getBlueToothExecutor()

            )
            Repositories.AIR_CONDITIONER_REPOSITORY -> AirConditionarRepository(
                    broadLink = broadLinkBleDevice,
                    bluetoothExecutor = getBlueToothExecutor()
            )
        }
    }
}