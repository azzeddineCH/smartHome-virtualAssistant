package com.cerist.summer.virtualassistant.Entities

import java.util.*

class LampProfile{

    companion object {
         const val DEVICE_MAC_ADDRESS:String = "E1:A8:5D:0E:86:10"


        const val LUMINOSITY_CHARACTERISTIC_UUID =  ""
        const val STATE_CHARACTERISTIC_UUID =  "0000beef-1212-EFDE-1523-785FEABCD123"
    }

    enum class LAMP_STATE(s: String) {
        ON("ON"),
        OFF("OFF")
    }

    enum class LAMP_LUMINOSITY(s:String) {
        NON("NON"),
        LOW("LOW"),
        MEDIUM("MEDIUM"),
        HIGH("HIGH"),
        MAX("MAX")
    }


}