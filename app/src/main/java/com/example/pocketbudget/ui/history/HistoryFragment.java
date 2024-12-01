package com.example.pocketbudget.ui.history;

import static com.example.pocketbudget.DBHandler.COL_AMOUNT;
import static com.example.pocketbudget.DBHandler.COL_CATEGORY_NAME;
import static com.example.pocketbudget.DBHandler.COL_DATE;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketbudget.DBHandler;
import com.example.pocketbudget.ExpenseAdapter;
import com.example.pocketbudget.R;
import com.example.pocketbudget.databinding.FragmentHistoryBinding;

import java.util.Date;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private DBHandler dbHandler;
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HistoryViewModel historyViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dbHandler = new DBHandler(requireContext());
        recyclerView = root.findViewById(R.id.expensesRecyclerView);

        Date currentDate = new Date();
        int currentYear = currentDate.getYear() + 1900;
        int currentMonth = currentDate.getMonth() + 1;

        displayExpenses(currentYear, currentMonth);

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

    private void displayExpenses(int year, int month) {
        Cursor cursor = dbHandler.getExpensesForMonth(year, month);

        ExpenseAdapter adapter = new ExpenseAdapter(requireContext(), cursor);
        recyclerView.setAdapter(adapter);
    }
}