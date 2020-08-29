package com.example.recipes

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipes.utils.convertToBitmap
import com.google.android.material.snackbar.Snackbar
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*

class RecipesAdapter(private var recipes: ArrayList<Recipe>, private val context: Context) : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>() {

    private val dataBaseHelper = DataBaseHelper(context)

    inner class RecipesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val recipeName: TextView = itemView.findViewById(R.id.recipe_name)
        private val recipeDescription: TextView = itemView.findViewById(R.id.recipe_description)
        private val recipeImage: CircleImageView = itemView.findViewById(R.id.civ_recipe_image)

        fun bind(position: Int) {
            val recipe = recipes[position]

            recipeName.text = recipe.name
            recipeDescription.text = recipe.description
            recipeImage.setImageBitmap(convertToBitmap(recipe.picture))
        }
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) = holder.bind(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecipesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recipy_item, parent, false))

    override fun getItemCount() = recipes.size

    fun removeRecipe(viewHolder: RecyclerView.ViewHolder) {
        val removedPosition = viewHolder.adapterPosition
        val removedItem = recipes[removedPosition]

        dataBaseHelper.removeRecipe(recipes[removedPosition].name)

        recipes.removeAt(removedPosition)
        notifyItemRemoved(removedPosition)

        Snackbar.make(viewHolder.itemView, "Удалено", Snackbar.LENGTH_LONG)
            .setAction("Отменить") {
                dataBaseHelper.restoreRecipe()
                recipes.add(removedPosition, removedItem)
                notifyItemInserted(removedPosition)
            }.show()
    }
}