package com.example.mylibrary.helper


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mylibrary.R
import kotlinx.android.synthetic.main.item_library.view.*


class MyLibraryAdapter(private val mValues: ArrayList<MyData>
) : RecyclerView.Adapter<MyLibraryAdapter.MyViewHolder>() {

    interface OnItemClickListener {//custom listener 인터페이스 정의
      fun onItemClick(holder: MyViewHolder, view: View, data: MyData, position: Int)
    }
    var mOnClickListener: OnItemClickListener?=null//리스너 객체 참조 변수

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_library, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = mValues[position]
        holder.name.text = item.mname
        holder.type.text = item.mtype
        holder.address.text = item.maddress

        if(item.mtype=="어린이도서관")
            holder.type.text="어린이"
        else if(item.mtype=="작은도서관")
            holder.type.text="작은"
        else
            holder.type.text="공공"

    }

    override fun getItemCount(): Int = mValues.size

    inner class MyViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val name: TextView = mView.name
        val type: TextView = mView.type
        val address:TextView=mView.address

        init {
            mView.setOnClickListener{
                mOnClickListener?.onItemClick(this, it, mValues[adapterPosition],adapterPosition    )
            }
        }
    }
}
