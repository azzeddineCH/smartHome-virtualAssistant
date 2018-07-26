package com.cerist.summer.virtualassistant.Entities

import java.util.*

class BroadLinkProfile{

    companion object {
        val DEVICE_MAC_ADDRESS ="B8:27:EB:AA:D6:51"
    }

    class AirConditionerProfile{

        companion object
        {

            const val STATE_CHARACTERISTIC_UUID = "44fac9e0-c111-11e3-9246-0002a5d5c51b"
            const val MODE_CHARACTERISTIC_UUID = "43fac9e0-c111-11e3-9246-0002a5d5c51b"
            const val TEMPERATURE_UP_CHARACTERISTIC_UUID = "41fac9e0-c111-11e3-9246-0002a5d5c51b"
            const val TEMPERATURE_DOWN_CHARACTERISTIC_UUID = "42fac9e0-c111-11e3-9246-0002a5d5c51b"
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
            const val STATE_CHARACTERISTIC_UUID = "53fac9e0-c111-11e3-9246-0002a5d5c51b"
            const val VOLUME_UP_CHARACTERISTIC_UUID = "51fac9e0-c111-11e3-9246-0002a5d5c51b"
            const val VOLUME_DOWN_CHARACTERISTIC_UUID = "52fac9e0-c111-11e3-9246-0002a5d5c51b"
            const val VOLUME_CHARACTERISTIC_UUID = "54fac9e0-c111-11e3-9246-0002a5d5c51b"
            const val TIMER_CHARACTERISTIC_UUID = "55fac9e0-c111-11e3-9246-0002a5d5c51b"
            const val MAX_VOLUME = 99
            const val MIN_VOLUME = 0
            val TV_TIMER_SET = arrayOf(0,15,30,60,90)
        }
        enum class State(val value: Int) {
            ON(1),
            OFF(0)
        }
    }
}