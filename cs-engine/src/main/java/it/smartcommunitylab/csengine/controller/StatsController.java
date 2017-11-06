package it.smartcommunitylab.csengine.controller;

import it.smartcommunitylab.csengine.common.Const;
import it.smartcommunitylab.csengine.common.Utils;
import it.smartcommunitylab.csengine.exception.EntityNotFoundException;
import it.smartcommunitylab.csengine.exception.StorageException;
import it.smartcommunitylab.csengine.exception.UnauthorizedException;
import it.smartcommunitylab.csengine.model.stats.RegistrationStats;
import it.smartcommunitylab.csengine.storage.DocumentManager;
import it.smartcommunitylab.csengine.storage.RepositoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Controller
public class StatsController extends AuthController {
	private static final transient Logger logger = LoggerFactory.getLogger(StatsController.class);
	
	@Autowired
	private RepositoryManager dataManager;
	
	@Autowired
	private DocumentManager documentManager;
	
	private Cache<String, RegistrationStats> statsCache;
		
	@PostConstruct
	public void init() {
		statsCache = CacheBuilder.newBuilder()
				.maximumSize(100)
		    .expireAfterAccess(1, TimeUnit.DAYS)
		    .build();
	}
	
	private String getStatsKey(String typology, String schoolYear) {
		return typology + "_" + schoolYear;
	}
	
	@RequestMapping(value = "/api/stats/registration/ordine", method = RequestMethod.GET)
	public @ResponseBody List<RegistrationStats> getRegistrationStatsByOrdine(
			@RequestParam List<String> schoolYears,
			HttpServletRequest request) throws Exception {
		List<RegistrationStats> result = new ArrayList<RegistrationStats>();
		for(String schoolYear : schoolYears) {
			String statsKey = getStatsKey(Const.TYPOLOGY_QNAME_ORDINE, schoolYear);
			RegistrationStats stats = statsCache.asMap().get(statsKey);
			if(stats == null) {
				stats = dataManager.getRegistrationStats(schoolYear, Const.TYPOLOGY_QNAME_ORDINE);
				statsCache.put(statsKey, stats);
			}
			result.add(stats);
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getRegistrationStatsByOrdine: %s - %s", schoolYears, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/stats/registration/tipologia", method = RequestMethod.GET)
	public @ResponseBody List<RegistrationStats> getRegistrationStatsByTipologia(
			@RequestParam List<String> schoolYears,
			HttpServletRequest request) throws Exception {
		List<RegistrationStats> result = new ArrayList<RegistrationStats>();
		for(String schoolYear : schoolYears) {
			String statsKey = getStatsKey(Const.TYPOLOGY_QNAME_TIPOLOGIA, schoolYear);
			RegistrationStats stats = statsCache.asMap().get(statsKey);
			if(stats == null) {
				stats = dataManager.getRegistrationStats(schoolYear, Const.TYPOLOGY_QNAME_TIPOLOGIA);
				statsCache.put(statsKey, stats);
			}
			result.add(stats);
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getRegistrationStatsByTipologia: %s - %s", schoolYears, result.size()));
		}
		return result;
	}
	
	@RequestMapping(value = "/api/stats/registration/indirizzo", method = RequestMethod.GET)
	public @ResponseBody List<RegistrationStats> getRegistrationStatsByIndirizzo(
			@RequestParam List<String> schoolYears,
			HttpServletRequest request) throws Exception {
		List<RegistrationStats> result = new ArrayList<RegistrationStats>();
		for(String schoolYear : schoolYears) {
			String statsKey = getStatsKey(Const.TYPOLOGY_QNAME_INDIRIZZO, schoolYear);
			RegistrationStats stats = statsCache.asMap().get(statsKey);
			if(stats == null) {
				stats = dataManager.getRegistrationStats(schoolYear, Const.TYPOLOGY_QNAME_INDIRIZZO);
				statsCache.put(statsKey, stats);
			}
			result.add(stats);
		}
		if(logger.isInfoEnabled()) {
			logger.info(String.format("getRegistrationStatsByIndirizzo: %s - %s", schoolYears, result.size()));
		}
		return result;
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
