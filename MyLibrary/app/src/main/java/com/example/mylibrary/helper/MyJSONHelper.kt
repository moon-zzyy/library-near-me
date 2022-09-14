package com.example.mylibrary.helper

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

class MyJSONHelper(val string: String) {
  //  lateinit var jsonArray1:JSONArray
    //    var tests=ArrayList<MyData>() //[title]=MyData(title, url, draft)
    lateinit var jsonArray2:JSONArray

    var records = ArrayList<MyData>()
    var operrecords= ArrayList<MyOperData>()
    var booksrecords= ArrayList<MyBooksData>()
    var contactrecords= ArrayList<MyContactData>()
    var maprecords= ArrayList<MyMapData>()

    fun readFile(): Boolean {//데이터 저장하기
        val jsonObject = JSONObject(string)
        jsonArray2 = jsonObject.getJSONArray("records")//데이터
        Log.d("json","${jsonArray2.length()}")
        for (i in 0 until jsonArray2.length()) {
            val record = jsonArray2.getJSONObject(i)//한개씩

            //MyData
            val name = record.getString("도서관명")
            val type=record.getString("도서관유형")
            val address=record.getString("소재지도로명주소")

            //MyOperData
            val holiday = record.getString("휴관일")
            val weekdaysOpen = record.getString("평일운영시작시각")
            val weekdaysClosed = record.getString("평일운영종료시각")
            val satOpen = record.getString("토요일운영시작시각")
            val satClosed = record.getString("토요일운영종료시각")
            val holOpen = record.getString("공휴일운영시작시각")
            val holClosed = record.getString("공휴일운영종료시각")

            //MyBooksData
            val seat = record.getString("열람좌석수")
            val books = record.getString("자료수(도서)")
            val books2 = record.getString("자료수(연속간행물)")
            val books3 = record.getString("자료수(비도서)")
            val borrowNum = record.getString("대출가능권수")
            val borrowDay = record.getString("대출가능일수")

            //MyContactData
            val tel = record.optString("도서관전화번호")
            val website = record.optString("홈페이지주소")

            //MyMapData
            val lat = record.optString("위도")
            val long = record.optString("경도")

            records.add(MyData(name, type, address))
            operrecords.add(
                MyOperData(
                    holiday,
                    weekdaysOpen,
                    weekdaysClosed,
                    satOpen,
                    satClosed,
                    holOpen,
                    holClosed
                )
            )
            booksrecords.add(
                MyBooksData(
                    seat,
                    books,
                    books2,
                    books3,
                    borrowNum,
                    borrowDay
                )
            )
            contactrecords.add(
                MyContactData(
                    tel,
                    website
                )
            )
            maprecords.add(
                MyMapData(
                    name,
                    lat.toDoubleOrNull(),
                    long.toDoubleOrNull()
                )
            )

            //            val city = record.getString("시도명")
//            val province = record.getString("시군구명")
//            val name = record.getString("운영기관명")
//            val name = record.getString("부지면적")
//            val name = record.getString("건물면적")
//            val name = record.getString("데이터기준일자")
//            val name = record.getString("제공기관코드")
//            val name = record.getString("제공기관명")
        }

        Log.d("json","1: ${records.size}")
        Log.d("json","2: ${operrecords.size}")
        Log.d("json","3: ${booksrecords.size}")
        Log.d("json","4: ${contactrecords.size}")
        Log.d("json","5: ${maprecords.size}")
        return records.size!=0
    }

    //데이터 전달하기
    fun getData()=records
    fun getOperData()=operrecords
    fun getBooksData()=booksrecords
    fun getContactData()=contactrecords
    fun getMapData()=maprecords

}