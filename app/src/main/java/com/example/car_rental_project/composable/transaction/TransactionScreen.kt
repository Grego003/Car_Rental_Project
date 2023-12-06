package com.example.car_rental_project.composable.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.car_rental_project.model.TransactionEntity
import com.example.car_rental_project.model.UserEntity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import com.example.car_rental_project.model.CarModel
import com.example.car_rental_project.model.TransactionStatus
import com.google.gson.annotations.SerializedName
enum class TransactionType(@SerializedName("type") val displayName: String) {
    BUYER("Buyer"),
    SELLER("Seller"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    transactionData: List<TransactionEntity>?,
    authUser: UserEntity?,
    acceptTransaction: (transaction: TransactionEntity) -> Unit,
    rejectTransaction: (transaction: TransactionEntity) -> Unit,
) {
    var sellerTransactionList by remember { mutableStateOf<List<TransactionEntity>?>(null) }
    var buyerTransactionList by remember { mutableStateOf<List<TransactionEntity>?>(null) }
    var topBarState by remember { mutableStateOf(TransactionType.BUYER) }

    LaunchedEffect(transactionData) {
        sellerTransactionList = transactionData?.filter {
            it.sellerId == authUser?.userId &&
                    it.status == TransactionStatus.WAITING
        }
        buyerTransactionList = transactionData?.filter {
            it.buyerId == authUser?.userId &&
                    it.status == TransactionStatus.WAITING
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text(text = "Transaction") },
            actions = {
                TextButton(
                    onClick = { topBarState = TransactionType.BUYER },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = if (topBarState == TransactionType.BUYER) Color.LightGray else Color.Transparent
                    ),
                ) {
                    Text("Buyer")
                }

                TextButton(
                    onClick = { topBarState = TransactionType.SELLER },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = if (topBarState == TransactionType.SELLER) Color.LightGray else Color.Transparent
                    )
                ) {
                    Text("Seller")
                }
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Green)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (topBarState == TransactionType.BUYER) {
                    itemsIndexed(items = buyerTransactionList.orEmpty()) { index, transaction ->
                        Column {
                            TransactionCard(transaction = transaction)
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { rejectTransaction(transaction) }
                            ) {
                                Text(text = "Cancel")
                            }
                        }
                    }
                } else {
                    itemsIndexed(items = sellerTransactionList.orEmpty()) { index, transaction ->
                        Column {
                            TransactionCard(transaction = transaction)
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { acceptTransaction(transaction) }
                            ) {
                                Text(text = "Approved")
                            }
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { rejectTransaction(transaction) }
                            ) {
                                Text(text = "Cancel")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: TransactionEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Title: ${transaction.carPost?.title}")
        Text("Buyer: ${transaction.buyerName}")
        Text(text = "Price: ${transaction.carPost?.price}")
        Text(text = "Status: ${transaction.status}")
    }
}
