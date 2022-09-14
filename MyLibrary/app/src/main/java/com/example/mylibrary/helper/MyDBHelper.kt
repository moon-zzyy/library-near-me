package com.example.mylibrary.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(val context: Context): SQLiteOpenHelper(context,  DB_NAME, null,  VERSION) {

    var foundRecords = ArrayList<MyData>()

    companion object {
        val VERSION = 1
        val DB_NAME = "mydb.db"
        val TABLE_NAME = "records"
        val ID = "_id"

        //MyData
        val NAME = "mname"
        val TYPE = "mtype"
        val ADDRESS = "maddress"

        //MyOperData
        val HOLIDAY = "mholiday"
        val WEEKDAYSOPEN = "mweekdaysOpen"
        val WEEKDAYSCLOSED = "mweekdaysClosed"
        val SATOPEN = "msatOpen"
        val SATCLOSED = "msatClosed"
        val HOLOPEN = "mholOpen"
        val HOLCLOSED = "mholClosed"

        //MyBooksData
        val SEAT = "mseat"
        val BOOKS = "mbooks"
        val BOOKS2 = "mbooks2"
        val BOOKS3 = "mbooks3"
        val BORROWNUM = "mborrowNum"
        val BORROWDAY = "mborrowDay"

        //MyContactData
        val TEL = "mtel"
        val WEBSITE = "mwebsite"

        //MyMapData
        val LAT = "mlat"
        val LONG = "mlong"
    }

    override fun onCreate(db: SQLiteDatabase?) {//create table
        val create = "create table if not exists " + TABLE_NAME + " (" +
                ID + " integer primary key autoincrement, " +

                NAME + " text, " +
                TYPE + " text, " +
                ADDRESS + " text, " +

                HOLIDAY + " text, " +
                WEEKDAYSOPEN + " text, " +
                WEEKDAYSCLOSED + " text, " +
                SATOPEN + " text, " +
                SATCLOSED + " text, " +
                HOLOPEN + " text, " +
                HOLCLOSED + " text, " +

                SEAT + " text, " +
                BOOKS + " text, " +
                BOOKS2 + " text, " +
                BOOKS3 + " text, " +
                BORROWNUM + " text, " +
                BORROWDAY + " text, " +

                TEL + " text, " +
                WEBSITE + " text, " +

                LAT + " double, " +
                LONG + " double)"

        db?.execSQL(create)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {//if version changed
        val drop = "drop table if exists " + TABLE_NAME
        db?.execSQL(drop)
        onCreate(db)
    }

    fun deleteAll() {//새로만들기
        val db = this.writableDatabase
        db?.execSQL("drop table if exists " + TABLE_NAME)
        onCreate(db)
    }

    fun insert1(myData: MyData, myOperData: MyOperData, myBooksData: MyBooksData,
                myContactData: MyContactData, myMapData: MyMapData
    ): Boolean {
        val contentValues = ContentValues()
        contentValues.put(NAME, myData.mname)
        contentValues.put(TYPE, myData.mtype)
        contentValues.put(ADDRESS, myData.maddress)

        contentValues.put(HOLIDAY, myOperData.mholiday)
        contentValues.put(WEEKDAYSOPEN, myOperData.mweekdaysOpen)
        contentValues.put(WEEKDAYSCLOSED, myOperData.mweekdaysClosed)
        contentValues.put(SATOPEN, myOperData.msatOpen)
        contentValues.put(SATCLOSED, myOperData.msatClosed)
        contentValues.put(HOLOPEN, myOperData.mholOpen)
        contentValues.put(HOLCLOSED, myOperData.mholClosed)

        contentValues.put(SEAT, myBooksData.mseat)
        contentValues.put(BOOKS, myBooksData.mbooks)
        contentValues.put(BOOKS2, myBooksData.mbooks2)
        contentValues.put(BOOKS3, myBooksData.mbooks3)
        contentValues.put(BORROWNUM, myBooksData.mborrowNum)
        contentValues.put(BORROWDAY, myBooksData.mborrowDay)

        contentValues.put(TEL, myContactData.mtel)
        contentValues.put(WEBSITE, myContactData.mwebsite)

        contentValues.put(LAT, myMapData.mlat)
        contentValues.put(LONG, myMapData.mlong)

        val db = this.writableDatabase

        return if (db.insert(TABLE_NAME, null, contentValues) > 0) {//성공
            db.close()
            true
        } else {//실패
            db.close()
            false
        }
    }

    fun find(name: String): ArrayList<MyData> {//Main
        var sql = "select * from " + TABLE_NAME +
                " where " + NAME + " LIKE \'" + name + "%\'"
        sql+= " order by $NAME asc "
        val db = this.readableDatabase
        val cursor = db.rawQuery(sql, null)

        if (cursor.count != 0) {//cursor에 정보가 있으면
            foundRecords = showRecord(cursor)
            cursor.close()
            db.close()
        } else {
            cursor.close()
            db.close()
        }
        return foundRecords
    }

    fun find2(address: String): ArrayList<MyData> {//Area
        var sql = "select * from " + TABLE_NAME +
                " where " + ADDRESS + " LIKE \'" + address + "%\'"
        sql+= " order by $ADDRESS asc "
        val db = this.readableDatabase
        val cursor = db.rawQuery(sql, null)

        if (cursor.count != 0) {//cursor에 정보가 있으면
            foundRecords = showRecord(cursor)
            cursor.close()
            db.close()
        } else {
            cursor.close()
            db.close()
        }
        return foundRecords
    }

    fun getMapRecords():ArrayList<MyMapData>{
        var sql = "select * from $TABLE_NAME"
        //sql+= " order by $ADDRESS asc "
        val db = this.readableDatabase
        val cursor = db.rawQuery(sql, null)
        var myData= ArrayList<MyMapData>()

        cursor.moveToFirst()
        if (cursor.count != 0) {//cursor에 정보가 있으면
            do {
                val name=cursor.getString(1)
                var lat:Double? =null
                var long :Double? =null
                if(cursor.getDouble(19)!=null&&cursor.getDouble(20)!=null){
                     lat = cursor.getDouble(19)
                     long = cursor.getDouble(20)
                }
                myData.add(
                    MyMapData(
                        name,
                        lat,
                        long
                    )
                )

            } while (cursor.moveToNext())
            cursor.close()
            db.close()
        } else {
            cursor.close()
            db.close()
        }
        return myData
    }
    fun getRow(name: String): ArrayList<String> {//한 행 뽑기
        val db = this.readableDatabase
        val sql = "select * from " + TABLE_NAME +
                " where " + NAME + " = \'" + name + "\'"
        val cursor = db.rawQuery(sql, null)
        var row = ArrayList<String>()

        if (cursor.moveToFirst()) {//cursor에 정보가 있으면
                for (i in 1 until cursor.columnCount) {
                    var record = "-"
                    if (cursor.getString(i) != null) {
                        record = cursor.getString(i)
                    }
                    row.add(record)
                }
            cursor.close()
            db.close()

        } else {
            cursor.close()
            db.close()
        }
        return row
    }

    fun showRecord(cursor: Cursor): ArrayList<MyData> {
        var myData = ArrayList<MyData>()
        cursor.moveToFirst()
        do {
            val name = cursor.getString(1)
            val type = cursor.getString(2)
            val address = cursor.getString(3)
            myData.add(MyData(name, type, address))

        } while (cursor.moveToNext())
        return myData
    }
}