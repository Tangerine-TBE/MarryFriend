package com.xyzz.myutils

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.text.TextUtils
import android.util.Base64
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.HashMap

class SPUtil private constructor(){
    companion object{
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            SPUtil().also {
                it.sharedPreferences= MyUtils.application.getSharedPreferences("my_util_sp",Context.MODE_PRIVATE)
                it.editor=it.sharedPreferences.edit()
            }
        }
    }
    
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor:SharedPreferences.Editor

    fun getBoolean(key: String, defaultVal: Boolean): Boolean {
        return this.sharedPreferences.getBoolean(key, defaultVal)
    }

    fun getBoolean(key: String): Boolean {
        return this.sharedPreferences.getBoolean(key, false)
    }


    fun getString(key: String, defaultVal: String?): String? {
        return this.sharedPreferences.getString(key, defaultVal)
    }

    fun getString(key: String): String? {
        return this.sharedPreferences.getString(key, null)
    }

    fun getInt(key: String, defaultVal: Int): Int {
        return this.sharedPreferences.getInt(key, defaultVal)
    }

    fun getInt(key: String): Int {
        return this.sharedPreferences.getInt(key, -1)
    }


    fun getFloat(key: String, defaultVal: Float): Float {
        return this.sharedPreferences.getFloat(key, defaultVal)
    }

    fun getFloat(key: String): Float {
        return this.sharedPreferences.getFloat(key, 0f)
    }

    fun getLong(key: String, defaultVal: Long): Long {
        return this.sharedPreferences.getLong(key, defaultVal)
    }

    fun getLong(key: String): Long {
        return this.sharedPreferences.getLong(key, 0L)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun getStringSet(key: String, defaultVal: Set<String>): Set<String>? {
        return this.sharedPreferences.getStringSet(key, defaultVal)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun getStringSet(key: String): Set<String>? {
        return this.sharedPreferences.getStringSet(key, null)
    }

    fun getAll(): Map<String, *> {
        return this.sharedPreferences.all
    }

    fun exists(key: String): Boolean {
        return sharedPreferences.contains(key)
    }


    fun putString(key: String, value: String): SPUtil {
        editor.putString(key, value)
        editor.commit()
        return this
    }


    fun putInt(key: String, value: Int): SPUtil {
        editor.putInt(key, value)
        editor.commit()
        return this
    }

    fun putFloat(key: String, value: Float): SPUtil {
        editor.putFloat(key, value)
        editor.commit()
        return this
    }

    fun putLong(key: String, value: Long): SPUtil {
        editor.putLong(key, value)
        editor.commit()
        return this
    }

    fun putBoolean(key: String, value: Boolean): SPUtil {
        editor.putBoolean(key, value)
        editor.commit()
        return this
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun putStringSet(key: String, value: Set<String>): SPUtil {
        editor.putStringSet(key, value)
        editor.commit()
        return this
    }

    fun putMap(key: String, map:Map<String, Int>): SPUtil {
        val `object` = JSONObject(map)
        val value = `object`.toString()
        editor.putString(key, value)
        editor.commit()
        return this

    }

    fun getMap(key: String): HashMap<String, Int> {
        val map = HashMap<String, Int>()
        val value = sharedPreferences.getString(key, "")?:return map
        if (!TextUtils.isEmpty(value)) {
            try {
                val jsonObject = JSONObject(value)
                val stringIterator = jsonObject.keys()
                while (stringIterator.hasNext()) {
                    val mapKey = stringIterator.next()
                    map[mapKey] = jsonObject.getInt(mapKey)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
        return map

    }

    fun putObject(key: String, `object`: Any) {
        val baos = ByteArrayOutputStream()
        var out: ObjectOutputStream? = null
        try {
            out = ObjectOutputStream(baos)
            out.writeObject(`object`)
            val objectVal = String(Base64.encode(baos.toByteArray(), Base64.DEFAULT))
            editor.putString(key, objectVal)
            editor.commit()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                baos.close()
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun <T> getObject(key: String, clazz: Class<T>): T? {
        if (sharedPreferences.contains(key)) {
            val objectVal = sharedPreferences.getString(key, null)
            val buffer = Base64.decode(objectVal, Base64.DEFAULT)
            val bais = ByteArrayInputStream(buffer)
            var ois: ObjectInputStream? = null
            try {
                ois = ObjectInputStream(bais)
                return ois.readObject() as T
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    bais.close()
                    ois?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return null
    }

    fun remove(key: String): SPUtil {
        editor.remove(key)
        editor.commit()
        return this
    }

    fun removeAll(): SPUtil {
        editor.clear()
        editor.commit()
        return this
    }
}