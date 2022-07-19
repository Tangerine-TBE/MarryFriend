package com.twx.marryfriend.search

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.City
import com.twx.marryfriend.bean.Province
import com.twx.marryfriend.bean.post.OccupationBean
import com.twx.marryfriend.bean.post.OccupationDataBean
import com.twx.marryfriend.dialog.*
import com.twx.marryfriend.enumeration.*
import com.twx.marryfriend.getCityData
import com.twx.marryfriend.getOccupationData
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.toast
import kotlinx.android.synthetic.main.activity_search.*

class SearchParamActivity:AppCompatActivity(R.layout.activity_search) {
    private val searchViewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }
    private val loadingDialog by lazy {
        LoadingDialogManager.createLoadingDialog().create(this)
    }

    private var ageRange:IntRange?=null
        set(value) {
            field=value
            searchViewModel.setAgeParameter(value)
            ageValue.text=if (value==null){
                "不限"
            }else{
                "${value.first}-${value.last}岁"
            }
        }
    private var heightRange:IntRange?=null
        set(value) {
            field=value
            searchViewModel.setHeightParameter(value)
            heightValue.text=if (value==null){
                "不限"
            }else{
                "${value.first}cm-${value.last}cm"
            }
        }
    private var salaryRange:IntRange?=null
        set(value) {
            field=value
            searchViewModel.setSalaryParameter(value)
            incomeValue.text=if (value==null){
                "不限"
            }else{
                "${value.first}k-${value.last}k"
            }
        }
    private val marriageState=HashSet<MarriageEnum>()
    //学历
    private var eduList:List<EduEnum>?=ArrayList<EduEnum>().apply {
        this.add(EduEnum.unlimited)
    }
        set(value) {
            field=value
            searchViewModel.setEduParameter(value)
        }
    private val eduDialog by lazy {
        EduDialog(this){ list ->
            //设置选择后的结果
            educationText.text=list.map { it.title }.toTextString()
            eduList=list
        }
    }
    //住房情况
    private var housingList:List<HousingEnum>?=null
        set(value) {
            field=value
            searchViewModel.setHousingParameter(value)
        }
    private val housingDialog by lazy {
        HousingDialog(this){ list ->
            //设置选择后的结果
            housingText.text=list.map { it.title }.toTextString()
            housingList=(list)
        }
    }
    //有没有孩子
    private var wantChildrenList:List<WantChildrenEnum>?=null
        set(value) {
            field=value
            searchViewModel.setWantChildrenParameter(value)
        }
    private val wantChildrenDialog by lazy {
        WantChildrenDialog(this){ list ->
            //设置选择后的结果
            childrenText.text=list.map { it.title }.toTextString()
            wantChildrenList=(list)
        }
    }
    //买车情况
    private var currentBuyCar=BuyCarEnum.unlimited
        set(value) {
            field=value
            searchViewModel.setBuyCarParameter(value)
        }
    private val buyCarDialog by lazy {
        BuyCarDialog(this){ car ->
            //设置选择后的结果
            buyCarText.text=car.title
            currentBuyCar=car
        }
    }
    //有无头像
    private var currentHavePortrait= HeadPortraitEnum.unlimited
        set(value) {
            field=value
            searchViewModel.setHavePortraitParameter(value)
        }
    private val havePortraitDialog by lazy {
        HavePortraitDialog(this){
            //设置选择后的结果
            headPortraitText.text=it.title
            currentHavePortrait=it
        }
    }
    //是否会员
    private var currentIsVip= IsVipEnum.unlimited
        set(value) {
            field=value
            searchViewModel.setVipParameter(value)
        }
    private val isVipDialog by lazy {
        IsVipDialog(this){
            //设置选择后的结果
            isVipText.text=it.title
            currentIsVip=it
        }
    }
    //登录情况
    private var currentOnLine= OnLineEnum.unlimited
        set(value) {
            field=value
            searchViewModel.setOnLineParameter(value)
        }
    private val onLineDialog by lazy {
        OnLineDialog(this){
            //设置选择后的结果
            isSignInText.text=it.title
            currentOnLine=it
        }
    }
    //是否实名
    private var currentRealName= RealNameEnum.unlimited
        set(value) {
            field=value
            searchViewModel.setRealNameParameter(value)
        }
    private val realNameDialog by lazy {
        RealNameDialog(this){
            //设置选择后的结果
            isRealNameText.text=it.title
            currentRealName=it
        }
    }
    //职业
    private var currentOccupation:Pair<OccupationDataBean,OccupationBean>?=null
    set(value) {
        field=value
        if (value==null){
            occupationText.text="不限"
        }else{
            occupationText.text=value.first.name+" "+value.second.name
        }
        searchViewModel.setOccupationParameter(value)
    }
    private val occupationDialog by lazy {
        val occupationData=getOccupationData()?.data?.map {
            Pair(it,it.child?: emptyList())
        }
        if (occupationData==null){
            return@lazy null
        }
        object :SecondaryOptionsDialog<OccupationDataBean, OccupationBean>(this,occupationData,{ t, i->
            currentOccupation=Pair(t,i)
        }){
            override fun getFirstText(t: OccupationDataBean): String {
                return t.name?:""
            }

            override fun getSecondText(i: OccupationBean): String {
                return i.name?:""
            }

        }.also {
            it.setTitle("你期望Ta的职业是？")
        }
    }

    private val cityData by lazy {
        getCityData()?.data?.map {
            Pair(it, it.cityList)
        }
    }
    //工作地区
    private var currentWorkplaceList:List<Pair<Province, City>>?=null
    set(value) {
        field=value
        if (value.isNullOrEmpty()){
            workplaceText.text="不限"
        }else{
            workplaceText.text=value.toTextString({
                it.first.name+"·"+it.second.name
            },"、")
        }
        searchViewModel.setWorkCityParameter(value)
    }
    private val workPlaceDialog by lazy {
        val cityData=cityData
        if (cityData==null){
            return@lazy null
        }
        object :MultipleSecondaryOptionsDialog<Province, City>(this,cityData,{
            currentWorkplaceList=it
        }){
            override fun getFirstText(t: Province): String {
                return t.name
            }

            override fun getSecondText(i: City): String {
                return i.name
            }

        }.also {
            it.maxContent=5
            it.setTitle("你期望Ta的工作地是？")
        }
    }
    //籍贯
    private var currentNativePlaceList:List<Pair<Province, City>>?=null
        set(value) {
            field=value
            if (value.isNullOrEmpty()){
                nativePlaceText.text="不限"
            }else{
                nativePlaceText.text=value.toTextString({
                    it.first.name+"·"+it.second.name
                },"、")
            }
            searchViewModel.setNativePlaceParameter(value)
        }
    private val nativePlaceDialog by lazy {
        val cityData=cityData
        if (cityData==null){
            return@lazy null
        }
        object :MultipleSecondaryOptionsDialog<Province, City>(this,cityData,{
            currentNativePlaceList=it
        }){
            override fun getFirstText(t: Province): String {
                return t.name
            }

            override fun getSecondText(i: City): String {
                return i.name
            }

        }.also {
            it.maxContent=5
            it.setTitle("你期望Ta的籍贯地是？")
        }
    }
    //星座
    private var currentConstellationList:List<ConstellationEnum>?=null
        set(value) {
            field=value
            if (value.isNullOrEmpty()){
                constellationText.text="不限"
            }else{
                constellationText.text=value.map { it.title }.toTextString()
            }
            searchViewModel.setConstellationParameter(value)
        }
    private val constellationDialog by lazy {
        object :ListOptionsDialog<ConstellationEnum>(this,ConstellationEnum.values().toList(),{
            currentConstellationList=it
        }){
            override fun addItemCollectMutex(
                item: ConstellationEnum,
                list: List<ConstellationEnum>
            ): List<ConstellationEnum> {
                if(item==ConstellationEnum.unlimited){
                    return list.toList()
                }else if (list.contains(ConstellationEnum.unlimited)){
                    return listOf(ConstellationEnum.unlimited)
                }
                return super.addItemCollectMutex(item, list)
            }

            override fun getFirstText(i: ConstellationEnum): String {
                return i.title
            }
        }.also {
            it.maxContent=12
            it.setTitle("你期望Ta的星座是？")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initListener()
    }

    private fun initListener(){
        ageReset.setOnClickListener {
            ageRange=null
            heightRange=null
            salaryRange=null
            ageSetValue.reset()
            heightSetValue.reset()
            incomeSetValue.reset()
        }
        ageSetValue.rangeCall={first,last->
            ageRange= IntRange(SearchViewModel.MIN_AGE+(first*(SearchViewModel.MAX_AGE-SearchViewModel.MIN_AGE)+0.5f).toInt(),SearchViewModel.MIN_AGE+(last*(SearchViewModel.MAX_AGE-SearchViewModel.MIN_AGE)+0.5f).toInt()).let {
                if (it.first==SearchViewModel.MIN_AGE&&it.last==SearchViewModel.MAX_AGE){
                    null
                }else {
                    it
                }
            }
        }
        heightSetValue.rangeCall={first,last->
            heightRange= IntRange(SearchViewModel.MIN_HEIGHT+(first*(SearchViewModel.MAX_HEIGHT-SearchViewModel.MIN_HEIGHT)+0.5f).toInt(),
                SearchViewModel.MIN_HEIGHT+(last*(SearchViewModel.MAX_HEIGHT-SearchViewModel.MIN_HEIGHT)+0.5f).toInt()).let {
                if (it.first==SearchViewModel.MIN_HEIGHT&&it.last==SearchViewModel.MAX_HEIGHT){
                    null
                }else {
                    it
                }
            }
        }
        incomeSetValue.rangeCall={first,last->
            salaryRange= IntRange(SearchViewModel.MIN_INCOME+(first*(SearchViewModel.MAX_INCOME-SearchViewModel.MIN_INCOME)+0.5f).toInt(),SearchViewModel.MIN_INCOME+(last*(SearchViewModel.MAX_INCOME-SearchViewModel.MIN_INCOME)+0.5f).toInt()).let {
                if (it.first==SearchViewModel.MIN_INCOME&&it.last==SearchViewModel.MAX_INCOME){
                    null
                }else {
                    it
                }
            }
        }
        val marriagePair=listOf(
            unlimitedMarriage to MarriageEnum.unlimited,
            unmarriedMarriage to MarriageEnum.unmarried,
            divorceMarriage to MarriageEnum.divorce,
            widowhoodMarriage to MarriageEnum.widowhood,
        )
        marriagePair.forEach { pair->
            pair.first.setOnClickListener {view->
                if (view.isSelected){
                    if(pair.second==MarriageEnum.unlimited){
                        return@setOnClickListener
                    }else {
                        marriageState.remove(pair.second)
                        if (marriageState.isEmpty()){
                            marriageState.add(MarriageEnum.unlimited)
                        }
                    }
                }else{
                    if (pair.second==MarriageEnum.unlimited){
                        marriageState.clear()
                        marriageState.add(MarriageEnum.unlimited)
                    }else{
                        marriageState.add(pair.second)
                        if (MarriageEnum.values().all {
                                marriageState.contains(it)||it==MarriageEnum.unlimited
                            }){
                            marriageState.clear()
                            marriageState.add(MarriageEnum.unlimited)
                        }else{
                            marriageState.remove(MarriageEnum.unlimited)
                        }
                    }
                }
                marriagePair.forEach {
                    it.first.isSelected=marriageState.contains(it.second)
                }
                searchViewModel.setMarriageParameter(marriageState.toList())
            }
        }
        education.setOnClickListener {
            eduDialog.show()
        }
        workplace.setOnClickListener {
            occupationDialog?:return@setOnClickListener toast("获取城市数据失败")
            workPlaceDialog?.show()
        }
        occupation.setOnClickListener {
            occupationDialog?:return@setOnClickListener toast("获取岗位数据失败")
            occupationDialog?.show()
        }
        nativePlace.setOnClickListener {
            nativePlaceDialog?:return@setOnClickListener toast("获取城市数据失败")
            nativePlaceDialog?.show()
        }
        housing.setOnClickListener {
            housingDialog.show()
        }
        buyCar.setOnClickListener {
            buyCarDialog.show()
        }
        children.setOnClickListener {
            wantChildrenDialog.show()
        }
        headPortrait.setOnClickListener {
            havePortraitDialog.show()
        }
        constellation.setOnClickListener {
            constellationDialog.show()
        }
        isVip.setOnClickListener {
            isVipDialog.show()
        }
        isRealName.setOnClickListener {
            realNameDialog.show()
        }
        isSignIn.setOnClickListener {
            onLineDialog.show()
        }
        startSearch.setOnClickListener {
            startActivity(SearchResultActivity.getIntent(this@SearchParamActivity,searchViewModel.getParameter()))
        }
        accurateSearch.setOnClickListener {
            startActivity(Intent(this,AccurateSearchActivity::class.java))
        }
    }

    private fun List<String>.toTextString(split:String="、"):String{
        val stringBuilder=StringBuilder()
        this.forEach {
            stringBuilder.append(it)
            stringBuilder.append(split)
        }
        return stringBuilder.removeSuffix(split).toString()
    }

    private fun <T> List<T>.toTextString(transactionAction:(T)->String,split:String):String{
        val stringBuilder=StringBuilder()
        this.forEach {
            stringBuilder.append(transactionAction.invoke(it))
            stringBuilder.append(split)
        }
        return stringBuilder.removeSuffix(split).toString()
    }
}