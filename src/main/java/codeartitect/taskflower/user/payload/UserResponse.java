package codeartitect.taskflower.user.payload;

import codeartitect.taskflower.user.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserResponse {
    private Long id;
    private String username;
    private String zoneId;
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.zoneId = user.getZoneId();
    }
}
