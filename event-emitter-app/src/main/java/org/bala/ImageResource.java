package org.bala;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/stream")
public class ImageResource {

    @Inject
    ImagePoster poster;

    @POST
    @Path("/image")
    @Produces(MediaType.TEXT_PLAIN)
    public void streamImage(String imgData) {
        poster.process(imgData); // send the base64 encoded image to kafka
    }
}