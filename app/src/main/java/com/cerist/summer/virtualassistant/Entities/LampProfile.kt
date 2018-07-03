package com.cerist.summer.virtualassistant.Entities

import java.util.*

class LampProfile{

    companion object {
         const val DEVICE_MAC_ADDRESS:String = "E1:A8:5D:0E:86:10"
         const val DEVICE_NAME = "name"
         val DEVICE_SERVICE_UUID = UUID.fromString("")
         val LUMINOSITY_CHARACTERISTIC_UUID =  UUID.fromString("")
         val STATE_CHARACTERISTIC_UUID =  UUID.fromString("000BEEF-1212-EFDE-1523-785FEABCD123")
    }

    enum class LAMP_STATE(s: String) {
        ON("ON"),
        OFF("ON")
    }

    enum class LAMP_LUMINOSITY(s:String) {
        NON("NON"),
        LOW("LOW"),
        MEDIUM("MEDIUM"),
        HIGH("HIGH"),
        MAX("MAX")
    }


}