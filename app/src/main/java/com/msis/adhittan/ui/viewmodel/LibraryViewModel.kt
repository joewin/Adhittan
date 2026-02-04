package com.msis.adhittan.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msis.adhittan.data.model.MenuItem
import com.msis.adhittan.data.repository.LibraryRepository
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    var currentPath by mutableStateOf("assets")
        private set
    
    var menuItems by mutableStateOf<List<MenuItem>>(emptyList())
        private set
        
    var textContent by mutableStateOf<String?>(null)
        private set
        
    var isLoading by mutableStateOf(false)
        private set

    init {
        loadMenu("assets")
    }

    fun loadMenu(path: String) {
        viewModelScope.launch {
            isLoading = true
            menuItems = libraryRepository.getMenu(path)
            currentPath = path
            textContent = null
            isLoading = false
        }
    }

    fun loadContent(file: String) {
        if (file == "..") {
            // Submenu logic hardcoded for now as per requirement
            loadMenu("assets/sutta")
        } else {
            viewModelScope.launch {
                isLoading = true
                // file might be "awgartha.txt" (if in root) or "params.txt" (if loaded from sutta menu, but the file path in menu item needs care)
                // The repository handles raw reading. If we are in "assets/sutta", the file link in menu.txt is usually relative to that?
                // Wait, in web version: `one.txt`. Fetch was `${currentPath}/${filename}`.
                // So if currentPath is `assets/sutta`, filename `one.txt` -> `assets/sutta/one.txt`.
                // My repo `getContent` takes the full path relative to assets root.
                
                val fullPath = if (currentPath == "assets") file else "${currentPath.replace("assets/", "")}/$file"
                textContent = libraryRepository.getContent(fullPath)
                isLoading = false
            }
        }
    }

    fun goBack() {
        if (textContent != null) {
            textContent = null
        } else if (currentPath != "assets") {
            loadMenu("assets")
        }
    }
}
