package com.twx.marryfriend.enumeration

/**
 * 星座
 */
enum class ConstellationEnum(val title:String,val code:Int) {
    unlimited("不限",0),
    Aries("白羊座",1),Taurus("金牛座",2),Gemini("双子座",3),
    cancer("巨蟹座",4),Leo("狮子座",5),Virgo("处女座",6),
    Libra("天秤座",7),Scorpio("天蝎座",8),Sagittarius("射手座",9),
    Capricorn("摩羯座",10),Aquarius("水瓶座",11),Pisces("双鱼座",12);
    companion object{
        fun findConstellation(code: Int?):ConstellationEnum?{
            return ConstellationEnum.values().find {
                it.code==code
            }
        }
    }
    /*companion object{
        fun changeChoice(enum: ConstellationEnum, list: MutableList<ConstellationEnum>, isChoice:Boolean){
            if (isChoice){
                if (enum==ConstellationEnum.unlimited){
                    list.clear()
                    list.add(ConstellationEnum.unlimited)
                }else{
                    list.add(enum)
                    if (ConstellationEnum.values().all {
                            list.contains(it)||it==ConstellationEnum.unlimited
                        }){
                        list.clear()
                        list.add(ConstellationEnum.unlimited)
                    }else{
                        list.remove(ConstellationEnum.unlimited)
                    }
                }
            }else{
                if(enum==ConstellationEnum.unlimited){
                    return
                }else {
                    list.remove(enum)
                    if (list.isEmpty()){
                        list.add(ConstellationEnum.unlimited)
                    }
                }
            }
        }
    }*/
}