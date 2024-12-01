package com.example.pocketbudget.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pocketbudget.DBHandler;
import com.example.pocketbudget.R;
import com.example.pocketbudget.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DBHandler dbHandler;
    private float currentBalance;
    private float dailyBudget;

    private EditText amountEditText;
    private AutoCompleteTextView categoryAutoComplete;
    private Button logButton;
    private TextView budgetTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        amountEditText = root.findViewById(R.id.amount);
        categoryAutoComplete = root.findViewById(R.id.category);
        logButton = root.findViewById(R.id.log);
        budgetTextView = root.findViewById(R.id.budget);

        dbHandler = new DBHandler(getActivity());

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category = categoryAutoComplete.getText().toString();

                if (category.isEmpty()) {
                    category = "uncategorized";
                }

                String amount = amountEditText.getText().toString();
                Date date = new Date();

                if (amount.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amountDouble = Double.parseDouble(amount);

                dbHandler.addExpense(category, amountDouble, date);

                Toast.makeText(getActivity(), "Expense logged", Toast.LENGTH_SHORT).show();
                amountEditText.setText("");
                categoryAutoComplete.setText("");
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        if (dbHandler != null) {
            dbHandler.close();
            dbHandler = null;
        }
        super.onDestroyView();
        binding = null;
    }
}