package com.example.recipes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.recipes.utils.convertArrayToString
import com.example.recipes.utils.convertStringToArray

class DataBaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "recipes.db"
        const val DATABASE_VERSION = 15

        const val TABLE_NAME = "RECIPES_TABLE"
        const val COLUMN_RECIPE_IMAGE = "RECIPE_IMAGE"
        const val COLUMN_RECIPE_NAME = "RECIPE_NAME"
        const val COLUMN_RECIPE_DESCRIPTION = "RECIPE_DESCRIPTION"
        const val COLUMN_INGREDIENTS = "INGREDIENTS"
        const val COLUMN_FULL_RECIPE = "FULL_RECIPE"
        const val COLUMN_DELETED = "is_deleted"
    }

    private var deletedRecipeName = ""

    override fun onCreate(db: SQLiteDatabase?) {

        val createRecipesTableStatement =
            "CREATE TABLE $TABLE_NAME ($COLUMN_RECIPE_NAME TEXT UNIQUE, $COLUMN_RECIPE_DESCRIPTION TEXT, $COLUMN_INGREDIENTS TEXT, $COLUMN_RECIPE_IMAGE TEXT, $COLUMN_FULL_RECIPE TEXT, $COLUMN_DELETED INTEGER)"

        db?.execSQL(createRecipesTableStatement)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getRecipes(): ArrayList<Recipe> {

        val recipesList = arrayListOf<Recipe>()
        val queryString = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DELETED = 0"

        val cursor: Cursor = readableDatabase.rawQuery(queryString, null)

        if (cursor.moveToFirst()) {
            do {
                val recipeName = cursor.getString(0)
                val recipeDescription = cursor.getString(1)
                val recipeIngredients = cursor.getString(2)
                val recipeImage = cursor.getString(3)
                val fullRecipe = cursor.getString(4)

                val newRecipe = Recipe(recipeName, recipeDescription, convertStringToArray(recipeIngredients), recipeImage, fullRecipe)
                recipesList.add(newRecipe)
            } while (cursor.moveToNext())
        }

        cursor.close()
        readableDatabase.close()

        return recipesList
    }

    fun addRecipeToTable(recipe: Recipe) {
        val cv = ContentValues()

        cv.put(COLUMN_RECIPE_NAME, recipe.name)
        cv.put(COLUMN_RECIPE_DESCRIPTION, recipe.description)
        cv.put(COLUMN_INGREDIENTS, convertArrayToString(recipe.ingredients))
        cv.put(COLUMN_RECIPE_IMAGE, recipe.picture)
        cv.put(COLUMN_FULL_RECIPE, recipe.fullRecipe)
        cv.put(COLUMN_DELETED, 0)

        writableDatabase.insert(TABLE_NAME, null, cv)
    }

    fun removeRecipe(recipeName: String) {
        writableDatabase.execSQL("DELETE FROM $TABLE_NAME WHERE $COLUMN_DELETED = 1")

        writableDatabase.execSQL("UPDATE $TABLE_NAME SET $COLUMN_DELETED = 1 WHERE $COLUMN_RECIPE_NAME = '$recipeName'")
        deletedRecipeName = recipeName
    }

    fun restoreRecipe() {
        writableDatabase.execSQL("UPDATE $TABLE_NAME SET $COLUMN_DELETED = 0 WHERE $COLUMN_RECIPE_NAME = '$deletedRecipeName'")
    }
}