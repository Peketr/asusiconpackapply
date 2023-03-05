package com.huep.asusiconpackapplyer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private fun getName(pack:ApplicationInfo): String {
        return pack.loadLabel(packageManager) as String
    }

    @SuppressLint("DiscouragedApi")
    private fun isIconPack(pack:ApplicationInfo):Boolean {
        val res = packageManager.getResourcesForApplication(pack)
        val resourceId = res.getIdentifier("appfilter", "xml", pack.packageName)
        return resourceId !=0
    }

    private var packItems = ArrayList<ApplicationInfo>()
    private var selectedApp = ""

    private fun updateList(listView:ListView){
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        packages.sortBy { pack -> getName(pack) }

        packItems.clear()

        for (packageInfo in packages) {
            if (isIconPack(packageInfo) || packageInfo.packageName == "com.asus.launcher"){
                packItems.add(packageInfo)
            }
        }

        if (packItems.size == 0){
            var placeholder = ApplicationInfo()
            placeholder.packageName="No Icon Packs installed"
            packItems.add(placeholder)

        }

        listView.adapter = IconPackListAdapter(this,packItems)
        (listView.adapter as IconPackListAdapter).notifyDataSetChanged()    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.listview)

        updateList(listView)

        listView.onItemClickListener = AdapterView.OnItemClickListener {
            _, _, position, _ ->
            selectedApp = packItems[position].packageName
            Toast.makeText(applicationContext, "Selected " + getName(packItems[position]),Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("unused")
    fun applyIconPack(view: View) {
        if (selectedApp != ""){
            try {
                val asus = Intent("com.asus.launcher")
                asus.action = "com.asus.launcher.intent.action.APPLY_ICONPACK"
                asus.addCategory(Intent.CATEGORY_DEFAULT)
                asus.putExtra("com.asus.launcher.iconpack.PACKAGE_NAME", selectedApp)
                asus.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                applicationContext.startActivity(asus)
                Toast.makeText(baseContext,"Setting icon pack and exiting",Toast.LENGTH_LONG).show()
                this.finish()
            } catch (e: Exception) {
                //something went wrong
                e.printStackTrace()
            }
        }
    }

    @Suppress("unused")
    fun refreshList(view: View) {
        val listView = findViewById<ListView>(R.id.listview)
        updateList(listView)
    }

    override fun onBackPressed() {
        this.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options,menu)
        val versionName = BuildConfig.VERSION_NAME
        menu?.add("v$versionName${if(BuildConfig.DEBUG){" (Debug)"}else{""}}")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if  (item.itemId == R.id.github_menu_item){
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Peketr/asusiconpackapply"))
            startActivity(browserIntent)
        } else if (item.itemId == R.id.refresh_menu_item){
            val listView = findViewById<ListView>(R.id.listview)
            updateList(listView)
        }
        return true
    }

}