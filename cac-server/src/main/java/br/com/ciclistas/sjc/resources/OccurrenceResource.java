package br.com.ciclistas.sjc.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import br.com.ciclistas.sjc.model.Occurrence;
import br.com.ciclistas.sjc.resources.utils.JaxrsUtils;


/**
 * @author Pedro Hos
 * @param <MultipartFormDataInput>
 *
 */

@Path("occurrences")
@Consumes("application/json; charset=UTF-8")
@Produces("application/json; charset=UTF-8")
public class OccurrenceResource {
	
	Logger logger =  Logger.getLogger(OccurrenceResource.class.getName());

	@ConfigProperty(name = "path.image.dir")
	String localPath;

	@ConfigProperty(name ="web.context")
	String webContext;

	@POST
	public Response newOccurrence(final Occurrence occurrence) {
		Occurrence.persist(occurrence);
		return Response.created(URI.create("/occurrences/" + occurrence.id)).build();
	}
	
	@POST
	@Consumes("multipart/form-data;charset=UTF-8")
	@Path(value = "/upload")
	public Response newOccurrenceWithUploads(MultipartFormDataInput multipart) throws IOException {
		
		logger.info("Saving new occurrence!");
		
		//logger.info("Is admin? " + securityContext.isUserInRole("admin"));

		Occurrence occurrence = getOccurence(multipart);
		
		Optional<List<InputPart>> photos = Optional.ofNullable(multipart.getFormDataMap().get("uploads"));
		
		if(photos.isPresent()) {
			List<String> urlImages = savePhoto(photos.get());
			occurrence.pathPhoto = urlImages;
		}
		
		Occurrence.persist(occurrence);
		
		return Response.created(URI.create("/occurrences/" + occurrence.id)).entity(occurrence).build();
	}

	private Occurrence getOccurence(MultipartFormDataInput multipart) throws IOException {
		Occurrence occurrence = new ObjectMapper().readValue(multipart.getFormDataPart("occurrence", String.class, null), Occurrence.class);
		return occurrence;
	}

	private List<String> savePhoto(List<InputPart> photos) {
		
		List<String> paths = new ArrayList<>();
		
		for (InputPart photo : photos) {
			
			String fileName = UUID.randomUUID() + ".jpeg";
			
			try {
				
				InputStream is = photo.getBody(InputStream.class, null);
				
				String pathAndFileName = localPath + "/" + fileName;
				Files.write(Paths.get(pathAndFileName), IOUtils.toByteArray(is));
				
				paths.add(webContext + "/images/" + fileName);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return paths;
	}
	
	@GET
	public Response allOccurrence() {
		return Response.ok().entity(JaxrsUtils.throw404IfNull(Occurrence.listAll())).build();
	}
	
	@GET
	@Path("/{id}")
	public Response findById(final Long id) {
		return Response.ok().entity(JaxrsUtils.throw404IfNull(Occurrence.findById(id))).build();
	}

}