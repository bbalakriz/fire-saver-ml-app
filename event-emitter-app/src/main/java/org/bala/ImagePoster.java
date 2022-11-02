package org.bala;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import io.smallrye.reactive.messaging.kafka.Record;

@ApplicationScoped
public class ImagePoster {

    @Inject
    @Channel("images")
    private Emitter<Record<Long, String>> emitter;

    // send a <k,v> pair of <uniqueid,b64encodedimage>
    // to kafka topic "images"
    public void process(String imgData) {
        emitter.send(Record.of(System.currentTimeMillis(), imgData));
    }

}