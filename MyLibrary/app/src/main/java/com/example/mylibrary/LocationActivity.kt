package com.example.mylibrary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mylibrary.helper.MyDBHelper
import com.example.mylibrary.helper.MyMapData
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.map_view.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class LocationActivity : AppCompatActivity(),
    MapView.POIItemEventListener, MapView.CurrentLocationEventListener {
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView

    lateinit var myDBHelper: MyDBHelper
    var maprecords = ArrayList<MyMapData>()

    lateinit var mapViewContainer: ViewGroup
    lateinit var mapView: MapView
    var nowPoint = MapPoint.mapPointWithGeoCoord(37.620518, 127.012452)
    var gpsOn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        initLayout()
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
        myDBHelper = MyDBHelper(this)
        maprecords = myDBHelper.getMapRecords()
        Log.d("map", "${maprecords.size}")

        mapView = MapView(this)
        mapViewContainer = findViewById(R.id.map_view)
        mapViewContainer.addView(mapView, 0)

        //MapView
        for (i in maprecords.indices) {
            if (maprecords[i].mlat != null) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(
                    maprecords[i].mlat!!.toDouble(),
                    maprecords[i].mlong!!.toDouble()
                )
                val marker = MapPOIItem()
                marker.itemName = maprecords[i].mname
                marker.mapPoint = mapPoint
                marker.markerType = MapPOIItem.MarkerType.BluePin// 기본으로 제공하는 BluePin 마커 모양.
                marker.selectedMarkerType =
                    MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                marker.showAnimationType = MapPOIItem.ShowAnimationType.SpringFromGround
                mapView.addPOIItem(marker)
            }
            //      Log.d("MAPS","$k: ${maprecords[k]?.mlat}, ${maprecords[k]?.mlong}")
        }
        mapView.zoomIn(true)
        mapView.zoomOut(true)

        //set listeners
        mapView.setPOIItemEventListener(this)
        mapView.setCurrentLocationEventListener(this)
        mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff

        gps.setOnClickListener {
            if (gpsOn) {
                mapView.currentLocationTrackingMode =
                    MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading//현재위치
                mapView.setMapCenterPoint(nowPoint, true)
                gpsOn = false
            } else {
                mapView.currentLocationTrackingMode =
                    MapView.CurrentLocationTrackingMode.TrackingModeOff
                gpsOn = true
            }
        }
    }

    private fun initLayout() {
        //toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarLoc)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        with(actionbar!!) {
            setHomeAsUpIndicator(R.drawable.baseline_menu_black_18dp)
            setDisplayHomeAsUpEnabled(true)
        }

        //toolbar item click
        toolbar.setOnMenuItemClickListener { // Handle the menu item
            when (it.itemId) {
                android.R.id.home -> {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
            true
        }

        //drawerLayout
        drawerLayout = findViewById(R.id.drawerLayoutLoc)
        navigationView = findViewById(R.id.navigationViewLoc)
        val drawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.closed)
        drawerLayout.addDrawerListener(drawerToggle)

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.findByName -> {
                    mapViewContainer.removeView(mapView)
                    val intent = Intent(this, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.findByState -> {
                    mapViewContainer.removeView(mapView)
                    val intent = Intent(this, AreaActivity::class.java)
                    finish()
                    startActivity(intent)
                }
                R.id.findByLoc -> {
//                    val intent = Intent(this, LocationActivity::class.java)
//                    startActivity(intent)
//                    finish()
                }

                R.id.appInfo -> {
                    val intent = Intent(this, AppInfoActivity::class.java)
                    startActivity(intent)
                }
                R.id.developerInfo -> {
                    val intent = Intent(this, DevInfoActivity::class.java)
                    startActivity(intent)
                }
            }
            //   if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


    }

    override fun onBackPressed() {
        mapViewContainer.removeView(mapView)

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    //POIItemEnebtListener interface
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
        mapViewContainer.removeView(mapView)
        val intent = Intent(this, InfoActivity::class.java)
        intent.putExtra("name3", p1?.itemName)
        startActivity(intent)
        //   finish()
    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
    }

    //CurrentLocationEventListener
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {
        Toast.makeText(this, "위치 기능을 켜주십시오.", Toast.LENGTH_SHORT).show()
    }

    override fun onCurrentLocationUpdate(p0: MapView?, p1: MapPoint?, p2: Float) {
        nowPoint = p1!!
    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
        Toast.makeText(this, "위치 검색이 중지되었습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {
        // TODO("Not yet implemented")
    }
}
