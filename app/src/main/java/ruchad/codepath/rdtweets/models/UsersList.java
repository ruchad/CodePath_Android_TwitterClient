package ruchad.codepath.rdtweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "UsersList")
public class UsersList extends Model {

    @Column(name = "next_cursor")
    public long next_cursor;
    @Column(name = "previous_cursor")
    public int previous_cursor;

    @Column(name = "users")
    public List<UsersEntity> users;

    public static class UsersEntity {
        @Column(name = "id_str")
        public String idStr;
        @Column(name = "name")
        public String name;
        @Column(name = "screen_name")
        public String screen_name;
        @Column(name = "description")
        public String description;
        @Column(name = "profile_image_url")
        public String profile_image_url;
    }
}
