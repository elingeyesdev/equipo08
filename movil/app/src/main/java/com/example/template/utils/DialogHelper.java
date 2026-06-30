package com.example.template.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.template.R;

public class DialogHelper {

    public interface ConfirmCallback {
        void onConfirm();
    }

    public static void showConfirmDialog(Context context, String title, String message, String confirmButtonText, ConfirmCallback callback) {
        if (context == null) return;

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null);
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvDialogMessage);
        Button btnCancel = dialogView.findViewById(R.id.btnDialogCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnDialogConfirm);

        if (title != null) tvTitle.setText(title);
        if (message != null) tvMessage.setText(message);
        if (confirmButtonText != null) btnConfirm.setText(confirmButtonText);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            if (callback != null) {
                callback.onConfirm();
            }
        });

        dialog.show();
    }

    public static void showInfoDialog(Context context, String title, String message, int iconResId, int iconTint) {
        if (context == null) return;

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null);
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvMessage = dialogView.findViewById(R.id.tvDialogMessage);
        Button btnCancel = dialogView.findViewById(R.id.btnDialogCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnDialogConfirm);
        android.widget.ImageView ivIcon = dialogView.findViewById(R.id.ivDialogIcon);
        View iconContainer = (View) ivIcon.getParent();

        if (title != null) tvTitle.setText(title);
        if (message != null) tvMessage.setText(message);
        
        btnCancel.setVisibility(View.GONE);
        
        btnConfirm.setText("Cerrar");
        btnConfirm.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0f172a")));
        
        if (iconResId != 0) {
            ivIcon.setImageResource(iconResId);
            ivIcon.setImageTintList(android.content.res.ColorStateList.valueOf(iconTint));
            iconContainer.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#f1f5f9")));
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnConfirm.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
