package com.cerist.summer.virtualassistant.Utils.Functions

import android.Manifest
import android.support.v4.app.FragmentActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable


fun requestBluetoothPermissions(activity: FragmentActivity): Observable<Boolean>{

    val rxPermissions = RxPermissions(activity)
        return  rxPermissions.request(Manifest.permission.BLUETOOTH_ADMIN,
                                Manifest.permission.BLUETOOTH
                                )
}

fun requestLocationPermissions(activity: FragmentActivity): Observable<Boolean> {
    val rxPermissions = RxPermissions(activity)
    return rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )
}