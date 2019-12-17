package com.touchlab.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import co.touchlab.kampstarter.db.Items
import co.touchlab.kampstarter.db.KampstarterDb
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.touchlab.shared.DatabaseHelper

import com.touchlab.shared.createApplicationScreenMessage
import com.touchlab.shared.ktorExample.KtorApiImpl
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text_view.text = createApplicationScreenMessage()
        getDatabaseRows()
        performNetworkRequest()
    }

    private fun getDatabaseRows(){
        val dbHelper = DatabaseHelper(AndroidSqliteDriver(KampstarterDb.Schema, this, "KampStarterDb"))
        dbHelper.insertItem(1,"Test")
        dbHelper.insertItem(2,"Test2")
        val queries: Query<Items> = dbHelper.selectAllItems()
        val items:List<Items> = queries.executeAsList()
        Log.i("DB",items.toString())
    }

    private fun performNetworkRequest() {
        KtorApiImpl.getJsonFromApi{ result ->
            Log.i("TAG",result)
        }
    }
}