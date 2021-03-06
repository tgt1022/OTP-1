package com.mrcornman.otp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.models.MatchItem;
import com.mrcornman.otp.models.gson.PhotoFile;
import com.mrcornman.otp.models.models.PhotoItem;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.utils.PrettyNumbers;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClientMatchAdapter extends BaseAdapter {

    private Context mContext;
    private List<MatchItem> mItems;

    public ClientMatchAdapter(Context context) {
        mContext = context;
        mItems = new ArrayList<>();
    }

    public void addMatch(MatchItem match) {
        mItems.add(match);
        notifyDataSetChanged();
    }

    public void clearMatches() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public MatchItem getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_match_client, parent, false);
        }

        // init views
        final TextView nameText = (TextView) convertView.findViewById(R.id.name_text);
        final TextView countText = (TextView) convertView.findViewById(R.id.count_text);

        final ImageView thumbImage = (ImageView) convertView.findViewById(R.id.thumb_image);

        MatchItem match = getItem(position);

        String currId = ParseUser.getCurrentUser().getObjectId();
        String otherId = match.getFirstId().equals(currId) ? match.getSecondId() : match.getFirstId();

        convertView.setTag(otherId);
        countText.setText(PrettyNumbers.formatInteger(match.getNumLikes()));

        DatabaseHelper.getUserById(otherId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    nameText.setText(parseUser.getString(ProfileBuilder.PROFILE_KEY_NAME));

                    List<PhotoItem> photoItems = parseUser.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);
                    if(photoItems != null && photoItems.size() > 0 && photoItems.get(0) != null && photoItems.get(0) != JSONObject.NULL) {
                        PhotoItem mainPhoto = photoItems.get(0);
                        mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                            @Override
                            public void done(PhotoItem photoItem, ParseException e) {
                                if(photoItem != null && e == null) {
                                    PhotoFile mainFile = photoItem.getPhotoFiles().get(0);
                                    Picasso.with(mContext).load(mainFile.url).fit().centerCrop().into(thumbImage);
                                }
                            }
                        });
                    } else {
                        Picasso.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_portrait).fit().centerCrop().into(thumbImage);
                    }
                }
            }
        });

        return convertView;
    }
}
