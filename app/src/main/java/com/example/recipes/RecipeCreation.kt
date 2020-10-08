package com.example.recipes

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipes.utils.convertToBase64
import kotlinx.android.synthetic.main.recipe_creation.*
import java.io.FileNotFoundException

class RecipeCreation : AppCompatActivity(), View.OnClickListener {

    private lateinit var imageView: ImageView
    private lateinit var dataBaseHelper: DataBaseHelper
    private var selectedImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_creation)

        dataBaseHelper = DataBaseHelper(this)
        dataBaseHelper.deleteRemovedRecipe()

        btn_add.setOnClickListener(this)
        btn_create_recipe.setOnClickListener {
            createRecipe()
        }

        iv_pick_image.setOnClickListener { pickImage() }
    }

    override fun onClick(v: View?) = addView()

    override fun onActivityResult(requestCode: Int, resultCode: Int, photo: Intent?) {
        super.onActivityResult(requestCode, resultCode, photo)

        when (requestCode) {
            1 -> if (resultCode == RESULT_OK && photo != null) {
                try {
                    val imageUri = photo.data
                    val imageStream = contentResolver.openInputStream(imageUri!!)
                    selectedImage = BitmapFactory.decodeStream(imageStream)
                    iv_pick_image.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun addView() {
        val ingredientView = layoutInflater.inflate(R.layout.add_ingredient_view, null, false)

        imageView = ingredientView.findViewById(R.id.btn_remove)
        imageView.setOnClickListener { removeView(ingredientView) }

        layout_list.addView(ingredientView)
    }

    private fun pickImage() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)

        photoPickerIntent.type = "image/*"

        startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), 1)
    }

    private fun removeView(v: View?) = layout_list.removeView(v)

    private fun createRecipe() {

        if (selectedImage == null) selectedImage = (iv_pick_image.drawable as BitmapDrawable).bitmap

        if (et_recipe_name.text.toString() == "") {
            Toast.makeText(this,"Ошибка при создании рецепта. Поле с названием рецепта должно быть заполнено!", Toast.LENGTH_LONG).show()
        }
         else {
            dataBaseHelper.addRecipeToTable(Recipe(et_recipe_name.text.toString(), et_recipe_description.text.toString(), getIngredients(), getIngredientsAmount(), convertToBase64(selectedImage), et_full_recipe.text.toString()))
            val intent = Intent(this, MainActivity::class.java)
                .putExtra("Created", true)
            startActivity(intent)
        }
    }

    private fun getIngredientsAmount(): ArrayList<String> {
        val ingredientsAmount: ArrayList<String> = arrayListOf()
        var i = 0
        while (i < layout_list.childCount) {
            val recipeView = layout_list.getChildAt(i)
            val ingredientAmount = recipeView.findViewById<EditText>(R.id.et_ingredients_amount).text.toString()
            if (ingredientAmount != "")
                ingredientsAmount.add(ingredientAmount)
            else
                ingredientsAmount.add("N/A")

            i++
        }

        return ingredientsAmount
    }

    private fun getIngredients(): ArrayList<String> {
        val ingredients: ArrayList<String> = arrayListOf()
        var i = 0
        while (i < layout_list.childCount) {
            val recipeView = layout_list.getChildAt(i)
            val ingredientName = recipeView.findViewById<EditText>(R.id.et_ingredient_name).text.toString()

            if (ingredientName != "") ingredients.add(ingredientName)

            i++
        }

        return ingredients
    }

}