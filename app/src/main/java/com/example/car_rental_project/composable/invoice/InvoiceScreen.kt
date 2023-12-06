package com.example.car_rental_project.composable.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.car_rental_project.composable.transaction.TransactionCard
import com.example.car_rental_project.composable.transaction.TransactionType
import com.example.car_rental_project.model.TransactionEntity
import com.example.car_rental_project.model.TransactionStatus
import com.example.car_rental_project.model.UserEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    transactionData: List<TransactionEntity>?,
    authUser: UserEntity?,
) {
    var completedTransaction by remember { mutableStateOf<List<TransactionEntity>?>(null) }

    LaunchedEffect(transactionData) {
        completedTransaction = transactionData?.filter {
            (it.sellerId == authUser?.userId || it.buyerId == authUser?.userId) && it.status == TransactionStatus.FINISHED
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text(text = "Invoice")} )
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
                itemsIndexed(items = completedTransaction.orEmpty()) { index, transaction ->
                    Column {
                        InvoiceCard(userId = authUser?.userId ?: "", transaction = transaction)
                    }
                }
            }
        }
    }
}
@Composable
fun InvoiceCard(userId : String, transaction: TransactionEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        if(userId == transaction.sellerId) {
            Text(text = "Seller")
            Text("Buyer: ${transaction.buyerName}")
        }
        else if(userId == transaction.buyerId) {
            Text(text = "Buyer")
            Text("Seller: ${transaction.sellerName}")
        }
        Text(text = "title : ${transaction.carPost?.title}")
        Text(text = "Price: ${transaction.carPost?.price}")
        Text(text = "Status: ${transaction.status}")
    }
}