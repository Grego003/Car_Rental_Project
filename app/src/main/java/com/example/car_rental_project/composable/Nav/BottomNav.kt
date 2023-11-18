package com.example.car_rental_project.composable.Nav

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import com.example.car_rental_project.Navigation.BottomNavItem
import com.example.car_rental_project.view_model.NavViewModel


@Composable
fun BottomNavigation(
    navController: NavController,
    navViewModel: NavViewModel,
    ) {

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.AddCarPost,
        BottomNavItem.History,
        BottomNavItem.Profile,
    )
    val selectedIndex by navViewModel.state.collectAsState()


    Scaffold(
        bottomBar =  {
            NavigationBar {
                navItems.forEachIndexed {index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                                  navViewModel.updateNav(index)
                            navController.navigate(item.screenRoute)
                        },
                        label = {
                            Text(text = item.title)
                        },
                        icon = { BadgedBox(
                            badge = {
                                if(item.badgeCount != null) {
                                    Badge {
                                        Text(text = item.badgeCount.toString())
                                    }
                                }
                                    else if(item.hasNews) {
                                        Badge()
                                    }
                            }
                        ) {
                            Icon(
                                imageVector =
                                if(index == selectedIndex) {
                                    item.selectedIcon
                                } else item.unSelectedIcon,
                                contentDescription = item.title
                            )
                        } })
                }
            }
        }
    ) {

    }
}