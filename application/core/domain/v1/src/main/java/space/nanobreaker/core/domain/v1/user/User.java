package space.nanobreaker.core.domain.v1.user;

import space.nanobreaker.ddd.AggregateRoot;

public class User extends AggregateRoot<Integer> {

    protected User(Integer integer) {
        super(integer);
    }
}
