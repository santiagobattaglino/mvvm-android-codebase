package com.santiagobattaglino.mvvm.codebase.domain.model.interiorefimero

import android.os.Parcelable
import com.santiagobattaglino.mvvm.codebase.domain.entity.Stock
import kotlinx.android.parcel.Parcelize

@Parcelize
class StockByUserResponse(
    val data: List<Stock>,
    val total: Int
) : Parcelable