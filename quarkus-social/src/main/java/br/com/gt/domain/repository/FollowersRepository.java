package br.com.gt.domain.repository;

import br.com.gt.domain.model.Followers;
import br.com.gt.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FollowersRepository implements PanacheRepository<Followers> {

    public boolean follows(User follower, User user){
        var params = Parameters.with("follower", follower)
                .and("user", user).map();

        PanacheQuery<Followers> query = find("follower = :follower and user = :user", params);
        Optional<Followers> result = query.firstResultOptional();
        return result.isPresent();
    }

    public List<Followers> findByUser(Long userId){
        PanacheQuery<Followers> query = find("user.id", userId);
        return query.list();
    }

    public List<Followers> findByFollower(Long followerId){
        PanacheQuery<Followers> query = find("follower.id", followerId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId){
        var params = Parameters.with("userId", userId)
                .and("followerId", followerId)
                .map();

        delete("follower.id = :followerId and user.id = :userId", params);
    }
}
