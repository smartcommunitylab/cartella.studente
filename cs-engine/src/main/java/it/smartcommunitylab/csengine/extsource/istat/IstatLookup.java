package it.smartcommunitylab.csengine.extsource.istat;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import it.smartcommunitylab.csengine.model.Certifier;
import it.smartcommunitylab.csengine.model.Institute;
import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.storage.CertifierRepository;
import it.smartcommunitylab.csengine.storage.InstituteRepository;
import it.smartcommunitylab.csengine.storage.TeachingUnitRepository;

@Component
public class IstatLookup {
	
	private static final transient Logger logger = LoggerFactory.getLogger(IstatLookup.class);
	
	private static final String ISTAT_PREFIX = "022";
	private final static int EARTH_RADIUS = 6371; // Earth radius in km.

	@Autowired
	@Value("${google.api.key}")
	private String apiKey;	
	
	
	@Autowired
	private TeachingUnitRepository teachingUnitRepository;

	@Autowired
	private InstituteRepository instituteRepository;	
	
	@Autowired
	private CertifierRepository certifierRepository;	
	
	private HashBiMap<String, String> istatMap;
	
	private Map<String, TownIstat> townIstatMap;
	
	GeoApiContext context;
	
	private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@PostConstruct
	public void init() {
		context = new GeoApiContext();
		context.setApiKey(apiKey);
		
		try {
		Map istat = mapper.readValue(Resources.toString(Resources.getResource("istat.json"), Charsets.UTF_8), Map.class);

		istatMap = HashBiMap.create();
		istatMap.putAll(istat);
		
		townIstatMap = Maps.newTreeMap();
		
		List<TownIstat> towns = mapper.readValue(Resources.toString(Resources.getResource("towns.json"), Charsets.UTF_8), new TypeReference<List<TownIstat>>() {
		});		
		towns.stream().forEach(x -> {
			Double coords[] = new Double[2];
			coords[0] = x.getLon();
			coords[1] = x.getLat();
			x.setCoords(coords);
			townIstatMap.put(padIstat(x.getIstat()), x);
		});
		
		
		} catch (Exception e) {
			logger.error("Error importing istat codes", e);
		}
	}
	
	public String addIstatCodeToTeachingUnits() throws Exception {

		List<TeachingUnit> list = teachingUnitRepository.findAll();

		int found = 0;
		for (TeachingUnit tu : list) {
			String address = tu.getAddress();
			String istat = findIstatByAddress(address);
			if (istat != null) {
				tu.setCodiceIstat(istat);
				
				TownIstat ti = townIstatMap.get(istat);
				
				geocode(tu, ti);
				found++;
			}
		}

		teachingUnitRepository.save(list);
	
		return found + "/" + list.size();
	}

	public String geocodeCertifier() throws Exception {

		List<Certifier> list = certifierRepository.findAll();

		int found = 0;
		for (Certifier cer : list) {
			geocode(cer);
		}

		certifierRepository.save(list);
	
		return "" + list.size();
	}	
	
	public String addIstatCodeToInstitutes() throws Exception {

		List<Institute> list = instituteRepository.findAll();

		int found = 0;
		for (Institute is : list) {
			String address = is.getAddress();
			String istat = findIstatByAddress(address);
			if (istat != null) {
				is.setCodiceIstat(istat);
				
				TownIstat ti = townIstatMap.get(istat);
				
				geocode(is, ti);
				found++;
			}
		}

		instituteRepository.save(list);
	
		return found + "/" + list.size();
	}	
	
	private String findIstatByAddress(String address) {
		try {
		Multimap<Integer, String> sorted = ArrayListMultimap.create();

		for (String town : istatMap.values()) {
			if (address.toLowerCase().contains(" " + town.toLowerCase() + " ")) {
				int startIndex = address.toLowerCase().indexOf(" " + town.toLowerCase() + " ");
				int endIndex = (" " + town.toLowerCase() + " ").length() + startIndex;
				sorted.put(endIndex, town);
			}
		}

		int max = sorted.keySet().stream().sorted(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2 - o1;
			}
		}).findFirst().get();
		String chosen = sorted.get(max).stream().sorted(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o2.length() - o1.length();
			}
		}).findFirst().get();

		return istatMap.inverse().get(chosen);
		} catch (Exception e) {
			logger.error("Error finding istat code for '" + address + "'", e);
			return null;
		}
		
	}
	
	private void geocode(TeachingUnit tu, TownIstat ti) throws Exception {
		if (tu.getGeocode() != null) {
			return;
		}
		
		String address = tu.getName() + " " + tu.getAddress();

		Double coords[] = ti.getCoords();
		int accuracy = 2;
		
		try {
			GeocodingResult[] results = GeocodingApi.geocode(context, address).region("IT").await();

			GeocodingResult bestResult = null;
			long minD = Long.MAX_VALUE;
			if (results != null) {
				for (GeocodingResult result : results) {
					LatLng location = result.geometry.location;
					long d = (long) (1000 * harvesineDistance(location.lat, location.lng, ti.getCoords()[1], ti.getCoords()[0]));

					if (d < minD) {
						minD = d;
						bestResult = result;
					}
				}
			}
			
			if (bestResult != null) {
				LatLng location = bestResult.geometry.location;

				long d = (long)(1000 *harvesineDistance(location.lat, location.lng, ti.getCoords()[1], ti.getCoords()[0]));
				
				if (d <= 20000) {
					coords = new Double[] { location.lng, location.lat};
					accuracy = 1;
				}
			} else {
			}
		} catch (Exception e) {
			logger.error("Error geocoding: " + address, e);
		}
		
		tu.setGeocode(coords);
		tu.setGeocodeAccuracy(accuracy);
		
	}	
	
	private void geocode(Institute is, TownIstat ti) throws Exception {
		if (is.getGeocode() != null) {
			return;
		}
		
		String address = is.getName() + " " + is.getAddress();

		Double coords[] = ti.getCoords();
		int accuracy = 2;
		
		try {
			GeocodingResult[] results = GeocodingApi.geocode(context, address).region("IT").await();

			GeocodingResult bestResult = null;
			long minD = Long.MAX_VALUE;
			if (results != null) {
				for (GeocodingResult result : results) {
					LatLng location = result.geometry.location;
					long d = (long) (1000 * harvesineDistance(location.lat, location.lng, ti.getCoords()[1], ti.getCoords()[0]));

					if (d < minD) {
						minD = d;
						bestResult = result;
					}
				}
			}
			
			if (bestResult != null) {
				LatLng location = bestResult.geometry.location;

				long d = (long)(1000 *harvesineDistance(location.lat, location.lng, ti.getCoords()[1], ti.getCoords()[0]));
				
				if (d <= 20000) {
					coords = new Double[] { location.lng, location.lat};
					accuracy = 1;
				}
			} else {
			}
		} catch (Exception e) {
			logger.error("Error geocoding: " + address, e);
		}
		
		is.setGeocode(coords);
		is.setGeocodeAccuracy(accuracy);
		
	}		
	
	private void geocode(Certifier cer) throws Exception {
		if (cer.getGeocode() != null) {
			return;
		}
		
		String address = cer.getName() + " " + cer.getAddress();

		Double coords[] = null;
		
		try {
			GeocodingResult[] results = GeocodingApi.geocode(context, address).region("IT").await();
			
			if (results != null) {
			LatLng location = results[0].geometry.location;

			coords = new Double[] { location.lng, location.lat};
			}
		} catch (Exception e) {
			logger.error("Error geocoding: " + address, e);
		}
		
		cer.setGeocode(coords);
	}		
	
	
	private static String padIstat(String istat) {
		return ISTAT_PREFIX + String.format("%03d", Integer.parseInt(istat));
	}	
	
	public static double harvesineDistance(double lat1, double lon1, double lat2, double lon2) {
		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);

		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;

		double a = Math.pow((Math.sin(dlat / 2)), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return EARTH_RADIUS * c;
	}	
	
	
}