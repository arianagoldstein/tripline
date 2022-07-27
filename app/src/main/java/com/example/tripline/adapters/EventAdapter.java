package com.example.tripline.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripline.R;
import com.example.tripline.databinding.ItemEventBinding;
import com.example.tripline.models.Event;
import com.example.tripline.models.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    public static final String TAG = "EventAdapter";
    private Context context;
    private List<Event> events;

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventBinding binding = ItemEventBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemEventBinding binding;

        private Event event;
        private JSONArray photoArray;
        protected EventPhotoAdapter adapter;
        protected List<Photo> eventPhotos;

        public ViewHolder(ItemEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Event event) {
            binding.tvTripTitleEvent.setText(event.getTitle());
            binding.tvDescriptionEvent.setText(event.getDescription());

            String activityType = event.getActivityType();
            binding.tvActivityTypeEvent.setText(activityType);

            // displaying icon depending on the activity type of the event
            switch(activityType) {
                case ("restaurant"):
                    binding.ivIconEvent.setImageResource(R.drawable.restaurant_icon);
                    break;
                case ("hotel"):
                    binding.ivIconEvent.setImageResource(R.drawable.hotel_icon);
                    break;
                case ("activity"):
                    binding.ivIconEvent.setImageResource(R.drawable.activity_icon);
                    break;
                case ("tour"):
                    binding.ivIconEvent.setImageResource(R.drawable.tour_icon);
                    break;
                case ("event"):
                    binding.ivIconEvent.setImageResource(R.drawable.event_icon);
                    break;
                default:
                    binding.ivIconEvent.setImageResource(R.drawable.push_pin_icon);
                    break;
            }

            // setting up horizontal recyclerview for event photos
            eventPhotos = new ArrayList<>();
            adapter = new EventPhotoAdapter(context, eventPhotos);
            binding.rvEventImages.setAdapter(adapter);
            LinearLayoutManager llm = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            binding.rvEventImages.setLayoutManager(llm);

            photoArray = event.getPhotos();

            // looping through the event photos and getting their URLs
            for (int index = 0; index < photoArray.length(); index++) {
                String url = "";
                try {
                    JSONObject jsonObject = (JSONObject) (photoArray.get(index));
                    JSONObject innerObject = jsonObject.getJSONObject("image");
                    url = innerObject.getString("url");

                    // constructing a Photo object with this URL
                    Photo photo = new Photo(url);
                    eventPhotos.add(photo);

                } catch (JSONException ex) {
                    Log.e(TAG, "Error with JSONObject: ", ex);
                }
                Log.i(TAG, "url: " + url);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
