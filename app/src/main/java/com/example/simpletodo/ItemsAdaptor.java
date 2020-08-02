package com.example.simpletodo;
import android.view.LayoutInflater;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemsAdaptor extends RecyclerView.Adapter<ItemsAdaptor.ViewHolder> {
    public interface onLongClickListener{//interface is used to communicate with mainActivity.java and inform us what position was tapped
        void onItemLongClicked(int position);
    }
    public interface onClickListener{//interface干嘛的
        void onItemClicked(int position);
    }
    ArrayList<String> items;
    onLongClickListener longClickListener;
    onClickListener clickListener;
    public ItemsAdaptor(ArrayList<String> items, onLongClickListener longClickListener, onClickListener clickListener) {
        this.items = items;
        this.longClickListener = longClickListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        //Use layout inflater to inflate a view
        boolean attachToRoot = false;
        View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, attachToRoot);//goto declaration or usage
        //wrap it inside a view holder and return it
        return new ViewHolder(todoView);
    }
    //binding data to a particular viewholder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        //grab the item at the position
        String item = items.get(position);
        //bind the item into the specific view holder
        holder.bind(item);
    }
    //tell the recyclerview how many items are in the list
    @Override
    public int getItemCount(){
        return items.size();
    }
    //container to provide easy access to views that represent each row of th list
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(android.R.id.text1);//get a reference of the view
        }
        //update the view inside the view holder with this data
        public void bind(String item) {
            tvItem.setText(item);
            tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(getAdapterPosition());
                }
            });
            tvItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //Notify the listener which position is long clicked
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;//the callback is consuming the long click
                }
            });//why );
        }
    }
}
