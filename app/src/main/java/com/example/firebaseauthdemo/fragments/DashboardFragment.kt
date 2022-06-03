package com.example.firebaseauthdemo.fragments

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseauthdemo.R
import com.example.firebaseauthdemo.Transaction
import com.example.firebaseauthdemo.TransactionAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class DashboardFragment : Fragment() {

    private lateinit var transactionRecyclerview : RecyclerView
    private lateinit var transactionArrayList : ArrayList<Transaction>
    private lateinit var db : FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        val userUid = auth.currentUser!!.uid
        val userBalanceText: TextView = requireView().findViewById(R.id.user_balance_main)
        val accountNameText: TextView = requireView().findViewById(R.id.welcome_text)

        val docRef = db.collection("users").document(userUid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "exist: ${document.get("balance")}")
                    userBalanceText.text = "$${document.getDouble("balance").toString()}"
                    accountNameText.text = document.getString("accountName")

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }

        transactionRecyclerview = requireView().findViewById(R.id.trans_list)
        transactionRecyclerview.layoutManager = LinearLayoutManager(context)
        transactionRecyclerview.setHasFixedSize(true)

        transactionArrayList = arrayListOf<Transaction>()
        getUserData(userUid)
    }

    private fun getUserData(userUid: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("transactions")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "found ${document.id} => ${document.data["date"]}")
                    if (document.data["uid"].toString() == userUid) {
                        val newTransaction =
                            Transaction(
                                document.data["amount"].toString().toDouble(),
                                document.data["category"].toString(),
                                document.data["date"].toString(),
                                document.data["operations"].toString()
                            )
                        transactionArrayList.add(newTransaction)
                    }
                }
                val adapter = TransactionAdapter(transactionArrayList)
                transactionRecyclerview.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
        }
    }

}