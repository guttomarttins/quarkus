package br.com.gt.rest.dto;

import br.com.gt.domain.model.Post;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {

    private String text;
    private LocalDateTime dateTime;

    public static PostResponse fromEntity(Post post){
        var response = new PostResponse();
        response.setText(post.getText());
        response.setDateTime(post.getDateTime());
        return response;
    }
}
