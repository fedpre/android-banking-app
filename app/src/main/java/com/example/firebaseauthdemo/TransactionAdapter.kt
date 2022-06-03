package com.example.firebaseauthdemo

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseauthdemo.fragments.DashboardFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.system.exitProcess

class TransactionAdapter(private val transactionList : ArrayList<Transaction>) : RecyclerView.Adapter<TransactionAdapter.MyViewHolder>() {

    private var transactionArrayList : ArrayList<TransactionId> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction,
        parent, false)
        // Get the id of all the transactions
        getTransactionIdData()
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = transactionList[position]

        holder.transactionOperation.text = currentItem.operation
        holder.transactionAmount.text = "$ ${currentItem.amount}"
        holder.transactionCategory.text = currentItem.category
        holder.transactionDate.text = currentItem.date
        holder.deleteBtn.setOnClickListener {

            // Get the current element id
            val current = transactionArrayList[position].id
            // Delete the element in the database
            FirebaseFirestore.getInstance().collection("transactions").document(current)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
            // Update the list to reflect the change
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionOperation : TextView = itemView.findViewById(R.id.transaction_operations)
        val transactionAmount : TextView = itemView.findViewById(R.id.transaction_amount)
        val transactionCategory : TextView = itemView.findViewById(R.id.transaction_category)
        val transactionDate : TextView = itemView.findViewById(R.id.transaction_date)
        val deleteBtn : ImageButton = itemView.findViewById(R.id.delete_transaction)
    }

    private fun getTransactionIdData() {

        val userUid = FirebaseAuth.getInstance().currentUser!!.uid

        FirebaseFirestore.getInstance().collection("transactions")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "found ${document.id} => ${document.data["date"]}")
                    if (document.data["uid"].toString() == userUid) {
                        val newTransaction =
                            TransactionId(
                                document.id,
                            )
                        transactionArrayList.add(newTransaction)
                    }
                }

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

}