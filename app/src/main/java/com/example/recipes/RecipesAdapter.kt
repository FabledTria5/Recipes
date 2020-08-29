package com.example.recipes

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.utils.convertToBitmap
import com.example.recipes.utils.second
import com.google.android.material.snackbar.Snackbar
import de.hdodenhof.circleimageview.CircleImageView

class RecipesAdapter(private var recipesForUI: ArrayList<Recipe>, private val context: Context) : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>(), Filterable {

    private val dataBaseHelper = DataBaseHelper(context)
    private var recipesAll : ArrayList<Recipe> = dataBaseHelper.getRecipes()

    inner class RecipesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val recipeName: TextView = itemView.findViewById(R.id.recipe_name)
        private val recipeDescription: TextView = itemView.findViewById(R.id.recipe_description)
        private val recipeImage: CircleImageView = itemView.findViewById(R.id.civ_recipe_image)
        private val recipeIngredients: TextView = itemView.findViewById(R.id.tv_ingredients_string)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            updateRecipesAll()
            val recipe = recipesForUI[position]

            recipeName.text = recipe.name
            recipeDescription.text = recipe.description
            recipeImage.setImageBitmap(convertToBitmap(recipe.picture))

            if (recipe.ingredients.size > 1) {
                recipeIngredients.text = "${recipe.ingredients.toString().second().toUpperCase()}${recipe.ingredients.toString().subSequence(2, recipe.ingredients.toString().length - 2)}"
            } else {
                recipeIngredients.text = "Ингредиенты не указаны"
            }
        }
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) = holder.bind(position)

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
                    if (ingredient.ingredients.toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
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