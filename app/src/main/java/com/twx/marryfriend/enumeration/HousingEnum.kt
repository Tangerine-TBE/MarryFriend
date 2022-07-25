package com.twx.marryfriend.enumeration

enum class HousingEnum(val title:String,val code:Int) {
    unlimited("不限",0),
    withFamily("和家人同住",1),purchasedHouse("已购房",2),
    rentHouse("租房",3),marryBuyHouse("打算婚后购房",4),
    dormitory("住在单位宿舍",5);
    companion object{
        fun changeChoice(enum: HousingEnum, list: MutableList<HousingEnum>, isChoice:Boolean){
            if (isChoice){
                if (enum==HousingEnum.unlimited){
                    list.clear()
                    list.add(HousingEnum.unlimited)
                }else{
                    list.add(enum)
                    if (HousingEnum.values().all {
                            list.contains(it)||it==HousingEnum.unlimited
                        }){
                        list.clear()
                        list.add(HousingEnum.unlimited)
                    }else{
                        list.remove(HousingEnum.unlimited)
                    }
                }
            }else{
                if(enum==HousingEnum.unlimited){
                    return
                }else {
                    list.remove(enum)
                    if (list.isEmpty()){
                        list.add(HousingEnum.unlimited)
                    }
                }
            }
        }
    }
}