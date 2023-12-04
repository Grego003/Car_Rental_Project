package com.example.car_rental_project.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.twotone.AddCircle
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material.icons.twotone.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.car_rental_project.R

sealed class BottomNavItem(
    val title : String,
    val selectedIcon : ImageVector,
    val unSelectedIcon : ImageVector,
    val hasNews : Boolean,
    val badgeCount : Int? = null,
    val screenRoute : String) {
    object Home : BottomNavItem(
        title = "Home",
        selectedIcon = Icons.TwoTone.Home,
        unSelectedIcon = Icons.Default.Home,
        false,
        null,
        "home"
    )
    object Profile : BottomNavItem(
            title = "Profile",
            selectedIcon = Icons.TwoTone.Face,
            unSelectedIcon = Icons.Default.Face,
            false,
            null,
            "profile"
    )
    object AddCarPost : BottomNavItem(
        title = "transaction",
        selectedIcon = Icons.TwoTone.ShoppingCart,
        unSelectedIcon = Icons.Default.ShoppingCart,
        false,
        null,
        "transaction"
    )
    object History: BottomNavItem(
        title = "Invoice",
        selectedIcon = Icons.TwoTone.Edit,
        unSelectedIcon = Icons.Default.Edit,
        false,
        null,
        "invoice"
    )
}

