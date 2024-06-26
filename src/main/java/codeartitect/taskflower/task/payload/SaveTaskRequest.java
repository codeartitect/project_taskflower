package codeartitect.taskflower.task.payload;

import codeartitect.taskflower.tag.model.Tag;
import codeartitect.taskflower.event.Event;
import codeartitect.taskflower.flow.Flow;
import codeartitect.taskflower.hashtag.Hashtag;
import codeartitect.taskflower.task.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SaveTaskRequest {
    private String title;
    private Flow flow;
    private Event event;
    private Tag tag;
    private List<Hashtag> hashtags;
    private String description;
    private Status status;
}
