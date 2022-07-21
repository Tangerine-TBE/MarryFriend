package com.twx.marryfriend.enumeration

enum class MarriageEnum(val title:String,val code:Int) {
    unlimited("不限",0),unmarried("未婚",1),divorce("离异",2),widowhood("丧偶",3);
    companion object{
        fun changeChoice(marriageEnum: MarriageEnum,marriageState: MutableList<MarriageEnum>,isChoice:Boolean){
            if (isChoice){
                if (marriageEnum==MarriageEnum.unlimited){
                    marriageState.clear()
                    marriageState.add(MarriageEnum.unlimited)
                }else{
                    marriageState.add(marriageEnum)
                    if (MarriageEnum.values().all {
                            marriageState.contains(it)||it==MarriageEnum.unlimited
                        }){
                        marriageState.clear()
                        marriageState.add(MarriageEnum.unlimited)
                    }else{
                        marriageState.remove(MarriageEnum.unlimited)
                    }
                }
            }else{
                if(marriageEnum==MarriageEnum.unlimited){
                    return
                }else {
                    marriageState.remove(marriageEnum)
                    if (marriageState.isEmpty()){
                        marriageState.add(MarriageEnum.unlimited)
                    }
                }
            }
        }
    }
}