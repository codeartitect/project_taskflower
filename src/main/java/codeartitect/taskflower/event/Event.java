package codeartitect.taskflower.event;

import codeartitect.taskflower.tag.model.Tag;
import codeartitect.taskflower.common.EntityUtil;
import codeartitect.taskflower.event.payload.SaveEventRequest;
import codeartitect.taskflower.common.BaseTimeEntity;
import codeartitect.taskflower.hashtag.Hashtag;
import codeartitect.taskflower.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Event")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Event extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Hashtag> hashtags = new ArrayList<>();

    @Column(name = "description")
    private String description;

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;

    @Column(name = "due_date_time")
    private LocalDateTime dueDateTime;

    @Column(name = "location")
    private String location;

    public Event(User user, SaveEventRequest saveEventRequest) {
        this.user = user;
        this.title = saveEventRequest.getTitle();
        this.tag = saveEventRequest.getTag();
        this.hashtags = EntityUtil.setListElements(saveEventRequest.getHashtags());
        this.description = saveEventRequest.getDescription();
        this.startDateTime = saveEventRequest.getStartDateTime();
        this.dueDateTime = saveEventRequest.getDueDateTime();
        this.location = saveEventRequest.getLocation();
    }

    @Override
    public ZoneId getUserZoneId() {
        return ZoneId.of(user.getZoneId());
    }

    public void update(SaveEventRequest saveEventRequest) {
        this.title = saveEventRequest.getTitle();
        this.tag = saveEventRequest.getTag();
        this.hashtags = EntityUtil.setListElements(saveEventRequest.getHashtags());
        this.description = saveEventRequest.getDescription();
        this.startDateTime = saveEventRequest.getStartDateTime();
        this.dueDateTime = saveEventRequest.getDueDateTime();
        this.location = saveEventRequest.getLocation();
    }
}
