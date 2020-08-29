package com.example.recipes.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

private const val strSeparator = ", "

fun convertToBase64(bitmap: Bitmap?): String {
    val os = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, os)
    val byteArray = os.toByteArray()
    return Base64.encodeToString(byteArray, 0)
}

fun convertToBitmap(base64String: String): Bitmap {
    val decodedString = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}

fun convertArrayToString(array: ArrayList<String>): String? {
    var str = ""

    for (element in array) {
        str += element
        if (array.indexOf(element) < array.size - 1) str += strSeparator
    }
    return str
}

fun convertStringToArray(string: String) =  ArrayList(string.split(strSeparator))

fun String.second() = get(1)