package br.com.ciclistas.sjc.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.ciclistas.sjc.repository.StatusRepository;
import br.com.ciclistas.sjc.resources.utils.JaxrsUtils;



/**
 * @author Pedro Hos
 *
 */

@Path("status")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StatusResource {
	
	@Inject
	StatusRepository statusRepository;
	
	@GET
	public Response getAllStatus() {
		return Response.ok(JaxrsUtils.throw404IfNull(statusRepository.listAll())).build();
	}
	
}
