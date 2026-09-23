package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SpendSavvyBottomNav
import com.example.ui.components.SpendSavvyTopBar
import com.example.ui.screens.AddExpenseDialog
import com.example.ui.screens.CreateGroupDialog
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.GroupProScreen
import com.example.ui.screens.SettleUpScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeumorphicBg
import com.example.ui.viewmodel.SpendSavvyViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: SpendSavvyViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        var showSplash by remember { mutableStateOf(true) }

        if (showSplash) {
          SplashScreen(onFinishSplash = { showSplash = false })
        } else {
          SpendSavvyApp(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun SpendSavvyApp(viewModel: SpendSavvyViewModel) {
  val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
  val showAddExpense by viewModel.showAddExpenseDialog.collectAsStateWithLifecycle()
  val showCreateGroup by viewModel.showCreateGroupDialog.collectAsStateWithLifecycle()
  val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()
  val selectedGroupId by viewModel.selectedGroupId.collectAsStateWithLifecycle()
  val activeGroup = allGroups.find { it.id == selectedGroupId }

  val screenTitle = when (activeTab) {
    0 -> activeGroup?.name?.uppercase() ?: "SPENDSAVVY"
    1 -> "SETTLE UP"
    2 -> "STATISTICS"
    else -> "GROUPS & PRO"
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = NeumorphicBg,
    topBar = {
      SpendSavvyTopBar(
        title = screenTitle,
        onMenuClick = {
          // Toggle to Statistics or Home
          if (activeTab != 2) viewModel.setActiveTab(2) else viewModel.setActiveTab(0)
        },
        onProfileClick = {
          // Open Group & Pro settings
          viewModel.setActiveTab(3)
        }
      )
    },
    bottomBar = {
      SpendSavvyBottomNav(
        activeTab = activeTab,
        onTabSelected = { viewModel.setActiveTab(it) }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(NeumorphicBg)
        .padding(innerPadding)
    ) {
      when (activeTab) {
        0 -> ExpensesScreen(
          viewModel = viewModel,
          onNavigateToSettle = { viewModel.setActiveTab(1) }
        )
        1 -> SettleUpScreen(viewModel = viewModel)
        2 -> StatisticsScreen(viewModel = viewModel)
        3 -> GroupProScreen(viewModel = viewModel)
      }

      if (showAddExpense) {
        AddExpenseDialog(
          viewModel = viewModel,
          onDismiss = { viewModel.showAddExpense(false) }
        )
      }

      if (showCreateGroup) {
        CreateGroupDialog(
          viewModel = viewModel,
          onDismiss = { viewModel.showCreateGroup(false) }
        )
      }
    }
  }
}

