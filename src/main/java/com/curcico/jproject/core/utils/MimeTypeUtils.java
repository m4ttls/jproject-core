package com.curcico.jproject.core.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MimeTypeUtils {

	public enum MimeType{
		MIME_APPLICATION_ANDREW_INSET ("application/andrew-inset", "ez"),
		MIME_APPLICATION_JSON ("application/json", "json"),
		MIME_APPLICATION_ZIP ("application/zip", "zip"),
		MIME_APPLICATION_X_GZIP ("application/x-gzip", "gzip"),
		MIME_APPLICATION_TGZ ("application/tgz", "tgz"),
		MIME_APPLICATION_MSWORD ("application/msword", "doc"),
		MIME_APPLICATION_MSWORD_DOCX ("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
		MIME_APPLICATION_MSWORD_TEMPLATE_DOTX ("application/vnd.openxmlformats-officedocument.wordprocessingml.template", "dotx"),
		MIME_APPLICATION_OPENDOCUMENT_TEXT ("application/vnd.oasis.opendocument.text", "odt"), 
		MIME_APPLICATION_OPENDOCUMENT_TEXT_TEMPLATE("application/vnd.oasis.opendocument.text-template", "ott"),
		MIME_APPLICATION_OPENDOCUMENT_HTML_TEMPLATE("application/vnd.oasis.opendocument.text-web", "oth"),
		MIME_APPLICATION_OPENDOCUMENT_MASTER_DOCUMENT ("application/vnd.oasis.opendocument.text-master", "odm"),
		MIME_APPLICATION_OPENDOCUMENT_DRAWING("application/vnd.oasis.opendocument.graphics", "odg"),
		MIME_APPLICATION_OPENDOCUMENT_DRAWING_TEMPLATE("application/vnd.oasis.opendocument.graphics-template", "otg"),
		MIME_APPLICATION_OPENDOCUMENT_PRESENTATION("application/vnd.oasis.opendocument.presentation", "odp"), 
		MIME_APPLICATION_OPENDOCUMENT_PRESENTATION_TEMPLATE("application/vnd.oasis.opendocument.presentation-template", "otp"),
		MIME_APPLICATION_OPENDOCUMENT_SPREADSHEET("application/vnd.oasis.opendocument.spreadsheet", "ods"),
		MIME_APPLICATION_OPENDOCUMENT_SPREADSHEET_TEMPLATE("application/vnd.oasis.opendocument.spreadsheet-template", "ots"), 
		MIME_APPLICATION_OPENDOCUMENT_CHART("application/vnd.oasis.opendocument.chart", "odc"),
		MIME_APPLICATION_OPENDOCUMENT_FORMULA("application/vnd.oasis.opendocument.formula" , "odf"),
		MIME_APPLICATION_OPENDOCUMENT_DATABASE("application/vnd.oasis.opendocument.database", "odb"),
		MIME_APPLICATION_OPENDOCUMENT_IMAGE("application/vnd.oasis.opendocument.image", "odi"),
		MIME_APPLICATION_POSTSCRIPT ("application/postscript", "eps"),
		MIME_APPLICATION_PDF ("application/pdf;base64,", "pdf"),
		MIME_APPLICATION_JNLP ("application/jnlp", "jnlp"),
		MIME_APPLICATION_MAC_BINHEX40 ("application/mac-binhex40", "hqx"),
		MIME_APPLICATION_MAC_COMPACTPRO ("application/mac-compactpro", "cpt"),
		MIME_APPLICATION_MATHML_XML ("application/mathml+xml", "mathml"),
		MIME_APPLICATION_OCTET_STREAM ("application/octet-stream", "dmg"),
		MIME_APPLICATION_ODA ("application/oda", "oda"),
		MIME_APPLICATION_RDF_XML ("application/rdf+xml", "rdf"),
		MIME_APPLICATION_JAVA_ARCHIVE ("application/java-archive", "jar"),
		MIME_APPLICATION_RDF_SMIL ("application/smil", "smil"),
		MIME_APPLICATION_SRGS ("application/srgs", "gram"),
		MIME_APPLICATION_SRGS_XML ("application/srgs+xml", "grxml"),
		MIME_APPLICATION_VND_MIF ("application/vnd.mif", "mif"),
		MIME_APPLICATION_VND_MSEXCEL ("application/vnd.ms-excel", "xls"),
		MIME_APPLICATION_VND_MSEXCEL_XLSX ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
		MIME_APPLICATION_VND_MSEXCEL_XLTX ("application/vnd.openxmlformats-officedocument.spreadsheetml.template", "xltx"),
		MIME_APPLICATION_VND_MSPOWERPOINT ("application/vnd.ms-powerpoint", "ppt"),
		MIME_APPLICATION_VND_RNREALMEDIA ("application/vnd.rn-realmedia", "rm"),
		MIME_APPLICATION_X_BCPIO ("application/x-bcpio", "bcpio"),
		MIME_APPLICATION_X_CDLINK ("application/x-cdlink", "vcd"),
		MIME_APPLICATION_X_CHESS_PGN ("application/x-chess-pgn", "pgn"),
		MIME_APPLICATION_X_CPIO ("application/x-cpio", "cpio"),
		MIME_APPLICATION_X_CSH ("application/x-csh", "csh"),
		MIME_APPLICATION_X_DIRECTOR ("application/x-director", "dxr"),
		MIME_APPLICATION_X_DVI ("application/x-dvi", "dvi"),
		MIME_APPLICATION_X_FUTURESPLASH ("application/x-futuresplash", "spl"),
		MIME_APPLICATION_X_GTAR ("application/x-gtar", "gtar"),
		MIME_APPLICATION_X_HDF ("application/x-hdf", "hdf"),
		MIME_APPLICATION_X_JAVASCRIPT ("application/x-javascript", "js"),
		MIME_APPLICATION_X_KOAN ("application/x-koan", "skm"),
		MIME_APPLICATION_X_LATEX ("application/x-latex", "latex"),
		MIME_APPLICATION_X_NETCDF ("application/x-netcdf", "cdf"),
		MIME_APPLICATION_X_OGG ("application/x-ogg", "ogg"),
		MIME_APPLICATION_X_SH ("application/x-sh", "sh"),
		MIME_APPLICATION_X_SHAR ("application/x-shar", "shar"),
		MIME_APPLICATION_X_SHOCKWAVE_FLASH ("application/x-shockwave-flash", "swf"),
		MIME_APPLICATION_X_STUFFIT ("application/x-stuffit", "sit"),
		MIME_APPLICATION_X_SV4CPIO ("application/x-sv4cpio", "sv4cpio"),
		MIME_APPLICATION_X_SV4CRC ("application/x-sv4crc", "sv4crc"),
		MIME_APPLICATION_X_TAR ("application/x-tar", "tar"),
		MIME_APPLICATION_X_RAR_COMPRESSED ("application/x-rar-compressed", "rar"),
		MIME_APPLICATION_X_TCL ("application/x-tcl", "tcl"),
		MIME_APPLICATION_X_TEX ("application/x-tex", "tex"),
		MIME_APPLICATION_X_TEXINFO ("application/x-texinfo", "texi"),
		MIME_APPLICATION_X_TROFF ("application/x-troff", "roff"),
		MIME_APPLICATION_X_TROFF_MAN ("application/x-troff-man", "man"),
		MIME_APPLICATION_X_TROFF_ME ("application/x-troff-me", "me"),
		MIME_APPLICATION_X_TROFF_MS ("application/x-troff-ms", "ms"),
		MIME_APPLICATION_X_USTAR ("application/x-ustar", "ustar"),
		MIME_APPLICATION_X_WAIS_SOURCE ("application/x-wais-source", "src"),
		MIME_APPLICATION_VND_MOZZILLA_XUL_XML ("application/vnd.mozilla.xul+xml", "xul"),
		MIME_APPLICATION_XHTML_XML ("application/xhtml+xml", "xht"),
		MIME_APPLICATION_XSLT_XML ("application/xslt+xml", "xslt"),
		MIME_APPLICATION_XML ("application/xml", "xsl"),
		MIME_APPLICATION_XML_DTD ("application/xml-dtd", "dtd"),
		MIME_IMAGE_BMP ("image/bmp", "bmp"),
		MIME_IMAGE_CGM ("image/cgm", "cgm"),
		MIME_IMAGE_GIF ("image/gif", "gif"),
		MIME_IMAGE_IEF ("image/ief", "ief"),
		MIME_IMAGE_JPEG ("image/jpeg", "jpeg"),
		MIME_IMAGE_TIFF ("image/tiff", "tiff"),
		MIME_IMAGE_PNG ("image/png", "png"),
		MIME_IMAGE_SVG_XML ("image/svg+xml", "svg"),
		MIME_IMAGE_VND_DJVU ("image/vnd.djvu", "djv"),
		MIME_IMAGE_WAP_WBMP ("image/vnd.wap.wbmp", "wbmp"),
		MIME_IMAGE_X_CMU_RASTER ("image/x-cmu-raster", "ras"),
		MIME_IMAGE_X_ICON ("image/x-icon", "ico"),
		MIME_IMAGE_X_PORTABLE_ANYMAP ("image/x-portable-anymap", "pnm"),
		MIME_IMAGE_X_PORTABLE_BITMAP ("image/x-portable-bitmap", "pbm"),
		MIME_IMAGE_X_PORTABLE_GRAYMAP ("image/x-portable-graymap", "pgm"),
		MIME_IMAGE_X_PORTABLE_PIXMAP ("image/x-portable-pixmap", "ppm"),
		MIME_IMAGE_X_RGB ("image/x-rgb", "rgb"),
		MIME_AUDIO_BASIC ("audio/basic", "au"),
		MIME_AUDIO_MIDI ("audio/midi", "kar"),
		MIME_AUDIO_MPEG ("audio/mpeg", "mp3"),
		MIME_AUDIO_X_AIFF ("audio/x-aiff", "aifc"),
		MIME_AUDIO_X_MPEGURL ("audio/x-mpegurl", "m3u"),
		MIME_AUDIO_X_PN_REALAUDIO ("audio/x-pn-realaudio", "ra"),
		MIME_AUDIO_X_WAV ("audio/x-wav", "wav"),
		MIME_CHEMICAL_X_PDB ("chemical/x-pdb", "pdb"),
		MIME_CHEMICAL_X_XYZ ("chemical/x-xyz", "xyz"),
		MIME_MODEL_IGES ("model/iges", "iges"),
		MIME_MODEL_MESH ("model/mesh", "silo"),
		MIME_MODEL_VRLM ("model/vrml", "wrl"),
		MIME_TEXT_PLAIN ("text/plain", "txt"),
		MIME_TEXT_RICHTEXT ("text/richtext", "rtx"),
		MIME_TEXT_RTF ("text/rtf", "rtf"),
		MIME_TEXT_HTML ("text/html", "html"),
		MIME_TEXT_CALENDAR ("text/calendar", "ifb"),
		MIME_TEXT_CSS ("text/css", "css"),
		MIME_TEXT_SGML ("text/sgml", "sgm"),
		MIME_TEXT_TAB_SEPARATED_VALUES ("text/tab-separated-values", "tsv"),
		MIME_TEXT_COMMA_SEPARATED_VALUES ("text/csv", "csv"),
		MIME_TEXT_VND_WAP_XML ("text/vnd.wap.wml", "wml"),
		MIME_TEXT_VND_WAP_WMLSCRIPT ("text/vnd.wap.wmlscript", "wmls"),
		MIME_TEXT_X_SETEXT ("text/x-setext", "etx"),
		MIME_TEXT_X_COMPONENT ("text/x-component", "htc"),
		MIME_VIDEO_QUICKTIME ("video/quicktime", "mov"),
		MIME_VIDEO_MPEG ("video/mpeg", "mpeg"),
		MIME_VIDEO_VND_MPEGURL ("video/vnd.mpegurl", "m4u"),
		MIME_VIDEO_X_MSVIDEO ("video/x-msvideo", "avi"),
		MIME_VIDEO_X_MS_WMV ("video/x-ms-wmv", "wmv"),
		MIME_VIDEO_X_SGI_MOVIE ("video/x-sgi-movie", "movie"),
		MIME_X_CONFERENCE_X_COOLTALK ("x-conference/x-cooltalk", "ice");
		
		private String mimeType;
		private String extension;
		
		private MimeType(String mimeType, String extension) {
			this.mimeType = mimeType;
			this.extension = extension;
		}

		/**
		 * @return the mimeType
		 */
		public String getMimeType() {
			return mimeType;
		}

		/**
		 * @return the extension
		 */
		public String getExtension() {
			return extension;
		}
		
		@Override
		public String toString() {
			return mimeType;
		}
	}
	
	public static MimeType findByMimeType(String mimeType) {
	    for (MimeType m : MimeType.values()) {
	        if (m.getMimeType().equalsIgnoreCase(mimeType)) {
	            return m;
	        }
	    }
	    return null;
	}
	
	public static MimeType findByExtension(String extension) {
	    for (MimeType m : MimeType.values()) {
	        if (m.getExtension().equalsIgnoreCase(extension)) {
	            return m;
	        }
	    }
	    return null;
	}
	
	  /**
	   * Returns the corresponding MIME type to the given extension.
	   * If no MIME type was found it returns 'application/octet-stream' type.
	   */
	  public static String getMimeType(String filename) {
	    String mimeType = lookupMimeType(filename);
	    if (mimeType == null) {
	      mimeType = MimeType.MIME_APPLICATION_OCTET_STREAM.getMimeType();
	    }
	    return mimeType;
	  }


	  /** Esta no es la mejor manera de comprobar los mimetypes, lo ideal es hacerlo en base a la información del 
	   * archivo en lugar de fiarse de la extensión
	   * @param filename
	   * @return
	   */
	@Deprecated
	  private static String lookupMimeType(String filename) {
		  //TODO separar la extensión del nombre del archivo y buscar por eso
		int dotPosition = filename.lastIndexOf('.');
		String ext = filename;
		if(dotPosition>-1)
			ext = filename.substring(dotPosition+1, filename.length());
		MimeType e = findByExtension(ext);
		if(e!=null) 
			return e.getMimeType(); 
		return null;
	  }
	  
	  public static String lookupMimeType(File file) throws IOException{
		  return Files.probeContentType(file.toPath()); 
	  }
	  
	  public static String getDefaultExtension(String mimetype){
		  MimeType e = findByMimeType(mimetype);
		  if(e!=null) 
			  return e.getExtension();
		  return null;
		  
	  } 
	  
	
}
