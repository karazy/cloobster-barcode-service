package net.karazy.cloobster.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
		
	public BarcodeService() {
		ctrl = new BarcodeController();
	}
	
//	@GET
//	@Produces(MediaType.TEXT_PLAIN)
//	public String testApi() {
//		return "Works"; 
//	}
	
	//TODO add log4j
	//TODO throw correct error codes
	//TODO add guice? Maybe overkill for this extremely small use case!
	
	
	@GET
	@Produces("image/png")
	@Path("{code}")
	public Response generateBarcode(@Context HttpServletResponse response, @PathParam("code") String code, @QueryParam("type") String type) throws IOException {
		
		log.info("Got request to generate barcode from " + request.getRemoteHost());
		
		byte[] bData = null;
		
		response.setContentType("image/png");
		
		
		try {
			bData = ctrl.generateBarcode(code, type, "300");
		} catch (IOException e) { 
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		if(bData == null) {
			return Response.serverError().build();
		}
		
		response.setContentLength(bData.length);
		response.getOutputStream().write(bData);
		response.flushBuffer();
		
		return Response.ok().build();
	}

}
