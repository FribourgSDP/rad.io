package com.github.fribourgsdp.radio

import org.json.JSONException
import org.json.JSONObject

abstract class JSONParser{
    abstract fun parse(s: String?) : JSONObject?
}

class JSONStandardParser : JSONParser() {
    override fun parse(s : String?) : JSONObject? {
        val out : JSONObject? = try{
            s?.let { JSONObject(it) }
        } catch (e : JSONException){
            null
        }
        return out
    }
}
