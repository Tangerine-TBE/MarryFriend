package com.twx.marryfriend.enumeration

enum class WantChildrenEnum(val title:String,val code:Int) {
    unlimited("不限",0),option1("没有孩子",1),
    option2("有孩子且住一起",2),option3("有孩子偶尔会住在一起",3),
    option4("有孩子但不在身边",4);
    companion object{
        fun changeChoice(enum: WantChildrenEnum, list: MutableList<WantChildrenEnum>, isChoice:Boolean){
            if (isChoice){
                if (enum==WantChildrenEnum.unlimited){
                    list.clear()
                    list.add(WantChildrenEnum.unlimited)
                }else{
                    list.add(enum)
                    if (WantChildrenEnum.values().all {
                            list.contains(it)||it==WantChildrenEnum.unlimited
                        }){
                        list.clear()
                        list.add(WantChildrenEnum.unlimited)
                    }else{
                        list.remove(WantChildrenEnum.unlimited)
                    }
                }
            }else{
                if(enum==WantChildrenEnum.unlimited){
                    return
                }else {
                    list.remove(enum)
                    if (list.isEmpty()){
                        list.add(WantChildrenEnum.unlimited)
                    }
                }
            }
        }
    }
}