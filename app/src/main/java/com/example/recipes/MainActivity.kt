package com.example.recipes

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.util_classes.SpacesItemDecoration
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var recipesAdapter: RecipesAdapter
    private lateinit var dataBaseHelper: DataBaseHelper

    private val spanCount = 2
    private val includeEdge = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataBaseHelper = DataBaseHelper(this)
        recipesAdapter = RecipesAdapter(dataBaseHelper.getRecipes(), this)

        rv_recipes_list.apply {
            adapter = recipesAdapter
            layoutManager = GridLayoutManager(this@MainActivity, 2)
        }

        val spacingPixels = (10 * resources.displayMetrics.density).roundToInt()

        rv_recipes_list.addItemDecoration(SpacesItemDecoration(spanCount, spacingPixels, includeEdge))

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
        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView
        searchView.queryHint = "Введите ингредиент"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                recipesAdapter.filter.filter(newText)
                return false
            }
        })
        return true
    }

    override fun onResume() {
        super.onResume()

        if (intent.getBooleanExtra("Created", false)) {
            updateAdapter()
            clearIntent()
        }
    }

    private fun clearIntent() = intent.removeExtra("Created")

    private fun updateAdapter() {
        recipesAdapter = RecipesAdapter(dataBaseHelper.getRecipes(), this)
        rv_recipes_list.adapter = recipesAdapter
    }

    private fun addRecipe() {
        val intent = Intent(this, RecipeCreation::class.java)
        startActivity(intent)
    }

}