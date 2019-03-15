import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Launcher {
	
	final static String allGamesUrlStart = "https://store.playstation.com/chihiro-api/viewfinder/SA/en/999/STORE-MSF75508-FULLGAMES?size=99&gkb=1&geoCountry=EU&start=";
	final static String allGamesUrlEnd = "&game_content_type=games";
	final static String dataFile = "data.txt";
	
	public static void main(String[] args) throws IOException, JSONException {
		List<Game> allGames = getAllGamesList(new ArrayList<>(), 0);
		System.out.println("done");
	}
	
	public static List<Game> getAllGamesList(List<Game> tempgamesData, Integer startGameNr) throws IOException, JSONException {
		List<Game> gamesData = tempgamesData;
		String fullUrl = allGamesUrlStart+startGameNr.toString()+allGamesUrlEnd;
		
		WriteDataToDataFile(fullUrl);
		System.out.println(fullUrl);

        JSONObject object = new JSONObject(ReadDataFromDataFile());
		JSONArray gamesLink = object.getJSONArray("links");
		
        for (int i = 0; i < gamesLink.length(); i++) {
            gamesData.add(parseGameData((JSONObject) gamesLink.get(i)));
        }
        
        if (gamesLink.length() == 99)
        	return getAllGamesList(gamesData, startGameNr + 99);
        
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
		if (obj.has("default_sku")) {
			String id = obj.getString("id");
			String name = cleanGamesName(obj.getString("title_name"));
			String dollarPrince = obj.getJSONObject("default_sku").getString("display_price");
			double price = Double.parseDouble(dollarPrince.substring(1));
			JSONObject gameImages = (JSONObject) obj.getJSONArray("images").get(0);
			String picUrl = gameImages.getString("url");
			
			System.out.println(name);

			return new Game(id, name, price, picUrl);
		}
		return null;

	}
	
	public static String cleanGamesName(String name) {
		name = name.replaceAll("’", "'");
		name = name.replaceAll("™", "\u2122");
		name = name.replaceAll("®", "\u00AE");
		name = name.replaceAll(":�", ":");
		name = name.replaceAll("ü", "u");
		name = name.replaceAll("–", "-");
		
		return name;
	}

}
