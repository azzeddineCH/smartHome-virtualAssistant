package com.cerist.summer.virtualassistant.Entities

class ChatBotProfile{

    companion object {
        const val DEVICE_NAME_PARAMETER_KEY = "Device"
        const val DEVICE_STATE_PARAMETER_KEY = "DeviceState"
        const val DEVICE_BRIGHTNESS_PARAMETER_KEY = "DeviceLuminosity"


        const val DEVICE_SWITCH_SET_ACTION_KEY = "device.switch.set"
        const val DEVICE_SWITCH_CHECK_ACTION_KEY= "device.switch.check"

        const val DEVICE_BRIGHTNESS_SET_ACTION_KEY = "device.brightness.set"
        const val DEVICE_BRIGHTNESS_CHECK_ACTION_KEY = "device.brightness.check"

        const val DEVICE_MODE_SET_ACTION_KEY = "device.mode.set"
        const val DEVICE_MODE_CHECK_ACTION_KEY = "device.mode.check"

        const val DEVICE_VOLUME_SET_ACTION_KEY = "device.volume.set"
        const val DEVICE_VOLUME_CHECK_ACTION_KEY = "device.volume.check"


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