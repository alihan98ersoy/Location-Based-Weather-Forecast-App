package com.alihan98ersoy.locationbasedweather.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alihan98ersoy.locationbasedweather.R;
import com.alihan98ersoy.locationbasedweather.services.CurrentWeatherUtils;

import java.util.ArrayList;

public class CardArrayAdapter extends RecyclerView.Adapter<CardArrayAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Card> cardlist;
    private Context context;

    public CardArrayAdapter(Context ctx, ArrayList<Card> cardlist){

        inflater = LayoutInflater.from(ctx);
        this.cardlist = cardlist;
        context=ctx;
    }

    @Override
    public CardArrayAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.daily_weather_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(CardArrayAdapter.MyViewHolder holder, int position) {

        // holder.iv.setImageResource(imageModelArrayList.get(position).getImage_drawable());
        // holder.time.setText(imageModelArrayList.get(position).getName());
        Card card = cardlist.get(position);
        holder.weathericon.setImageResource(CurrentWeatherUtils.getWeatherIconResId(card.getIcon()));
        holder.condition.setText(card.getWeathercondision());
        holder.temperature.setText(card.getCelcius()+"°C");
        holder.location_date.setText(card.getCity());
    }

    @Override
    public int getItemCount() {
        return cardlist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView condition,temperature,location_date;
        ImageView weathericon;


        public MyViewHolder(View itemView) {
            super(itemView);
            weathericon = (ImageView) itemView.findViewById(R.id.weather_condition_icon);
            condition = (TextView) itemView.findViewById(R.id.weather_condition);
            temperature = (TextView) itemView.findViewById(R.id.temperature);
            location_date = (TextView) itemView.findViewById(R.id.location);


            //time = (TextView) itemView.findViewById(R.id.tv);
            //iv = (ImageView) itemView.findViewById(R.id.iv);
        }

    }
}
    /*
    public CardArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

    }

    @Override
    public void add(Card object) {
        cardList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.cardList.size();
    }

    @Override
    public Card getItem(int index) {
        return this.cardList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CardViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.daily_weather_item, parent, false);
            viewHolder = new CardViewHolder();
            viewHolder.line1 = (TextView) row.findViewById(R.id.line1);
            row.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder)row.getTag();
        }
        Card card = getItem(position);
       // viewHolder.line1.setText(card.getLine1());
        //viewHolder.line2.setText(card.getLine2());
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
*/
