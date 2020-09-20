package com.example.recipes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.utils.convertToBitmap
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList

class RecipesAdapter(private var recipesForUI: ArrayList<Recipe>, private val context: Context) : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>(), Filterable {

    private val dataBaseHelper = DataBaseHelper(context)
    private var recipesAll : ArrayList<Recipe> = dataBaseHelper.getRecipes()
    private var index = -1

    inner class RecipesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val recipeName: TextView = itemView.findViewById(R.id.recipe_name)
        private val recipeImage: ImageView = itemView.findViewById(R.id.iv_recipe_image)
        private val recipeIngredients: TextView = itemView.findViewById(R.id.tv_ingredients_string)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            updateRecipesAll()
            val recipe = recipesForUI[position]

            recipeName.text = recipe.name
            recipeImage.setImageBitmap(convertToBitmap(recipe.picture))
            recipeIngredients.text = ""

            when {
                recipe.ingredients.size in 2..3 -> recipe.ingredients.forEach { recipeIngredients.text = "${recipeIngredients.text}$it; "}
                recipe.ingredients.size > 3 -> recipeIngredients.text = "${recipe.ingredients[0]}; ${recipe.ingredients[1]}; ${recipe.ingredients[2]}"
                else -> recipeIngredients.text = "Ингредиенты не указаны"
            }
        }
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        holder.bind(position)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, FullRecipe::class.java)
            intent.putExtra("Recipe", recipesForUI[position])
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecipesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recipy_item, parent, false))

    override fun getItemCount() = recipesForUI.size

    override fun getFilter() = filter

    private val filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {

            val filteredRecipesList = arrayListOf<Recipe>()

            if (constraint.toString().isEmpty()) {
                filteredRecipesList.addAll(recipesAll)
            } else {
                for (ingredient in recipesAll) {
                    if (ingredient.ingredients.toString().toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))) {
                        filteredRecipesList.add(ingredient)
                    }
                }
            }

            val filterResults = FilterResults()
            filterResults.values = filteredRecipesList

            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            recipesForUI.clear()
            recipesForUI.addAll(results?.values as Collection<Recipe>)
            notifyDataSetChanged()
        }

    }

    fun removeRecipe(viewHolder: RecyclerView.ViewHolder) {
        val removedPosition = viewHolder.adapterPosition
        val removedItem = recipesForUI[removedPosition]

        dataBaseHelper.removeRecipe(recipesForUI[removedPosition].name)

        recipesForUI.removeAt(removedPosition)
        updateRecipesAll()
        notifyItemRemoved(removedPosition)

        Snackbar.make(viewHolder.itemView, "Удалено", Snackbar.LENGTH_LONG)
            .setAction("Отменить") {
                dataBaseHelper.restoreRecipe()
                recipesForUI.add(removedPosition, removedItem)
                updateRecipesAll()
                notifyItemInserted(removedPosition)
            }.show()
    }

    private fun updateRecipesAll() {
        recipesAll = dataBaseHelper.getRecipes()
    }
}