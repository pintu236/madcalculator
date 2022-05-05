package com.demo.madcalculator

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.demo.madcalculator.adapter.HistoryAdapter
import com.demo.madcalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var historyListAdapter: HistoryAdapter
    private var isHistory: Boolean = false;
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java];

        addNumberListeners();
        addOperationListeners();
        initHistoryAdapter();
        viewModel.operationLiveData.observe(this) { text ->
//            binding.textCalculated.visibility = View.VISIBLE
            binding.textCalculated.text = text
            binding.inputCalculator.setText(text)
            //Update History
            historyListAdapter.updateList(viewModel.lastResultsStack)

        }
        viewModel.inputLiveData.observe(this) { response ->
            binding.inputCalculator.append(response.text);

        }
        binding.operationClear.setOnClickListener {
            binding.inputCalculator.setText("");
            binding.textCalculated.visibility = View.GONE
        }
        binding.operationHistory.setOnClickListener {
            isHistory = !isHistory;

            val layoutParams = binding.historyList.layoutParams as LinearLayout.LayoutParams;
            layoutParams.weight = if (isHistory) 1F else 0F

            binding.historyList.layoutParams = layoutParams;
        }
        binding.operationUndo.setOnClickListener {
            if (binding.inputCalculator.text?.isNotEmpty() == true) {
                binding.inputCalculator.setText(
                    binding.inputCalculator.text.toString().substring(
                        0,
                        binding.inputCalculator.text?.length?.minus(1) ?: 0
                    )
                )
            }

        }
    }

    private fun initHistoryAdapter() {
        historyListAdapter = HistoryAdapter(this);
        binding.historyList.adapter = historyListAdapter;
        // Check Logged in and add data
        viewModel.getOperations();
        viewModel.loggedInResultsLiveData.observe(this) { response ->
            when (response.status) {
                NetworkResponse.Status.SUCCESS -> {
                    response.response?.let { historyListAdapter.updateList(it) }
                }
            }
        }

    }

    private fun addOperationListeners() {
        binding.operationPlus.setOnClickListener {
            viewModel.doOperation("+", OperationType.ADDITION)
        }
        binding.operationMinus.setOnClickListener {
            viewModel.doOperation("-", OperationType.SUBTRACTION)
        }
        binding.operationMultiply.setOnClickListener {
            viewModel.doOperation("x", OperationType.MULTIPLY)
        }
        binding.operationDivide.setOnClickListener {
            viewModel.doOperation("/", OperationType.DIVISION)
        }

        binding.operationEqual.setOnClickListener {
            if (binding.inputCalculator.text?.isEmpty() == true) return@setOnClickListener
            viewModel.equalOperation(binding.inputCalculator.text.toString().trim())
        }
    }

    private fun addNumberListeners() {
        binding.number7.setOnClickListener {
            viewModel.setInput(getString(R.string.char_7))
        }
        binding.number8.setOnClickListener {
            viewModel.setInput(getString(R.string.char_8))
        }
        binding.number9.setOnClickListener {
            viewModel.setInput(getString(R.string.char_9))
        }
        binding.number4.setOnClickListener {
            viewModel.setInput(getString(R.string.char_4))
        }
        binding.number5.setOnClickListener {
            viewModel.setInput(getString(R.string.char_5))
        }
        binding.number6.setOnClickListener {
            viewModel.setInput(getString(R.string.char_6))
        }
        binding.number1.setOnClickListener {
            viewModel.setInput(getString(R.string.char_1))
        }
        binding.number2.setOnClickListener {
            viewModel.setInput(getString(R.string.char_2))
        }

        binding.number3.setOnClickListener {
            viewModel.setInput(getString(R.string.char_3))
        }
        binding.number00.setOnClickListener {
            viewModel.setInput(getString(R.string.char_00))
        }
        binding.number0.setOnClickListener {
            viewModel.setInput(getString(R.string.char_0))
        }

    }
}