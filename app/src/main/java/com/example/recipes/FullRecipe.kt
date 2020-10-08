package com.example.recipes

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.recipes.utils.convertToBitmap
import kotlinx.android.synthetic.main.recipe_full_view.*
import java.util.ArrayList

class FullRecipe : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_full_view)

        val recipe = intent.getSerializableExtra("Recipe") as Recipe

        tv_recipe_name.text = recipe.name
        tv_recipe_description.text = recipe.description
        setIngredients(recipe.ingredients, recipe.ingredientsAmount)
        iv_recipe_image.setImageBitmap(convertToBitmap(recipe.picture))
        tv_full_recipe.text = recipe.fullRecipe
    }

    private fun setIngredients(ingredients: ArrayList<String>, ingredientsAmount: ArrayList<String>) {
        ingredients.forEach {
            val ingredientView = layoutInflater.inflate(R.layout.simple_ingredient, null, false)
            val ingredientName = ingredientView.findViewById<TextView>(R.id.tv_ingredient_name)
            val ingredientAmount = ingredientView.findViewById<TextView>(R.id.tv_ingredient_amount)

            ingredients_list.addView(ingredientView)
            ingredientName.text  = it
            ingredientAmount.text = ingredientsAmount[ingredients.indexOf(it)]
        }
    }

}