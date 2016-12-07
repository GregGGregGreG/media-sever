package edu.greg.telesens.server.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by
 * GreG on 11/8/2016.
 */
public class StopCmd extends ResourceSupport {

    private final String content;

    @JsonCreator
    public StopCmd(@JsonProperty("content") String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
