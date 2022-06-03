package com.example.firebaseauthdemo.fragments

import android.app.PendingIntent.getActivity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.firebaseauthdemo.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class OperationsFragment : Fragment() {
    private var rootView: View? = null
    override fun onResume() {
        super.onResume()
        val operations = resources.getStringArray(R.array.operations)
        val categories = resources.getStringArray(R.array.categories)

        val arrayAdapterOperations = ArrayAdapter(requireContext(), R.layout.dropdown_item, operations)
        val arrayAdapterCategories = ArrayAdapter(requireContext(), R.layout.dropdown_item, categories)

        val operationsView: AutoCompleteTextView = rootView!!.findViewById(R.id.op_type)
        val categoriesView: AutoCompleteTextView = rootView!!.findViewById(R.id.category_type)

        operationsView.setAdapter(arrayAdapterOperations)
        categoriesView.setAdapter(arrayAdapterCategories)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_operations, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        var date = ""

        val userUid = auth.currentUser!!.uid
        val btnAdd: Button = requireView().findViewById(R.id.btn_add)
        val opField: AutoCompleteTextView = requireView().findViewById(R.id.op_type)
        val catField: AutoCompleteTextView = requireView().findViewById(R.id.category_type)
        val amount: TextInputEditText = requireView().findViewById(R.id.amount_input)
        val picker: DatePicker = requireView().findViewById(R.id.picker)
        val today = Calendar.getInstance()
        picker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)) {
                view, year, month, day ->
            val month = month + 1
            date = "$month/$day/$year"
        }
        val dashboardFragment = DashboardFragment()


        btnAdd.setOnClickListener {
            when {
                TextUtils.isEmpty(date) -> {
                    Toast.makeText(
                        context,
                        "Please choose a date.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    // Save info on the database
                    val transactionInfo: MutableMap<String, Any>  = HashMap()
                    transactionInfo["uid"] = userUid
                    transactionInfo["operations"] = opField.text.toString()
                    transactionInfo["category"] = catField.text.toString()
                    transactionInfo["amount"] = amount.text.toString().toDouble()
                    transactionInfo["date"] = date

                    // Save transaction on the db
                    db.collection("transactions").document()
                        .set(transactionInfo)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Transaction recorded to the database.",
                                Toast.LENGTH_SHORT
                            ).show()
                            replaceFragment(dashboardFragment)
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Transaction not recorded to the database.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    // Get current balance

                    val docRef = db.collection("users").document(userUid)
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                Log.d(ContentValues.TAG, "existFrg: ${document.get("balance")}")
                                var finalBalance = document.getDouble("balance")
                                if (finalBalance != null) {
                                    if(opField.text.toString() == "Deposit") {
                                        finalBalance += amount.text.toString().toDouble()
                                    } else {
                                        finalBalance -= amount.text.toString().toDouble()
                                    }
                                    // Update current balance
                                    db.collection("users").document(userUid)
                                        .update("balance", finalBalance)
                                        .addOnSuccessListener { Log.d(TAG, "Balance successfully updated!") }
                                        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                                }
                            } else {
                                Log.d(ContentValues.TAG, "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(ContentValues.TAG, "get failed with ", exception)
                        }

                    }
                }

            }
        }
    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}

