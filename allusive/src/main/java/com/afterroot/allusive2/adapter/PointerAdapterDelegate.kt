/*
 * Copyright (C) 2016-2020 Sandip Vaghela
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.afterroot.allusive2.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.afterroot.allusive2.adapter.callback.ItemSelectedCallback
import com.afterroot.allusive2.model.IPointer
import org.koin.core.Koin
import java.util.*

class PointerAdapterDelegate(private val callbacks: ItemSelectedCallback, koin: Koin) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList = ArrayList<IPointer>()
    private var delegateAdapters = SparseArrayCompat<TypeDelegateAdapter>()

    init {
        with(delegateAdapters) {
            put(IPointer.TYPE_POINTER, PointerDelegate(callbacks))
            put(IPointer.TYPE_LOCAL_P, LocalPointerDelegate(callbacks, koin))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        delegateAdapters.get(getItemViewType(position))!!.onBindViewHolder(holder, mList[position])

    override fun getItemViewType(position: Int): Int = mList[position].type()

    fun add(value: List<IPointer>) {
        removeAll()
        mList.addAll(value)
        notifyItemRangeInserted(0, mList.size)
    }

    fun getItem(position: Int) = mList[position]

    fun getList(): List<IPointer> = mList

    private fun removeAll() {
        val size = mList.size
        mList.clear()
        notifyItemRangeRemoved(0, size)
    }
}

