package com.likefool.apps.aliketweetr.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aliku on 2015/8/13.
 */
@Table(name = "Users")
public class User extends Model {
    @Column(name = "Name")
    private String name;
    @Column(name = "Uid")
    private long uid;
    @Column(name = "ScreenName")
    private String screenName;
    @Column(name = "ProfileImageUrl")
    private String profileImageUrl;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public static User fromJSON(JSONObject json){
        User u = new User();
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");
            u.save(); // insert into sqllite
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }
}
