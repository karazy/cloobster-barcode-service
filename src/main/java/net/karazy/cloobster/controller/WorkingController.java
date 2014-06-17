package net.karazy.cloobster.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.eps.EPSCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkingController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
	

    private static final long serialVersionUID = -1612710758060435089L;

    /** Parameter name for the message */
    public static final String BARCODE_MSG                 = "msg";
    /** Parameter name for the barcode type */
    public static final String BARCODE_TYPE                = "type";
    /** Parameter name for the barcode height */
    public static final String BARCODE_HEIGHT              = "height";
    /** Parameter name for the module width */
    public static final String BARCODE_MODULE_WIDTH        = "mw";
    /** Parameter name for the wide factor */
    public static final String BARCODE_WIDE_FACTOR         = "wf";
    /** Parameter name for the quiet zone */
    public static final String BARCODE_QUIET_ZONE          = "qz";
    /** Parameter name for the human-readable placement */
    public static final String BARCODE_HUMAN_READABLE_POS  = "hrp";
    /** Parameter name for the output format */
    public static final String BARCODE_FORMAT              = "fmt";
    /** Parameter name for the image resolution (for bitmaps) */
    public static final String BARCODE_IMAGE_RESOLUTION    = "res";
    /** Parameter name for the grayscale or b/w image (for bitmaps) */
    public static final String BARCODE_IMAGE_GRAYSCALE     = "gray";
    /** Parameter name for the font size of the human readable display */
    public static final String BARCODE_HUMAN_READABLE_SIZE = "hrsize";
    /** Parameter name for the font name of the human readable display */
    public static final String BARCODE_HUMAN_READABLE_FONT = "hrfont";
    /** Parameter name for the pattern to format the human readable message */
    public static final String BARCODE_HUMAN_READABLE_PATTERN = "hrpattern";


//    private transient Logger log = new ConsoleLogger(ConsoleLogger.LEVEL_INFO);

    /**
     * @throws Throwable 
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    public byte[] generateBarcode(String data, String type, String ress)
                throws Throwable {

        try {
//            String format = determineFormat(request);
        	String format = MimeTypes.MIME_PNG;
            int orientation = 0;

            Configuration cfg = buildCfg(type, null, null);
            
            String msg = data;
            if (msg == null) {
                msg = "0123456789";
            }

            BarcodeUtil util = BarcodeUtil.getInstance();
            BarcodeGenerator gen = util.createBarcodeGenerator(cfg);

            ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
            try {
                if (format.equals(MimeTypes.MIME_SVG)) {
                    //Create Barcode and render it to SVG
                    SVGCanvasProvider svg = new SVGCanvasProvider(false, orientation);
                    gen.generateBarcode(svg, msg);
                    org.w3c.dom.DocumentFragment frag = svg.getDOMFragment();

                    //Serialize SVG barcode
                    TransformerFactory factory = TransformerFactory.newInstance();
                    Transformer trans = factory.newTransformer();
                    Source src = new javax.xml.transform.dom.DOMSource(frag);
                    Result res = new javax.xml.transform.stream.StreamResult(bout);
                    trans.transform(src, res);
                } else if (format.equals(MimeTypes.MIME_EPS)) {
                    EPSCanvasProvider eps = new EPSCanvasProvider(bout, orientation);
                    gen.generateBarcode(eps, msg);
                    eps.finish();
                } else {
//                    String resText = request.getParameter(BARCODE_IMAGE_RESOLUTION);
                	String resText = null;
                    int resolution = 300; //dpi
                    if (resText != null) {
                        resolution = Integer.parseInt(resText);
                    }
                    if (resolution > 2400) {
                        throw new IllegalArgumentException(
                            "Resolutions above 2400dpi are not allowed");
                    }
                    if (resolution < 10) {
                        throw new IllegalArgumentException(
                            "Minimum resolution must be 10dpi");
                    }
//                    String gray = request.getParameter(BARCODE_IMAGE_GRAYSCALE);
                    String gray = "false";
                    BitmapCanvasProvider bitmap = ("true".equalsIgnoreCase(gray)
                        ? new BitmapCanvasProvider(
                                bout, format, resolution,
                                BufferedImage.TYPE_BYTE_GRAY, true, orientation)
                        : new BitmapCanvasProvider(
                                bout, format, resolution,
                                BufferedImage.TYPE_BYTE_BINARY, false, orientation));
                    gen.generateBarcode(bitmap, msg);
                    bitmap.finish();
                }
            } finally {
                bout.close();
            }
//            response.setContentType(format);
//            response.setContentLength(bout.size());
//            response.getOutputStream().write(bout.toByteArray());
//            response.getOutputStream().flush();
            
            return bout.toByteArray();
        } catch (Exception e) {
//            log.error("Error while generating barcode", e);
            throw e;
        } catch (Throwable t) {
//            log.error("Error while generating barcode", t);
            throw t;
        }
    }

    /**
     * Check the request for the desired output format.
     * @param request the request to use
     * @return MIME type of the desired output format.
     */
//    protected String determineFormat(String format) {
////        String format = request.getParameter(BARCODE_FORMAT);
//        format = MimeTypes.expandFormat(format);
//        if (format == null) {
//            format = MimeTypes.MIME_SVG;
//        }
//        return format;
//    }

    /**
     * Build an Avalon Configuration object from the request.
     * @param request the request to use
     * @return the newly built COnfiguration object
     * @todo Change to bean API
     */
    protected Configuration buildCfg(String bType, String bHeight, String bModuleWidth) {
        DefaultConfiguration cfg = new DefaultConfiguration("barcode");
        //Get type
        String type = bType;
        
        Collection bNames = BarcodeUtil.getInstance().getClassResolver().getBarcodeNames();
        
        //TODO setting type dynamically fails. For now code 39 is enough
//        if (type == null || !bNames.contains(type)) {
            type = "code39";
//        }
        
        log.info("Using type {}",type);
        
        DefaultConfiguration child = new DefaultConfiguration(type);
        cfg.addChild(child);
        //Get additional attributes
        DefaultConfiguration attr;
        String height = bHeight;
        if (height != null) {
            attr = new DefaultConfiguration("height");
            attr.setValue(height);
            child.addChild(attr);
        }
        String moduleWidth = bType;
        if (moduleWidth != null) {
            attr = new DefaultConfiguration("module-width");
            attr.setValue(moduleWidth);
            child.addChild(attr);
        }
        String wideFactor = null; //TODO
        if (wideFactor != null) {
            attr = new DefaultConfiguration("wide-factor");
            attr.setValue(wideFactor);
            child.addChild(attr);
        }
        String quietZone = null; //TODO
        if (quietZone != null) {
            attr = new DefaultConfiguration("quiet-zone");
            if (quietZone.startsWith("disable")) {
                attr.setAttribute("enabled", "false");
            } else {
                attr.setValue(quietZone);
            }
            child.addChild(attr);
        }

        // creating human readable configuration according to the new Barcode Element Mappings
        // where the human-readable has children for font name, font size, placement and
        // custom pattern.
//        String humanReadablePosition = request.getParameter(BARCODE_HUMAN_READABLE_POS);
//        String pattern = request.getParameter(BARCODE_HUMAN_READABLE_PATTERN);
//        String humanReadableSize = request.getParameter(BARCODE_HUMAN_READABLE_SIZE);
//        String humanReadableFont = request.getParameter(BARCODE_HUMAN_READABLE_FONT);
        
        //TODO
        String humanReadablePosition = "none";
        String pattern = null;
        String humanReadableSize = null;
        String humanReadableFont = null;

        if (!((humanReadablePosition == null)
                && (pattern == null)
                && (humanReadableSize == null)
                && (humanReadableFont == null))) {
            attr = new DefaultConfiguration("human-readable");

            DefaultConfiguration subAttr;
            if (pattern != null) {
                subAttr = new DefaultConfiguration("pattern");
                subAttr.setValue(pattern);
                attr.addChild(subAttr);
            }
            if (humanReadableSize != null) {
                subAttr = new DefaultConfiguration("font-size");
                subAttr.setValue(humanReadableSize);
                attr.addChild(subAttr);
            }
            if (humanReadableFont != null) {
                subAttr = new DefaultConfiguration("font-name");
                subAttr.setValue(humanReadableFont);
                attr.addChild(subAttr);
            }
            if (humanReadablePosition != null) {
              subAttr = new DefaultConfiguration("placement");
              subAttr.setValue(humanReadablePosition);
              attr.addChild(subAttr);
            }

            child.addChild(attr);
        }

        return cfg;
    }

}
