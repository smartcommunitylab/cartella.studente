package it.smartcommunitylab.csengine.controller.ext;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportIstituzioni;
import it.smartcommunitylab.csengine.extsource.infotn.InfoTnImportUnita;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class InfoTnController {
	private static final transient Logger logger = LoggerFactory.getLogger(InfoTnController.class);
	
	@Autowired
	InfoTnImportIstituzioni importIstituzioni;
	
	@Autowired
	InfoTnImportUnita importUnita;
	
	@RequestMapping(value = "/extsource/infotn/istituzioni/empty", method = RequestMethod.GET)
	public @ResponseBody String importIstituzioniFromEmpty() throws Exception {
		return importIstituzioni.importIstituzioniFromEmpty();
	}
	
	@RequestMapping(value = "/extsource/infotn/unita/empty", method = RequestMethod.GET)
	public @ResponseBody String importUnitaFromEmpty() throws Exception {
		return importUnita.importUnitaFromEmpty();
	}
	
	@ExceptionHandler({EntityNotFoundException.class, StorageException.class})
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Map<String,String> handleEntityNotFoundError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(value=HttpStatus.FORBIDDEN)
	@ResponseBody
	public Map<String,String> handleUnauthorizedError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Map<String,String> handleGenericError(HttpServletRequest request, Exception exception) {
		logger.error(exception.getMessage());
		return Utils.handleError(exception);
	}		
	
}
