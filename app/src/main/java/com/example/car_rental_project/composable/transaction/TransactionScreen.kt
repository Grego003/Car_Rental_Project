package com.example.car_rental_project.composable.transaction

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.car_rental_project.composable.home.formatPrice
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
                .background(color = MaterialTheme.colorScheme.background)
        ) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (topBarState == TransactionType.BUYER) {
                    itemsIndexed(items = buyerTransactionList.orEmpty()) { index, transaction ->
                        Column {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .padding(horizontal = 8.dp)
                            ) {
                                TransactionCard(transaction = transaction, topBarState)
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                        .padding(horizontal = 8.dp),
                                    onClick = { rejectTransaction(transaction) },
                                ) {
                                    Text(text = "Cancel")
                                }
                            }
                        }
                    }
                } else {
                    itemsIndexed(items = sellerTransactionList.orEmpty()) { index, transaction ->
                        Column {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                            ) {
                                TransactionCard(transaction = transaction, transactionState = topBarState)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        modifier = Modifier
                                            .weight(1f),
                                        onClick = { acceptTransaction(transaction) }
                                    ) {
                                        Text(text = "Accept")
                                    }
                                    OutlinedButton(
                                        modifier = Modifier
                                            .weight(1f),
                                        onClick = { rejectTransaction(transaction) }
                                    ) {
                                        Text(text = "Reject")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))

        }
    }
}
@Composable
fun TransactionCard(transaction: TransactionEntity, transactionState : TransactionType) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(transaction.carPost?.images?.firstOrNull())
                    .build()
            )
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = transaction.carPost?.title.orEmpty(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                if(transactionState == TransactionType.BUYER){
                    Text(
                        text = "Seller: ${transaction.sellerName}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
                else {
                    Text(
                        text = "buyer: ${transaction.buyerName}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Price: Rp ${formatPrice(transaction.carPost?.price)}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Status: ${transaction.status}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
            }
        }
    }
}