package com.example.mylibrary

import android.Manifest
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylibrary.helper.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.toolbar_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var myJSONHelper: MyJSONHelper
    lateinit var myDBHelper: MyDBHelper
    var myPermission = false//권한승인 여부

  //  var tests = ArrayList<MyData>()
    var records = ArrayList<MyData>()
    var operrecords = ArrayList<MyOperData>()
    var booksrecords = ArrayList<MyBooksData>()
    var contactrecords = ArrayList<MyContactData>()
    var maprecords = ArrayList<MyMapData>()
    var foundRecords = ArrayList<MyData>()//한 도서관에 대한 정보
    lateinit var adapter: MyLibraryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermission()
        if (myPermission) {
            val file = this.getDatabasePath("mydb.db")
            if (!file.exists()) {
                openFile()
                initDB()
            } else {
//                openFile()
//                initDB()
                myDBHelper = MyDBHelper(this)
                foundRecords = myDBHelper.find("")
                adapter = MyLibraryAdapter(foundRecords)
                recyclerView.adapter = adapter
            }
            initLayout()
            initClick()
        }
    }

    private fun getHashKey() {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")
        for (signature in packageInfo!!.signatures) {
            try {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d(
                    "KeyHash",
                    Base64.encodeToString(md.digest(), Base64.DEFAULT)
                )
            } catch (e: NoSuchAlgorithmException) {
                Log.e(
                    "KeyHash",
                    "Unable to get MessageDigest. signature=$signature",
                    e
                )
            }
        }
    }
    private fun getPermission() {
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            myPermission = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CALL_PHONE
                ),
                100
            )
            myPermission = false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                myPermission = true
                val file = this.getDatabasePath("mydb.db")
                if (!file.exists()) {
                    openFile()
                    initDB()
                } else {
                    myDBHelper = MyDBHelper(this)
                    foundRecords = myDBHelper.find("")
                    adapter = MyLibraryAdapter(foundRecords)
                    recyclerView.adapter = adapter
                }
                initLayout()
                initClick()
            } else {
                Toast.makeText(this, "위치 및 전화걸기 권한을 허용바랍니다.", Toast.LENGTH_SHORT).show()
                myPermission = false
            }
        }
    }

    private fun initClick() {
        adapter.mOnClickListener = object : MyLibraryAdapter.OnItemClickListener {
            override fun onItemClick(
                holder: MyLibraryAdapter.MyViewHolder,
                view: View,
                data: MyData,
                position: Int
            ) {
                val intent = Intent(this@MainActivity, InfoActivity::class.java)
                intent.putExtra("name", data.mname)
                startActivity(intent)
            }
        }
    }

    private fun openFile() {//JSON 파일 열기
        val assetManager = resources.assets
        // val inputStream = assetManager.open("library_standard_data.json")
        val inputStream = assetManager.open("library_standard_data.json")
        var jsonString = inputStream.bufferedReader().use {
            it.readText()
        }
        myJSONHelper = MyJSONHelper(jsonString)
        if (myJSONHelper.readFile()) {//파일 읽기
            records = myJSONHelper.getData()
            operrecords = myJSONHelper.getOperData()
            booksrecords = myJSONHelper.getBooksData()
            contactrecords = myJSONHelper.getContactData()
            maprecords = myJSONHelper.getMapData()
        }

//        Log.d("operrecords", "1: ${records.size}")
//        Log.d("operrecords", "2: ${operrecords.size}")
//        Log.d("operrecords", "3: ${booksrecords.size}")
//        Log.d("operrecords", "4: ${contactrecords.size}")
//        Log.d("operrecords", "5: ${maprecords.size}")

    }

    private fun initDB() {
        myDBHelper = MyDBHelper(this)
        myDBHelper.deleteAll()

        for (i in 0 until operrecords.size) {
            val name = records[i].mname
            val type = records[i].mtype
            val address = records[i].maddress

            //MyOperData
            val holiday = operrecords[i].mholiday
            val weekdaysOpen = operrecords[i].mweekdaysOpen
            val weekdaysClosed = operrecords[i].mweekdaysClosed
            val satOpen = operrecords[i].msatOpen
            val satClosed = operrecords[i].msatClosed
            val holOpen = operrecords[i].mholOpen
            val holClosed = operrecords[i].mholClosed

            //MyBooksData
            val seat = booksrecords[i].mseat
            val books = booksrecords[i].mbooks
            val books2 = booksrecords[i].mbooks2
            val books3 = booksrecords[i].mbooks3
            val borrowNum = booksrecords[i].mborrowNum
            val borrowDay = booksrecords[i].mborrowDay

            //MyContactData
            val tel = contactrecords[i].mtel
            val website = contactrecords[i].mwebsite

            //MyMapData
            val lat = maprecords[i].mlat
            val long = maprecords[i].mlong

            val result1 = myDBHelper.insert1(
                MyData(name, type, address),
                MyOperData(
                    holiday, weekdaysOpen, weekdaysClosed,
                    satOpen, satClosed, holOpen, holClosed
                ),
                MyBooksData(
                    seat, books, books2, books3, borrowNum, borrowDay
                ),
                MyContactData(tel, website),
                MyMapData(name, lat, long)
            )
        }
    }

    private fun initLayout() {
        //toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        with(actionbar!!) {
            setHomeAsUpIndicator(R.drawable.baseline_menu_black_18dp)
            setDisplayHomeAsUpEnabled(true)
        }

        //toolbar item click
        toolbar.setOnMenuItemClickListener { // Handle the menu item
            when (it.itemId) {
                R.id.app_bar_search -> {
                }
                android.R.id.home -> {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
            true
        }

        //drawerLayout
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        val drawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.closed)
        drawerLayout.addDrawerListener(drawerToggle)

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.findByName -> {
                }
                R.id.findByState -> {
                    val intent = Intent(this, AreaActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.findByLoc -> {
                    val intent = Intent(this, LocationActivity::class.java)
                    startActivity(intent)
                    finish()
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
        //recyclerView
        recyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        foundRecords = myDBHelper.find("")
        adapter = MyLibraryAdapter(foundRecords)
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)

        //도서관명으로 검색하기
        val searchView = menu?.findItem(R.id.app_bar_search)?.actionView as SearchView

        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = "도서관명을 입력하세요"
        searchView.maxWidth = toolbar.measuredWidth
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {//결과보여주기
                foundRecords.clear()
                foundRecords = myDBHelper.find(query.toString())
                adapter = MyLibraryAdapter(foundRecords)
                recyclerView.adapter = adapter
                initClick()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {//실시간
                foundRecords.clear()
                foundRecords = myDBHelper.find(newText.toString())
                adapter = MyLibraryAdapter(foundRecords)
                recyclerView.adapter = adapter
                initClick()
                return true
            }
        })
        return true//toolbar 버튼 활성화
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }
}
