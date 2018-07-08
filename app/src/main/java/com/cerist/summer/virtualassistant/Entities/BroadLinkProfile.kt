package com.cerist.summer.virtualassistant.Entities

import java.util.*

class BroadLinkProfile{
    companion object {
        val DEVICE_NAME= ""
        val DEVICE_MAC_ADDRESS ="B8:27:EB:AA:D6:51"
    }

    class AirConditionerProfile{

        companion object {

            const val DEVICE_SERVICE_UUID =  ""
            const val STATE_CHARACTERISTIC_UUID = ""
            const val MODE_CHARACTERISTIC_UUID = ""
            const val TEMPERATURE_CHARACTERISTIC_UUID = ""
            const val MAX_TEMP = 30
            const val MIN_TEMP = 17
        }

        enum class State(val value: Int) {
            ON(1),
            OFF(0)
        }
        enum class Mode(val value: Int) {
            COOL(3),
            FUN(2),
            ENERGY_SAVER(1),
            SLEEP(0)
        }
    }

    class TvProfile{
        companion object {
            const val DEVICE_SERVICE_UUID= ""
            const val STATE_CHARACTERISTIC_UUID = ""
            const val VOLUME_CHARACTERISTIC_UUID = ""
            const val MAX_VOLUME = 99
            const val MIN_VOLUME = 0
        }
        enum class State(val value: Int) {
            ON(1),
            OFF(2)
        }
    }
}