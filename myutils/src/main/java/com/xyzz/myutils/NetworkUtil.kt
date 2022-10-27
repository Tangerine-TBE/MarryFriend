package com.xyzz.myutils

import android.content.Intent
import androidx.core.content.FileProvider
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.wLog
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object NetworkUtil {
    private val volley by lazy { Volley.newRequestQueue(MyUtils.application) }
    private val errorLogDir by lazy {
        File(MyUtils.application.externalCacheDir,"network_error").also {
            if (!it.exists()){
                it.mkdirs()
            }
        }
    }
    private fun generateHtmlFile():File{
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd_HH_mm_ss_SSSS", Locale.CHINA)
        return File(errorLogDir,simpleDateFormat.format(System.currentTimeMillis())+"_error_log.html")
    }
    private val simpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    }
    private val responseListener by lazy {
        ArrayList<(String)->Unit>()
    }
    fun addResponseListener(a:(String)->Unit){
        responseListener.add(a)
    }
    fun sendPostSecret(url: String, parameter:Map<String,String>, success: (String) -> Unit, fail: (msg: String) -> Unit, notEncryptionParameter:Map<String,String>?=null): StringRequest {
        iLog("接口:"+url+" 加密参数:"+parameter.toString()+" 未加密参数:"+notEncryptionParameter.toString(),"网络,接口")
        val request=object : StringRequest(Method.POST, url, { response ->
            iLog("${response}","网络,响应,${url.split("/").lastOrNull()?:url.takeLast(10)}")
            //456,被封禁
            success.invoke(response)
            responseListener.forEach {
                it.invoke(response)
            }
        }, { error ->
            wLog("${error.networkResponse?.statusCode?.toString()?:""}","网络,响应,${url.split("/").lastOrNull()?:url.takeLast(10)}")
            fail.invoke(error.networkResponse?.statusCode.toString())
            try {
                generateHtmlFile().also { file ->
                    file.outputStream().use {
                        it.write("<!--${simpleDateFormat.format(System.currentTimeMillis())}\n".toByteArray())
                        it.write("接口:${url} 加密参数:${parameter} 未加密参数:${notEncryptionParameter.toString()}\n -->".toByteArray())
                        it.write(error.networkResponse.data)
                    }
                    if (BuildConfig.DEBUG){
                        val intent=Intent(Intent.ACTION_VIEW)
                        intent.data =
                            FileProvider.getUriForFile(MyUtils.application,MyUtils.authorities,file)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        MyUtils.application.startActivity(intent)
                    }
                }
            }catch (e:Exception){
                wLog(e.stackTraceToString())
            }
        }){
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                val t=(System.currentTimeMillis()/1000).toString()
                parameter.forEach {
                    params[it.key]= it.value
                }
                params["timestamp"]=t
                params["signature"]= getSignature(url, parameter,t)

                /**
                 * 不加密的参数
                 */
                notEncryptionParameter?.forEach {
                    params[it.key]= it.value
                }
                return params
            }
        }
        volley.add(request)
        return request
    }

    fun sendPost(url: String, parameter:Map<String,Any>, success: (String) -> Unit, fail: (msg: String) -> Unit): StringRequest {
        val request=object : StringRequest(Method.POST, url, { response ->
            iLog(response,"网络,响应")
            success.invoke(response)
            responseListener.forEach {
                it.invoke(response)
            }
        }, {
            eLog(it.networkResponse?.statusCode?.toString()?:"","网络,响应失败")
            fail.invoke(it.message?:"null")
        }){
            override fun getParams(): MutableMap<String, String> {
                val params: MutableMap<String, String> = HashMap()
                parameter.forEach {
                    params[it.key]=it.value.toString()
                }
                return params
            }
        }
        volley.add(request)
        return request
    }

    private fun getSignature(url: String, parameter:Map<String,String>, t:String):String{
        val stringBuilder=StringBuilder()
        parameter.keys.sorted().forEach {
            stringBuilder.append(parameter[it])
        }
        val d=stringBuilder.toString()
        val timestamp=t
        val token="x389fh^feahykge"
        val nonce=523146
        val service=url.substringAfter("/").substringAfter("?").split("/").let { list ->
            if (list.size>=2){
                val sb=StringBuilder()
                list.takeLast(2).forEach {
                    sb.append(it)
                    sb.append("/")
                }
                sb.removeSuffix("/")
                sb.toString()
            }else{
                ""
            }.removeSuffix("/")
        }
        val signature=(token+timestamp+nonce+service+d).toMd5()

        return signature
    }

}