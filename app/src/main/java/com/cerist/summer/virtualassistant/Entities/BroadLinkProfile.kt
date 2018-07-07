package com.cerist.summer.virtualassistant.Entities

import java.util.*

class BroadLinkProfile{
    companion object {
        val DEVICE_NAME= ""
        val DEVICE_MAC_ADDRESS ="B8:27:EB:AA:D6:51"
    }

    class AirConditionarProfile{

        companion object {

         val DEVICE_SERVICE_UUID =  ""

        val STATE_CHARACTERISTIC_UUID = ""
        val MODE_CHARACTERISTIC_UUID = ""
        val TEMPERATURE_CHARACTERISTIC_UUID = ""
        }

        enum class AIR_CONDITIONAR_STATE(s: String) {
            ON("ON"),
            OFF("OFF")
        }

        enum class AIR_CONDITIONAR_MODE(s: String) {
            AUTO("ON"),
            DRY("DRY"),
            HEAT("HEAT")
        }
    }

    class TvProfile{
        companion object {
        val DEVICE_SERVICE_UUID= ""
        val STATE_CHARACTERISTIC_UUID = ""
        val VOLUME_CHARACTERISTIC_UUID = ""
            val maxVolume = 100
            val minVolume = 0
        }

        enum class TV_STATE(s: String) {
            ON("ON"),
            OFF("OFF")
        }
    }
}