package ruchad.codepath.rdtweets.adapters;

import com.bumptech.glide.Glide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import ruchad.codepath.rdtweets.R;
import ruchad.codepath.rdtweets.models.UsersList;

//ToDo: Implement viewholder
public class UsersListAdapter extends ArrayAdapter<UsersList.UsersEntity> {

    public UsersListAdapter(Context context, List<UsersList.UsersEntity> objects) {
        super(context,android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UsersList.UsersEntity user = getItem(position);

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        ImageView ivProfilePic = ButterKnife.findById(convertView, R.id.ivProfilePicture);
        TextView tvUsername = ButterKnife.findById(convertView, R.id.tvUsername);
        TextView tvUserHandle = ButterKnife.findById(convertView, R.id.tvUserHandle);
        TextView tvTagline = ButterKnife.findById(convertView, R.id.tvTagLine);

        Glide.with(ivProfilePic.getContext()).load(user.profile_image_url).into(ivProfilePic);
        tvUsername.setText(user.name);
        tvUserHandle.setText("@" + user.screen_name);
        tvTagline.setText(user.description);

        return convertView;
    }
}
