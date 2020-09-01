package com.example.recipes

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.recipes.utils.convertToBitmap
import kotlinx.android.synthetic.main.recipe_full_view.*

class FullRecipe() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_full_view)

        val recipe = intent.getSerializableExtra("Recipe") as Recipe

        tv_recipe_name.text = recipe.name
        tv_recipe_description.text = recipe.description
        setIngredientsList(recipe.ingredients)
        iv_recipe_image.setImageBitmap(convertToBitmap(recipe.picture))
    }

    @SuppressLint("SetTextI18n")
    private fun setIngredientsList(ingredients: ArrayList<String>) {

        ingredients.forEach {
            val ingredientView = layoutInflater.inflate(R.layout.simple_ingredient, null, false)

            val ingredientName = ingredientView.findViewById<TextView>(R.id.tv_ingredient_name)

            ingredients_list.addView(ingredientView)
            ingredientName.text = it
        }
    }

}