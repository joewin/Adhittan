package com.msis.adhittan.data.repository

import android.content.Context
import com.msis.adhittan.data.model.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class LibraryRepository(private val context: Context) {

    suspend fun getMenu(path: String): List<MenuItem> = withContext(Dispatchers.IO) {
        val items = mutableListOf<MenuItem>()
        try {
            // Path logic: if root, fetch librarymenu.txt, if submenu, fetch menu.txt
            val assetPath = if (path == "assets") "librarymenu.txt" else "$path/menu.txt"
            
            // Fix path for raw assets access (assets folder is root)
            // If path is "assets", we look for "librarymenu.txt" in root of assets
            // If path is "assets/sutta", we look for "sutta/menu.txt"
            
            val actualFile = if (path == "assets") "librarymenu.txt" else path.replace("assets/", "") + "/menu.txt"

            context.assets.open(actualFile).use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?
                // Split by comma outside the implementation, or simple regex
                // Format: "Title", "filename"
                val regex = "\"(.*?)\",\\s*\"(.*?)\"".toRegex()
                
                while (reader.readLine().also { line = it } != null) {
                    val match = regex.find(line!!)
                    if (match != null) {
                        val (title, file) = match.destructured
                        items.add(MenuItem(title, file))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        items
    }

    suspend fun getContent(filename: String): String = withContext(Dispatchers.IO) {
        try {
            // Filename might be "sutta/one.txt" or just "awgartha.txt"
            context.assets.open(filename).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "Error loading content: ${e.message}"
        }
    }
    
    // Helper to get flatten list for "New Session"
    suspend fun getAllChants(): List<MenuItem> = withContext(Dispatchers.IO) {
        val allItems = mutableListOf<MenuItem>()
        val rootItems = getMenu("assets")
        
        for (item in rootItems) {
            if (item.filename == "..") {
                // Hardcoded knowledge based on structure: submenus are in "sutta"
                // In a robust app, we'd infer this, but here sticking to known logic
                val subItems = getMenu("assets/sutta")
                subItems.forEach { subItem ->
                     allItems.add(MenuItem(subItem.title, "sutta/${subItem.filename}"))
                }
            } else {
                allItems.add(item)
            }
        }
        allItems
    }
}
