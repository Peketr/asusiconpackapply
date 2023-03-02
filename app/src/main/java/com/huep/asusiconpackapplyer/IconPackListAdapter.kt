package com.huep.asusiconpackapplyer

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.view.View
import android.view.ViewGroup
import android.widget.*
class IconPackListAdapter(private val context: Activity, private val _package: ArrayList<ApplicationInfo>)
    : ArrayAdapter<ApplicationInfo>(context, R.layout.iconpack_row_layout, _package) {

    fun getName(pack:ApplicationInfo): String {
        return pack.loadLabel(getContext().packageManager) as String
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.iconpack_row_layout, null, true)

        val titleText = rowView.findViewById(R.id.title) as TextView
        val imageView = rowView.findViewById(R.id.icon) as ImageView
        val subtitleText = rowView.findViewById(R.id.description) as TextView

        titleText.text = getName(_package[position])
        val icon = _package[position].loadIcon(getContext().packageManager)
        imageView.setImageDrawable(icon)
        subtitleText.text = _package[position].packageName

        return rowView
    }
}