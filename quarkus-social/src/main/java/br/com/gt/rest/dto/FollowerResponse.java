package br.com.gt.rest.dto;

import br.com.gt.domain.model.Followers;
import lombok.Data;

@Data
public class FollowerResponse {

    private Long id;
    private String name;

    public FollowerResponse() {
    }

    public FollowerResponse(Followers obj) {
        this(obj.getId(), obj.getFollower().getName());
    }

    public FollowerResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
