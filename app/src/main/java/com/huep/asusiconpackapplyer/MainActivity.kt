package com.huep.asusiconpackapplyer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    var listItems = ArrayList<String>()
    var packItems = ArrayList<ApplicationInfo>()

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    var adapter: ArrayAdapter<String>? = null

    var packages: MutableList<ApplicationInfo>? = null
    var selectedApp = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            listItems
        )
        packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        packages!!.sortBy { pack -> getName(pack) }

        val listView = findViewById<ListView>(R.id.listview)
        listView.adapter = adapter
        for (packageInfo in packages!!) {
            if (isIconPack(packageInfo)){
                listItems.add(getName(packageInfo))
                packItems.add(packageInfo)
            }
        }
        adapter!!.notifyDataSetChanged()

        listView.onItemClickListener = AdapterView.OnItemClickListener {
                _, _, position, _ ->
            if (packItems[position].packageName != null){
                selectedApp = packItems[position].packageName
                Toast.makeText(applicationContext, "Selected " + getName(packItems[position]),Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Something went wrong selecting package",Toast.LENGTH_LONG).show()
            }

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
            } catch (e: ActivityNotFoundException) {
                //something went wrong
            } catch (e: NullPointerException) {
                //something went wrong but with extra steps
            }
        }
    }
}