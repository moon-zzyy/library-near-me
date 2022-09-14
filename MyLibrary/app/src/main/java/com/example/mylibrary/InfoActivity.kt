package com.example.mylibrary

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.mylibrary.helper.MyDBHelper
import com.example.mylibrary.helper.MyMapData
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class InfoActivity : AppCompatActivity() {
    lateinit var myDBHelper:MyDBHelper

    var name:String?=null
    var name2:String?=null
    var name3:String?=null

    var row=ArrayList<String>()

    lateinit var mapViewContainer: ViewGroup
    lateinit var mapView : MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        initData()
        initLayout()
//       initMap()
    }

    override fun onResume() {
        super.onResume()
        initMap()
    }
    override fun onStop() {
        super.onStop()
        mapViewContainer.removeView(mapView)
    }
    private fun initMap() {
        mapView=MapView(this)
        mapViewContainer = findViewById(R.id.map_view_info)
        mapViewContainer.addView(mapView,0)

        mapView.currentLocationTrackingMode=MapView.CurrentLocationTrackingMode.TrackingModeOff
        mapView.removeAllPOIItems()

        var mapLatLong = MyMapData(
            row[0],
            row[18].toDoubleOrNull(),
            row[19].toDoubleOrNull()
        )

        //MapView
        if (mapLatLong.mlat != null) {
            val mapPoint = MapPoint.mapPointWithGeoCoord(
                mapLatLong.mlat!!.toDouble(),
                mapLatLong.mlong!!.toDouble()
            )
            mapView.setMapCenterPoint(mapPoint, true)

            val marker = MapPOIItem()
            marker.itemName = row[0]
            marker.mapPoint = mapPoint
            marker.markerType = MapPOIItem.MarkerType.BluePin// 기본으로 제공하는 BluePin 마커 모양.
            marker.selectedMarkerType =
                MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            marker.showAnimationType = MapPOIItem.ShowAnimationType.SpringFromGround
            mapView.addPOIItem(marker)
        } else
            mapViewContainer.visibility = View.VISIBLE


        mapView.zoomIn(false)
        mapView.zoomOut(false)

        mapView.setOnTouchListener { v, event ->
            return@setOnTouchListener true
        }
    }

    private fun initData() {
        val i= intent
        myDBHelper= MyDBHelper(this)

        //main
        name= i.getStringExtra("name")
        if(name!=null){
            row = myDBHelper.getRow(name!!)//한 행
        }
        //area
        name2=i.getStringExtra("name2")
        if(name2!=null){
            row = myDBHelper.getRow(name2!!)//한 행
        }
        //location
        name3=i.getStringExtra("name3")
        if(name3!=null){
            row = myDBHelper.getRow(name3!!)//한 행
        }

    }

    private fun initLayout() {
        //toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarInfo)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        with(actionbar!!) {
            title=row[0]
            subtitle=row[1].substring(0,2)
            setDisplayHomeAsUpEnabled(true)
        }

        val address: TextView =findViewById(R.id.addressi)
        val tel: TextView =findViewById(R.id.teli)
        val website: TextView =findViewById(R.id.websitei)
        val holiday: TextView =findViewById(R.id.holidayi)
        address.text=row[2]
        tel.text="+82-"+row[16]
        website.text=row[17]
        holiday.text=row[3]

        val weekdays: TextView =findViewById(R.id.weekdaysi)
        val saturday: TextView =findViewById(R.id.saturdayi)
        val hol: TextView =findViewById(R.id.holi)
        weekdays.text="${row[4]} ~ ${row[5]}"
        saturday.text="${row[6]} ~ ${row[7]}"
        hol.text="${row[8]} ~ ${row[9]}"

        val books: TextView =findViewById(R.id.booksi)
        val books2: TextView =findViewById(R.id.books2i)
        val books3: TextView =findViewById(R.id.books3i)
        books.text=row[11]
        books2.text=row[12]
        books3.text=row[13]

        val borrowNum: TextView =findViewById(R.id.borrowNumi)
        val borrowDay: TextView =findViewById(R.id.borrowDayi)
        val seat: TextView =findViewById(R.id.seati)
        borrowNum.text=row[14]
        borrowDay.text=row[15]
        seat.text=row[10]

    }

    override fun onBackPressed() {
        super.onBackPressed()
        mapViewContainer.removeView(mapView)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mapViewContainer.removeView(mapView)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_info, menu)
        return true
    }
}
