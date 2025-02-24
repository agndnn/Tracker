package com.example.tracker;
import static com.example.tracker.Params.usersOut;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.google.android.ads.mediationtestsuite.viewmodels.HeaderViewHolder;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    // private ArrayList<User>  users;
    private Context context;


    public UserAdapter(Context context, ArrayList<User> users) {
       // this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                   .inflate(R.layout.header_item_user, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_user, parent, false);
            return new UserViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            // Заголовок не требует дополнительной логики
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            ((HeaderViewHolder)holder).addButton.setOnClickListener(v-> showAddUserDialog());
        } else {
            User user = Params.usersOut.get(position - 1); // смещение на 1, чтобы учесть заголовок
            UserViewHolder userViewHolder = (UserViewHolder) holder;
            userViewHolder.userCodeTextView.setText(user.getCode());
            userViewHolder.phoneNumberTextView.setText(user.getPhone());
            holder.itemView.setOnClickListener(v -> showEditDialog(user, position));

            // Устанавливаем обработчик нажатия на кнопку удаления
            ((UserViewHolder) holder).deleteButton.setOnClickListener(v -> showDeleteDialog(position));
//            ((UserViewHolder) holder).deleteButton.setOnLongClickListener(v -> {
//                showDeleteDialog(position-1);
//                return true; // Возвращаем true, чтобы предотвратить дальнейшее обработку события
//            });
            // Устанавливаем обработчик нажатия на кнопку удаления для этого пользователя
  //          ((UserViewHolder) holder).deleteButton.setOnClickListener(v -> showDeleteDialog(position-1));

        }
    }

    @Override
    public int getItemCount() {
        return Params.usersOut.size() + 1; // +1 для заголовка
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView addButton; // Кнопка удаления

        HeaderViewHolder(View itemView) {
            super(itemView);
            addButton = itemView.findViewById(R.id.button_add_user);

            // Укажите здесь, если нужно, элементы заголовка, если они будут динамическими
        }
    }

    // Метод для показа диалога редактирования
    private void showEditDialog(User user, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View editView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_user, null);

        EditText editUserCode = editView.findViewById(R.id.edit_user_code);
        EditText editPhoneNumber = editView.findViewById(R.id.edit_phone_number);

        // Заполняем поля текущими данными
        editUserCode.setText(user.getCode());
        editPhoneNumber.setText(user.getPhone());

        builder.setTitle(R.string.edit_user_header)
                .setView(editView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Сохраняем введенные данные
                    String updatedUserId = editUserCode.getText().toString();
                    String updatedPhoneNumber = editPhoneNumber.getText().toString();

                    // Обновляем данные в массиве пользователей
                    user.setCode(updatedUserId);
                    user.setPhone(updatedPhoneNumber);
                    notifyItemChanged(position); // Обновляем конкретный элемент списка
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View addView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_user, null);

        EditText editUserCode = addView.findViewById(R.id.edit_user_code);
        EditText editPhoneNumber = addView.findViewById(R.id.edit_phone_number);

        builder.setTitle(R.string.add_user_header)
                .setView(addView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String newUserId = editUserCode.getText().toString();
                    String newPhoneNumber = editPhoneNumber.getText().toString();

                    // Создаем нового пользователя и добавляем его в список
                    User newUser = new User(newUserId, newPhoneNumber);
                    usersOut.add(newUser);
                    //userAdapter.notifyItemInserted(usersOut.size() - 1); // Уведомляем адаптер о добавлении
                    notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    // Метод для показа диалога удаления
    @SuppressLint("NotifyDataSetChanged")
    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_user_header)
                .setMessage(R.string.delete_user_msg)
                .setPositiveButton(R.string.yes_label, (dialog, which) -> {
                    Params.usersOut.remove(position-1); // Удаляем пользователя из списка
                    //notifyItemRemoved(position); // Уведомляем адаптер об удалении
                    // notifyItemRangeChanged(position, Params.usersOut.size()); // Обновляем оставшиеся элементы
                    // Принудительное обновление всего адаптера
                    notifyDataSetChanged();
                    Toast.makeText(context, "Пользователь удален", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no_label, null);

        builder.create().show();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userCodeTextView;
        TextView phoneNumberTextView;
        ImageView deleteButton; // Кнопка удаления
        UserViewHolder(View itemView) {
            super(itemView);
            userCodeTextView = itemView.findViewById(R.id.user_code);
            phoneNumberTextView = itemView.findViewById(R.id.user_phone);
            deleteButton = itemView.findViewById(R.id.button_delete_user); //
        }
    }
}