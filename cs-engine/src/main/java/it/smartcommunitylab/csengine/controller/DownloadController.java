package it.smartcommunitylab.csengine.controller;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.csengine.storage.LocalDocumentManager;

@RestController
public class DownloadController {

	@Autowired
	private LocalDocumentManager documentManager;
	
	private static final transient Logger logger = LoggerFactory.getLogger(DownloadController.class);
	
	@GetMapping("/download/file")
	public void download(@RequestParam String key, HttpServletResponse response) throws Exception {
		String result[] = documentManager.getFile(key);
		
		if (result != null) {
			Long expiration = Long.parseLong(result[2]);
			if (System.currentTimeMillis() > expiration) {
				logger.error("Url expired");
				response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT);
			}			
			
			File file = documentManager.loadFile(result[0]);

			response.setContentType(result[1]);
			
			if (result[3] != null) {
				response.setHeader("Content-Disposition", "attachment; filename=\"" + result[3] + "\"");
			}
			response.getOutputStream().write(FileUtils.readFileToByteArray(file));			
			
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
		
	}
	
}
