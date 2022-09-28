package com.message.chat

const val HELPER_STYLE_1="helper_style_1"
enum class CustomEvent(val title:String, val code:String,val category:String?=null){
    flower("送花","flower"),
    security("安全提示","security"),
    openSuperVip("开通vip","openvip"),
    upload_head("上传头像","uploadhead"),

    dazhaohu_str("填写打招呼","dazhaohu_str",HELPER_STYLE_1),
    putong_xihuan("普通喜欢","putong_xihuan",HELPER_STYLE_1),
    touxiang_pass("头像通过","touxiang_pass",HELPER_STYLE_1),
    touxiang_fail("头像失败","touxiang_fail",HELPER_STYLE_1),
    yuying_pass("语音通过","yuying_pass",HELPER_STYLE_1),
    yuying_fail("语音失败","yuying_fail",HELPER_STYLE_1),
    shiming_pass("实名通过","shiming_pass",HELPER_STYLE_1),
    shiming_fail("实名失败","shiming_fail",HELPER_STYLE_1),
    xiangce_pass("相册通过","xiangce_pass",HELPER_STYLE_1),
    xiangce_fail("相册失败","xiangce_fail",HELPER_STYLE_1),
    shenghuo_pass("生活照通过","shenghuo_pass",HELPER_STYLE_1),
    shenghuo_fail("生活照失败","shenghuo_fail",HELPER_STYLE_1),
    dongtai_pass("动态通过","dongtai_pass",HELPER_STYLE_1),
    dongtai_fail("动态失败","dongtai_fail",HELPER_STYLE_1),
    jubao_pass("举报通过","jubao_pass",HELPER_STYLE_1),
    jubao_fail("举报失败","jubao_fail",HELPER_STYLE_1),


    HELPER_VIP_EXPIRE("会员到期","VIP_EXPIRE");

    companion object{
        fun getTip(code: String):String?{
            return CustomEvent.values().find {
                it.code==code
            }?.title?.let {
                "[${it}]"
            }
        }
        fun codeToEvent(code:String?):CustomEvent?{
            return CustomEvent.values().find {
                it.code==code
            }
        }
    }
}