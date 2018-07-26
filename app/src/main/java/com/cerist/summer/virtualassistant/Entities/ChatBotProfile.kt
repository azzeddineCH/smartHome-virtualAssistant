package com.cerist.summer.virtualassistant.Entities

class ChatBotProfile{

    companion object {
        const val DEVICE_NAME_PARAMETER_KEY = "Device"
        const val DEVICE_STATE_PARAMETER_KEY = "DeviceState"
        const val DEVICE_BRIGHTNESS_PARAMETER_KEY = "DeviceLuminosity"
        const val DEVICE_MODE_PARAMETER_KEY = "AirMode"
        const val DEVICE_VOLUME_PARAMETER_KEY = "number"
        const val DEVICE_TIMER_PARAMETER_KEY = "number"


        const val DEVICE_SWITCH_SET_ACTION_KEY = "device.switch.set"
        const val DEVICE_SWITCH_CHECK_ACTION_KEY= "device.switch.check"

        const val DEVICE_BRIGHTNESS_SET_ACTION_KEY = "device.brightness.set"
        const val DEVICE_BRIGHTNESS_CHECK_ACTION_KEY = "device.brightness.check"

        const val DEVICE_MODE_SET_ACTION_KEY = "device.mode.set"
        const val DEVICE_MODE_CHECK_ACTION_KEY = "device.mode.check"

        const val DEVICE_VOLUME_SET_ACTION_KEY = "device.volume.set"
        const val DEVICE_VOLUME_CHECK_ACTION_KEY = "device.volume.check"

        const val DEVICE_TIMER_ENABLE_ACTION_KEY = "device.timer.enable"
        const val DEVICE_TIMER_DISABLE_ACTION_KEY = "device.timer.disable"
        const val DEVICE_TIMER_SET_ACTION_KEY = "device.timer.set"

        const val DEVICE_SWITCH_CONTEXT ="device-switch"



        fun parameterValueMapper(value:String)
                = value.toUpperCase().replace(" ","_")
    }


    enum class Device(val s:String) {
        TV("TV"),
        LAMP("lamp"),
        AIR_CONDITIONER("air conditioner")
    }

    enum class State(val s:String){
        ON("on"),
        OFF("off")
    }

    enum class Luminosity(val s:String){
        NON("Non"),
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        MAX("Max")
    }

    enum class AirMode(val s:String){
        SLEEP("Sleep"),
        ENERGY_SAVER("Energy saver"),
        FUN("Fun"),
        COOL("Cool")
    }
}