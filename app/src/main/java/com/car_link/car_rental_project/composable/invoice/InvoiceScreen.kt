package com.car_link.car_rental_project.composable.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.car_link.car_rental_project.composable.home.formatPrice
import com.car_link.car_rental_project.composable.transaction.TransactionType
import com.car_link.car_rental_project.model.TransactionEntity
import com.car_link.car_rental_project.model.TransactionStatus
import com.car_link.car_rental_project.model.UserEntity

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
            title = { Text(text = "Invoice") },
        )

        if(completedTransaction.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 300.dp),
                contentAlignment = Alignment.Center,

                ) {
                Text(text = "No invoice found", color = Color.LightGray)
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            itemsIndexed(items = completedTransaction.orEmpty()) { index, transaction ->
                Column {
                    InvoiceCard(userId = authUser?.userId ?: "", transaction = transaction)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun InvoiceCard(userId: String, transaction: TransactionEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (userId == transaction.sellerId) "Seller" else "Buyer",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (userId == transaction.sellerId) Color.Green else Color.Black,
            )

            Text(
                text = if (userId == transaction.sellerId) "Buyer: ${transaction.buyerName}" else "Seller: ${transaction.sellerName}",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "${transaction.carPost?.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Rp ${formatPrice(transaction.carPost?.price)}",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "Status: ${transaction.status}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
