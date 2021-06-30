package com.example.gestaofinanceira.adapter

import android.view.View

interface FinancyItemListener {

    fun onFinancyItemClick(v: View, pos:Int)

    fun onFinancyItemLongClick(v: View, pos:Int)

}