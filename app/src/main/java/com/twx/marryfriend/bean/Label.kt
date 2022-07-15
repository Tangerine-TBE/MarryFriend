package com.twx.marryfriend.bean

data class Label(val icon:Int, val label: String){
    companion object{
        fun String.toLabel(icon: Int): Label?{
            return if (this.isBlank()) {
                null
            }else{
                Label(icon,this)
            }
        }
    }
}