package com.example.mylibrary.helper

import java.io.Serializable

//도서관명, 유형, 소재지도로명주소
data class MyData(val mname:String, val mtype:String, val maddress: String) :Serializable
//휴관일, 평일운영시작시각, 평일운영종료시각, 토요일운영시작시각, 토요일운영종료시각, 공휴일운영시작시각, 공휴일운영종료시각
data class MyOperData(val mholiday:String,val mweekdaysOpen:String,val mweekdaysClosed:String,
                    val msatOpen:String,val msatClosed:String,val mholOpen:String,val mholClosed:String) :Serializable
//열람좌석수, 자료수(도서), 자료수(연속간행물), 자료수(비도서), 대출가능권수, 대출가능일수
data class MyBooksData(val mseat:String, val mbooks:String,val mbooks2:String,
                       val mbooks3:String, val mborrowNum:String, val mborrowDay:String) :Serializable
//도서관전화번호, 홈페이지주소
data class MyContactData(val mtel:String?, val mwebsite:String?) :Serializable
//도서관명, 위도, 경도
data class MyMapData(val mname:String,val mlat: Double?, val mlong: Double?) :Serializable
