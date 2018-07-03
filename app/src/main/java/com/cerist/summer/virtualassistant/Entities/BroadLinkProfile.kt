package com.cerist.summer.virtualassistant.Entities

import java.util.*

class BroadLinkProfile{
    companion object {
        val DEVICE_NAME= ""
        val DEVICE_MAC_ADDRESS ="B8:27:EB:AA:D6:51"
    }

    class AirConditionarProfile{
        val DEVICE_SERVICE_UUID = UUID.fromString("")

        val STATE_CHARACTERISTIC_UUID = UUID.fromString("")
        val MODE_CHARACTERISTIC_UUID = UUID.fromString("")
        val TEMPERATURE_CHARACTERISTIC_UUID = UUID.fromString("")
    }

    class TvProfile{
        val DEVICE_SERVICE_UUID= UUID.fromString("")
        val STATE_CHARACTERISTIC_UUID = UUID.fromString("")
        val VOLUME_CHARACTERISTIC_UUID = UUID.fromString("")
    }
}