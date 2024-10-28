package space.nanobreaker.cqrs;

public sealed interface Event
        permits Command, Query {

}
