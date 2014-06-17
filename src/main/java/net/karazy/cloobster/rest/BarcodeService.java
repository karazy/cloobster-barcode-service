package net.karazy.cloobster.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;







import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.karazy.cloobster.controller.BarcodeController;

/**
 * @author Frederik Reifschneider
 *
 */
@Path("barcodes")
public class BarcodeService {
	
	@Context
	private HttpServletRequest request;
	
	public final BarcodeController ctrl;
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
		
	public BarcodeService() {
		ctrl = new BarcodeController();
	}
	
//	@GET
//	@Produces(MediaType.TEXT_PLAIN)
//	public String testApi() {
//		return "Works"; 
//	}
	
	
	/**
	 * @param response
	 * @param code
	 * @param type
	 * @return
	 * @throws IOException
	 */
	@GET
	@Produces("image/png")
	@Path("{type}/{code}")
	public Response generateBarcode(@Context HttpServletResponse response, @PathParam("code") String code, @PathParam("type") String type) throws IOException {
		
		log.info("Got request to generate barcode from " + request.getRemoteHost());		  	
		
		byte[] bData = null;
		
		response.setContentType("image/png");
		
		bData = ctrl.generateBarcode(code, type, "300");		 
				
		if(bData == null) {
			return Response.serverError().build();
		}
		
		response.setContentLength(bData.length);
		response.getOutputStream().write(bData);
		response.flushBuffer();
		
		return Response.ok().build();
	}

}
