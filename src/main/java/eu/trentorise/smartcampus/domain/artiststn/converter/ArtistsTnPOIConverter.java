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

import com.google.protobuf.ByteString;

import eu.trentorise.smartcampus.domain.discovertrento.GenericPOI;
import eu.trentorise.smartcampus.domain.discovertrento.POIData;
import eu.trentorise.smartcampus.service.artiststn.data.message.Artiststn.Artist;

public class ArtistsTnPOIConverter  implements DataConverter {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private static final String TYPE_MUSE = "Muse";   
	
//	private static final int DURATION = (24*60 - 1)*60*1000;
	
	public Serializable toMessage(Map<String, Object> parameters) {
		if (parameters == null)
			return null;
		return new HashMap<String, Object>(parameters);
	}
	
	public Object fromMessage(Serializable object) {
		List<ByteString> data = (List<ByteString>) object;
		Tuple res = new Tuple();
		List<GenericPOI> list = new ArrayList<GenericPOI>();
		for (ByteString bs : data) {
			try {
				Artist art = Artist.parseFrom(bs);
				GenericPOI gp = extractGenericPOI(art);
				list.add(gp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		res.put("data", list.toArray(new GenericPOI[list.size()]));
		return res;
	}

	private GenericPOI extractGenericPOI(Artist art) throws ParseException {
		GenericPOI gp = new GenericPOI();
		
		POIData pd = new POIData(art.getPoi());
		
		gp.setPoiData(pd);
		gp.setType(TYPE_MUSE);
		
		gp.setSource("Muse");
		
		gp.setTitle(art.getName());
		gp.setDescription(createDescription(art));
		
		gp.setId(art.getPoi().getPoiId());
		
		return gp;
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

