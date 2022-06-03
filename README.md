# Overview


{Provide a description the software that you wrote and how it integrates with a Cloud Database.  Describe how to use your program.}

{Describe your purpose for writing this software.}

{Provide a link to your YouTube demonstration.  It should be a 4-5 minute demo of the software running, a walkthrough of the code, and a view of the cloud database.}

[App Demo Video](https://youtu.be/S2m8b0olvZM)

# Cloud Database

I am using Firestore from Firebase. I am using the authentication function and the firestore to store transactions.  
I have two tables, "Users" and "Transactions" which are related through the userId key.

In the Users collection, we store the balance of the account, which is updated each transaction. We also store the account name and id.

In the Transactions collection, we store each transaction added through the UI. It stores the amount, category, date, type of operations, and which user has input the transaction. In this way, the app shows only the transaction that are related to a specific user. 

Data are fetched and stored into a map which is displayed as a RecyclerView in the Android app.
# Development Environment

* Android Studio
* Kotlin

# Useful Websites

{Make a list of websites that you found helpful in this project}
* [Firebase Official Website](https://firebase.google.com/)
* [Add Firebase To Your Android App](https://www.youtube.com/watch?v=K7QEyNVMxSA)
* [Google Firebase Authentication](https://www.youtube.com/watch?v=8I5gCLaS25w)
* [Firebase Docs](https://firebase.google.com/docs/firestore/)
* [Setup bottom navigation bar](https://www.youtube.com/watch?v=v8MbOjBCu0o)
* [How to use Date Picker](https://developer.android.com/reference/android/widget/DatePicker)
* [RecyclerView using Kotlin - Firebase](https://www.youtube.com/watch?v=VVXKVFyYQdQ)

# Future Work

* List transaction in ascending order according to newest date
* Improve the design of the transaction list
* Item 3
