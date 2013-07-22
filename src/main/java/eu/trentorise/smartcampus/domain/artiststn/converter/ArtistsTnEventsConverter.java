package eu.trentorise.smartcampus.domain.artiststn.converter;

import it.sayservice.platform.core.domain.actions.DataConverter;
import it.sayservice.platform.core.domain.ext.Tuple;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.protobuf.ByteString;

import eu.trentorise.smartcampus.domain.discovertrento.GenericEvent;
import eu.trentorise.smartcampus.domain.discovertrento.GenericPOI;
import eu.trentorise.smartcampus.domain.discovertrento.POIData;
import eu.trentorise.smartcampus.service.artiststn.data.message.Artiststn.Artist;

public class ArtistsTnEventsConverter implements DataConverter {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");

	private static final String SOURCE_MUSE = "Muse";
	private static final String TYPE_MUSE = "Muse";

	// private static final int DURATION = (24*60 - 1)*60*1000;

	public Serializable toMessage(Map<String, Object> parameters) {
		if (parameters == null)
			return null;
		return new HashMap<String, Object>(parameters);
	}

	public Object fromMessage(Serializable object) {
		List<ByteString> data = (List<ByteString>) object;
		Tuple res = new Tuple();
		List<GenericEvent> list = new ArrayList<GenericEvent>();
		for (ByteString bs : data) {
			try {
				Artist art = Artist.parseFrom(bs);
				GenericEvent ge = extractGenericEvent(art);
				list.add(ge);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		res.put("data", list.toArray(new GenericEvent[list.size()]));
		return res;
	}

	private GenericEvent extractGenericEvent(Artist art) throws ParseException {
		List<GenericEvent> result = new ArrayList<GenericEvent>();

		GenericEvent ge = new GenericEvent();
		ge.setTitle(art.getName());
		ge.setDescription(art.getDescription());
		ge.setSource(SOURCE_MUSE);
		ge.setType(TYPE_MUSE);

		String f = art.getTimesList().get(0);
		String t = art.getTimesList().get(art.getTimesList().size() - 1);

		ge.setFromTimeString(f);
		ge.setToTimeString(t);

		long from = sdf.parse(f).getTime();
		long to = sdf.parse(t).getTime();
		ge.setFromTime(from);
		ge.setToTime(to);

		ge.setPoiId(art.getPoi().getPoiId());
		
		ge.setId(encode(art.getName()));
		
		Map<String,Object> map = new TreeMap<String, Object>();
		map.put("image", art.getImg());
		map.put("link", art.getLink());
		try {
			ge.setCustomData(new ObjectMapper().writeValueAsString(map));
		} catch (Exception e) {
			e.printStackTrace();
		}		

		return ge;
	}

	private String createDescription(Artist art) {
		StringBuilder descr = new StringBuilder();
		descr.append("<p>");
		descr.append("<a href=\"" + art.getLink() + "\">Website</a><br/>");
		descr.append(art.getDescription());
		descr.append("</p>");
		return descr.toString();
	}

	private static String encode(String s) {
		return new BigInteger(s.getBytes()).toString(16);
	}
}
