package com.example.recipes

import java.io.Serializable

class Recipe(val name: String, val description: String, val ingredients: ArrayList<String>, val picture: String, val fullRecipe: String) : Serializable