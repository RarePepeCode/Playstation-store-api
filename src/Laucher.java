import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;;


public class Laucher {
	
	final static String allGamesUrlStart = "https://store.playstation.com/chihiro-api/viewfinder/SA/en/999/STORE-MSF75508-FULLGAMES?size=99&gkb=1&geoCountry=EU&start=";
	final static String allGamesUrlEnd = "&game_content_type=games";
	final static String dataFile = "data.txt";
	
	public static void main(String[] args) throws IOException, JSONException {
		List<Game> allGames = getAllGamesUrl(new ArrayList<>(), 0);
		System.out.println("done");


	}
	
	public static List<Game> getAllGamesUrl(List<Game> tempgamesData, Integer startGameNr) throws IOException, JSONException {
		List<Game> gamesData = tempgamesData;
		String fullUrl = allGamesUrlStart+startGameNr.toString()+allGamesUrlEnd;
		WriteDataToDataFile(fullUrl);
		
//		InputStream is = Laucher.class.getResourceAsStream("/data.json");
//        JSONTokener tokener = new JSONTokener(is);
        JSONObject object = new JSONObject(ReadDataFromDataFile());
		JSONArray gamesLink = object.getJSONArray("links");
        for (int i = 0; i < gamesLink.length(); i++) {
        	System.out.println(i);
            gamesData.add(parseGameData((JSONObject) gamesLink.get(i)));
        }
        if (gamesLink.length() == 99)
        	return getAllGamesUrl(gamesData, startGameNr + 99);		
		return gamesData;
	}
	
	public static void WriteDataToDataFile(String path) throws IOException {
		URL url = new URL(path);
		byte buf[] = new byte[4096];
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fos = new FileOutputStream(dataFile);

		   int bytesRead = 0;

		   while((bytesRead = bis.read(buf)) != -1) {
		       fos.write(buf, 0, bytesRead);
		   }

		   fos.flush();
		   fos.close();
		   bis.close();
	}
	
	public static String ReadDataFromDataFile() throws IOException {
		return new String(Files.readAllBytes(Paths.get(dataFile))); 
	}
	
	public static Game parseGameData(JSONObject obj) throws JSONException {
		String dollarPrince = "";
		String id = obj.getString("id");
		String name = obj.getString("name");
		System.out.println(name);
		if (!obj.getJSONObject("default_sku").equals(null))
			dollarPrince = obj.getJSONObject("default_sku").getString("display_price");
		double price = Double.parseDouble(dollarPrince.substring(1));
		JSONObject gameImages = (JSONObject) obj.getJSONArray("images").get(0);
		String picUrl = gameImages.getString("url");
		
		return new Game(id, name, price, picUrl);
		
	}

}
