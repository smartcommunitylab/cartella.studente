package it.smartcommunitylab.csengine.extsource.istat;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;

import it.smartcommunitylab.csengine.model.TeachingUnit;
import it.smartcommunitylab.csengine.storage.TeachingUnitRepository;

@Component
public class IstatLookup {
	
	private static final transient Logger logger = LoggerFactory.getLogger(IstatLookup.class);

	@Autowired
	private TeachingUnitRepository repository;

	private HashBiMap<String, String> istatMap;
	
	private ObjectMapper mapper = new ObjectMapper();

	@PostConstruct
	public void init() {
		try {
		Map istat = mapper.readValue(Resources.toString(Resources.getResource("istat.json"), Charsets.UTF_8), Map.class);

		istatMap = HashBiMap.create();
		istatMap.putAll(istat);
		} catch (Exception e) {
			logger.error("Error importing istat codes", e);
		}
	}
	
	public String addIstatCodeToTeachingUnits() throws Exception {

		List<TeachingUnit> list = repository.findAll();

		int found = 0;
		for (TeachingUnit tu : list) {
			String address = tu.getAddress();
			String istat = findIstatByTeachingUnitAddress(address);
			if (istat != null) {
				tu.setCodiceIstat(istat);
				repository.save(tu);
				found++;
			}
		}

		return found + "/" + list.size();
	}

	private String findIstatByTeachingUnitAddress(String address) {
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
}