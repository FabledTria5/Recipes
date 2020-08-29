package com.example.recipes

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var recipesAdapter: RecipesAdapter
    private lateinit var dataBaseHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataBaseHelper = DataBaseHelper(this)
        recipesAdapter = RecipesAdapter(dataBaseHelper.fillMainRv(), this)

        rv_recipes_list.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                recipesAdapter.removeRecipe(viewHolder)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(baseContext, R.color.colorDelete))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_delete_white_24)
                    .create()
                    .decorate()
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(rv_recipes_list)

        btn_create.setOnClickListener {
            addRecipe()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        openAdminLogin()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        if (intent.getBooleanExtra("Created", false)) {
            updateAdapter()
            clearIntent()
        }
    }

    private fun clearIntent() = intent.removeExtra("Created")

    fun updateAdapter() {
        recipesAdapter = RecipesAdapter(dataBaseHelper.fillMainRv(), this)
        rv_recipes_list.adapter = recipesAdapter
    }

    private fun addRecipe() {
        val intent = Intent(this, RecipeCreation::class.java)
        startActivity(intent)
    }

    private fun openAdminLogin() {
        Toast.makeText(this, "Еще не сделано", Toast.LENGTH_LONG).show()
    }

}