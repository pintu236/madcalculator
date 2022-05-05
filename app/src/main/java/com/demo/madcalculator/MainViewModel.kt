package com.demo.madcalculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap


class MainViewModel : ViewModel() {

    val operatorMap = mapOf(
        "x" to 4,
        "+" to 3,
        "/" to 2,
        "-" to 1,
        "" to 0
    )

    val operationStack = Stack<String>();
    val numbersStack = Stack<Float>();
    var lastResultsStack = Stack<CalculationResult>();

    val inputLiveData = MutableLiveData<CalcOperation>();
    val operationLiveData = MutableLiveData<String>();
    val loggedInResultsLiveData =
        MutableLiveData<NetworkResponse<MutableList<CalculationResult>?>>();


    val mFirestoreDataBase = FirebaseFirestore.getInstance();
    val mFirebaseAuth = FirebaseAuth.getInstance();
    var currentUser: FirebaseUser? = null;

    init {
        currentUser = mFirebaseAuth.currentUser;
    }

    fun setInput(text: String) {
        inputLiveData.postValue(CalcOperation(text, OperationType.NUMBER))
    }

    fun doOperation(operation: String, operationType: OperationType) {
        inputLiveData.postValue(CalcOperation(operation, operationType))
    }

    fun equalOperation(text: String) {

//        text.split(Regex("[0-9]")).forEach {
//            if (it.isNotEmpty()) {
//                operationStack.push(it)
//            }
//        }
//        operationStack.reverse()

        var i = 0;
        //Iterate through text
        while (i < text.length) {
            try {
                //Get number from sequence
                val parsedNumber = parseNumber(text, i);
                numbersStack.push(parsedNumber.toFloat())
                //Go on in sequence
                i += parsedNumber.toString().length;
                if (i >= text.length) {
                    break
                }
                //Get Operator from sequence
                val nextOperator = parseOperator(text, i);
                doCalculation(nextOperator);
                operationStack.push(nextOperator)
                i++;
            } catch (e: NumberFormatException) {

            }

        }
        doCalculation("")
        if (numbersStack.size == 1 && operationStack.size == 0) {
            val result = numbersStack.pop().toString();
            lastResultsStack.push(CalculationResult(text, result))
            operationLiveData.postValue(result);
            //Write to db
            val mapOfResult = hashMapOf<String, Any>()
            var results = mutableListOf<CalculationResult>()
            lastResultsStack.forEach {
                results.add(it)
            }
            mapOfResult["results"] = results;
            mFirestoreDataBase.collection("users")
                .document(currentUser?.uid.toString())
                .set(mapOfResult, SetOptions.merge())


        }
        numbersStack.clear()
        operationStack.clear()
    }


    private fun doCalculation(nextOperator: String) {
        while (numbersStack.size >= 2 && operationStack.size >= 1) {
            if (operatorMap[nextOperator]!! <= operatorMap[operationStack.peek()]!!) {
                val second: Float = numbersStack.pop()
                val first: Float = numbersStack.pop()
                val op: String = operationStack.pop()
                val result: Float = applyOperation(first, op, second)
                numbersStack.push(result)
            } else {
                break
            }
        }
    }

    private fun applyOperation(
        left: Float,
        op: String,
        right: Float
    ): Float {
        return when (op) {
            "+" -> left + right
            "-" -> left - right
            "x" -> left * right
            "/" -> left / right
            else -> right
        }
    }


    private fun parseOperator(
        sequence: String,
        offset: Int
    ): String {
        if (offset < sequence.length) {
            when (sequence[offset]) {
                '+' -> return "+"
                '-' -> return "-"
                'x' -> return "x"
                '/' -> return "/"
            }
        }
        return "";
    }

    private fun parseNumber(sequence: String, offset: Int): Int {
        var offset = offset
        val sb = StringBuilder()
        while (offset < sequence.length && Character.isDigit(sequence[offset])) {
            sb.append(sequence[offset])
            offset++
        }
        return sb.toString().toInt()
    }

    fun getOperations() {
        if (currentUser != null) {
            loggedInResultsLiveData.postValue(NetworkResponse.loading())
            mFirestoreDataBase.collection("users").document(currentUser?.uid.toString())
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        val snapshotResult = it.result
                        if (snapshotResult.exists() && snapshotResult.data?.get("results") != null) {
                            try {
                                val mutableList =
                                    snapshotResult.data?.get("results") as MutableList<HashMap<String, Any>>;
                                mutableList.forEach { result ->
                                    lastResultsStack.push(
                                        CalculationResult(
                                            result["input"].toString(),
                                            result["result"].toString()
                                        )
                                    )
                                }
                                loggedInResultsLiveData.postValue(
                                    NetworkResponse.success(
                                        lastResultsStack
                                    )
                                )
                            } catch (e: Exception) {
                                loggedInResultsLiveData.postValue(NetworkResponse.error(e.message))
                            }
                        } else {
                            loggedInResultsLiveData.postValue(NetworkResponse.error("No data"))
                        }
                    } else {
                        loggedInResultsLiveData.postValue(NetworkResponse.error(it.exception?.message))
                    }
                }

        }
    }


}