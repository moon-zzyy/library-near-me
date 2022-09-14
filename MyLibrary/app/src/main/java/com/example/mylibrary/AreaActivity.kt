package com.example.mylibrary

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mylibrary.helper.MyDBHelper
import com.example.mylibrary.helper.MyData
import com.example.mylibrary.helper.MyLibraryAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.toolbar_area.*

class AreaActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView

    lateinit var adapter: MyLibraryAdapter
    lateinit var myDBHelper: MyDBHelper
    var foundRecords = ArrayList<MyData>()//찾은 도서관목록

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area)

        initLayout()
        initRecyclerView()
        initClick()
    }
    private fun initClick() {
        adapter.mOnClickListener=object :
            MyLibraryAdapter.OnItemClickListener{
            override fun onItemClick(
                holder: MyLibraryAdapter.MyViewHolder,
                view: View,
                data: MyData,
                position: Int
            ) {
                val intent= Intent(this@AreaActivity,InfoActivity::class.java)
                intent.putExtra("name2",data.mname)
                startActivity(intent)
            }
        }
    }
    private fun initRecyclerView() {
        myDBHelper= MyDBHelper(this)

        //recyclerView
        recyclerViewArea.layoutManager= LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        foundRecords=myDBHelper.find2("")
        adapter= MyLibraryAdapter(foundRecords)
        recyclerViewArea.adapter=adapter
    }

    private fun initLayout() {
        //toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarArea)
        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        with(actionbar!!) {
            setHomeAsUpIndicator(R.drawable.baseline_menu_black_18dp)
            setDisplayHomeAsUpEnabled(true)
        }

        //toolbar item click
        toolbar.setOnMenuItemClickListener { // Handle the menu item
            when (it.itemId) {
                R.id.app_bar_search_area -> {
                }
                android.R.id.home -> {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
            true
        }

        //drawerLayout
        drawerLayout = findViewById(R.id.drawerLayoutArea)
        navigationView = findViewById(R.id.navigationViewArea)
        val drawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.closed)
        drawerLayout.addDrawerListener(drawerToggle)

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.findByName -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.findByState -> {
//                    val intent = Intent(this, AreaActivity::class.java)
//                    startActivity(intent)
//                    finish()
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
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)

        //도서관명으로 검색하기
        val searchView = menu?.findItem(R.id.app_bar_search)?.actionView as SearchView
        //val searchView= search?.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = "주소를 입력하세요"
        searchView.maxWidth = toolbarArea.measuredWidth
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {//결과보여주기
                foundRecords.clear()
                foundRecords=myDBHelper.find2(query.toString())
                adapter= MyLibraryAdapter(foundRecords)
                recyclerViewArea.adapter=adapter
                initClick()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {//실시간
                foundRecords.clear()
                foundRecords=myDBHelper.find2(newText.toString())
                adapter= MyLibraryAdapter(foundRecords)
                recyclerViewArea.adapter=adapter
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
