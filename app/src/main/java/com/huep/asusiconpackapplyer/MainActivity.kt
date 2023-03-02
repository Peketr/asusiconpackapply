package com.huep.asusiconpackapplyer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    fun getName(pack:ApplicationInfo): String {
        return pack.loadLabel(packageManager) as String
    }

    fun isIconPack(pack:ApplicationInfo):Boolean {
        val res = packageManager.getResourcesForApplication(pack)
        val resourceId = res.getIdentifier("appfilter", "xml", pack.packageName)
        return resourceId !=0
    }

    private var packItems = ArrayList<ApplicationInfo>()
    private var selectedApp = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        packages.sortBy { pack -> getName(pack) }

        val listView = findViewById<ListView>(R.id.listview)

        for (packageInfo in packages!!) {
            if (isIconPack(packageInfo)){
                packItems.add(packageInfo)
            }
        }
        listView.adapter = IconPackListAdapter(this,packItems)
        (listView.adapter as IconPackListAdapter).notifyDataSetChanged()

        listView.onItemClickListener = AdapterView.OnItemClickListener {
            _, _, position, _ ->
            selectedApp = packItems[position].packageName
            Toast.makeText(applicationContext, "Selected " + getName(packItems[position]),Toast.LENGTH_LONG).show()
        }
    }

    fun applyIconPack(view: View) {
        if (selectedApp != ""){
            try {
                val asus = Intent("com.asus.launcher")
                asus.action = "com.asus.launcher.intent.action.APPLY_ICONPACK"
                asus.addCategory(Intent.CATEGORY_DEFAULT)
                asus.putExtra("com.asus.launcher.iconpack.PACKAGE_NAME", selectedApp)
                asus.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                applicationContext.startActivity(asus)
                this.finish()
            } catch (e: Exception) {
                //something went wrong
                e.printStackTrace()
            }
        }
    }
}