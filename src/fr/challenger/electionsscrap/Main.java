package fr.challenger.electionsscrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Main {
	
	private static final int SLEEP_MS = 100;
	private static final int RESPONSE_OK = 200;
	private static final int CIRCONS_TOTAL = 577;
	private static final int CIRCONS_ABSOLU = (CIRCONS_TOTAL + 1) / 2;
	private static final String baseLink = "https://www.francetvinfo.fr/elections/resultats/";
	
	private static final String CIRCON_MOTIF = "result-panel__left-col";
	private static final String PARTICIP_MOTIF = "result-participation__percent";
	private static final String CANDIDAT_MOTIF = "text-only-for-screen-reader";
	
	private static final String RESET = "\u001B[0m";
	private static final String RED = "\u001B[31m";
	private static final String GREEN = "\u001B[32m";
	private static final String YELLOW = "\u001B[33m";
	private static final String CYAN = "\u001B[36m";
	
	
	/*
	 * ChatGPT Prompt :
	 * 
	 * cite moi tous les départements français sous cette forme :

		[nom]_[code postal]
		
		Les [] ne doivent pas être affichés.
		Le nom doit être écrit en minuscules.
		Si le département comporte des caractères non alphabétiques, ils seront automatiquement remplacés par -
		Les accents seront remplacés par leur caractère d'origine, ex : è ou é devient e
		
		Forme ainsi une Liste sous la forme d'un String[], compatible avec Java.
	 * 
	 */
	
	private static String[] pages = {
		    "ain_01",
		    "aisne_02",
		    "allier_03",
		    "alpes-de-haute-provence_04",
		    "hautes-alpes_05",
		    "alpes-maritimes_06",
		    "ardeche_07",
		    "ardennes_08",
		    "ariege_09",
		    "aube_10",
		    "aude_11",
		    "aveyron_12",
		    "bouches-du-rhone_13",
		    "calvados_14",
		    "cantal_15",
		    "charente_16",
		    "charente-maritime_17",
		    "cher_18",
		    "correze_19",
		    "corse-du-sud_2a",
		    "haute-corse_2b",
		    "cote-d-or_21",
		    "cotes-d-armor_22",
		    "creuse_23",
		    "dordogne_24",
		    "doubs_25",
		    "drome_26",
		    "eure_27",
		    "eure-et-loir_28",
		    "finistere_29",
		    "gard_30",
		    "haute-garonne_31",
		    "gers_32",
		    "gironde_33",
		    "herault_34",
		    "ille-et-vilaine_35",
		    "indre_36",
		    "indre-et-loire_37",
		    "isere_38",
		    "jura_39",
		    "landes_40",
		    "loir-et-cher_41",
		    "loire_42",
		    "haute-loire_43",
		    "loire-atlantique_44",
		    "loiret_45",
		    "lot_46",
		    "lot-et-garonne_47",
		    "lozere_48",
		    "maine-et-loire_49",
		    "manche_50",
		    "marne_51",
		    "haute-marne_52",
		    "mayenne_53",
		    "meurthe-et-moselle_54",
		    "meuse_55",
		    "morbihan_56",
		    "moselle_57",
		    "nievre_58",
		    "nord_59",
		    "oise_60",
		    "orne_61",
		    "pas-de-calais_62",
		    "puy-de-dome_63",
		    "pyrenees-atlantiques_64",
		    "hautes-pyrenees_65",
		    "pyrenees-orientales_66",
		    "bas-rhin_67",
		    "haut-rhin_68",
		    "rhone_69",
		    "haute-saone_70",
		    "saone-et-loire_71",
		    "sarthe_72",
		    "savoie_73",
		    "haute-savoie_74",
		    "paris_75",
		    "seine-maritime_76",
		    "seine-et-marne_77",
		    "yvelines_78",
		    "deux-sevres_79",
		    "somme_80",
		    "tarn_81",
		    "tarn-et-garonne_82",
		    "var_83",
		    "vaucluse_84",
		    "vendee_85",
		    "vienne_86",
		    "haute-vienne_87",
		    "vosges_88",
		    "yonne_89",
		    "territoire-de-belfort_90",
		    "essonne_91",
		    "hauts-de-seine_92",
		    "seine-saint-denis_93",
		    "val-de-marne_94",
		    "val-d-oise_95",
		    "guadeloupe_971",
		    "martinique_972",
		    "guyane_973",
		    "la-reunion_974",
		    "saint-pierre-et-miquelon_975",
		    "mayotte_976",
		    "saint-martin-saint-barthelemy_977",
		    "wallis-et-futuna_986",
		    "polynesie-francaise_987",
		    "nouvelle-caledonie_988",
		    "francais-de-l-etranger_99"
		};
	
	private static void sleep()
	{
		try {
			Thread.sleep(SLEEP_MS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static HttpURLConnection getConnection(final String page)
	{
		final URL url;
		final HttpURLConnection urlConnection;
		
		try {
			url = new URL(baseLink + page);
			urlConnection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return urlConnection;
	}
	
	private static String downloadPage(final String page)
	{
		final HttpURLConnection connection = getConnection(page);
		
		if (connection == null) return null;
		
		final BufferedReader reader;
		
		try {
			if (connection.getResponseCode() != RESPONSE_OK) return null;
			
			reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream(), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		final StringBuilder builder = new StringBuilder();
		String line;

		try {
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
        
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return builder.toString();
	}
	
	private static Pair<Map<String, Integer>, Map<String, Integer>> countElu(final String page)
	{
		final Map<String, Integer> firstPlace = new HashMap<>();
		final Map<String, Integer> elus = new HashMap<>();
		final String downloadedPage = downloadPage(page);
		
		if (downloadedPage == null) return Pair.of(firstPlace, elus);
		
		final String[] circons = downloadedPage.split(CIRCON_MOTIF);
		
		for (int i = 3; i < circons.length; i++)
		{
			final String circon = circons[i];
			
			final int participIndex = circon.indexOf(PARTICIP_MOTIF);
			
			if (participIndex == -1) continue ;
			
			final String participResult = circon.substring(participIndex + PARTICIP_MOTIF.length() + 2,
					circon.indexOf('%', participIndex));
			final Double participPercent = Double.valueOf(participResult);
			
			final String topResult = circon.substring(circon.indexOf(CANDIDAT_MOTIF, 50) + CANDIDAT_MOTIF.length());
			
			System.out.println("  * Circonscription n°" + (i - 2) 
					+ " : (Participation: " + participPercent + "%)");
			
			final String[] infosResult = topResult.substring(2, topResult.indexOf('%')).split(", ");
			
			final Double resultPercent = Double.valueOf(infosResult[2]);
			
			final Double votedPercent = (resultPercent * participPercent) / 100;
			
			final String parti = infosResult[1].split("-")[0];
			
			System.out.println((resultPercent < 50.0 ? RED : (votedPercent < 25.0 ? YELLOW : GREEN)) +
					"     -> Candidat en tête : " + infosResult[0] + " - " + parti
							+ " - " + resultPercent + "%" + RESET);
			
			if (resultPercent >= 50.0 && votedPercent >= 25.0)
			{
				elus.put(parti, elus.getOrDefault(parti, 0) + 1);
			}
			
			firstPlace.put(parti, firstPlace.getOrDefault(parti, 0) + 1);
		}
		
		return Pair.of(firstPlace, elus);
	}
	
	private static void mapComplete(final Map<String, Integer> map, final Map<String, Integer> part)
	{
		for (final Entry<String, Integer> entry : part.entrySet())
		{
			map.put(entry.getKey(), map.getOrDefault(entry.getKey(), 0) + entry.getValue());
		}
	}
	
	private static Pair<Map<String, Integer>, Map<String, Integer>> countForAll()
	{
		final Map<String, Integer> firstPlace = new HashMap<>();
		final Map<String, Integer> elus = new HashMap<>();
		
		for (final String page : pages)
		{
			System.out.println(CYAN + "Étude du département " + page + " :" + RESET + "\n");
			
			final Pair<Map<String, Integer>, Map<String, Integer>> localElus = countElu(page);
			
			mapComplete(firstPlace, localElus.getLeft());
			mapComplete(elus, localElus.getRight());
			
			System.out.println("");
			sleep();
		}
		
		return Pair.of(firstPlace, elus);
	}
	
	private static void printForCategory(final String category, final List<Map.Entry<String, Integer>> partis)
	{
		int i = 0;
		int total = 0;
		
		partis.sort((e1, e2) -> {
			int valueCompare = e2.getValue().compareTo(e1.getValue());
			if (valueCompare != 0) {
				return valueCompare;
			} else {
				return e1.getKey().compareTo(e2.getKey());
			}
		});
		
		System.out.println("\n" + category.toUpperCase() + " :\n");
		
		while (i < partis.size())
		{
			final Entry<String, Integer> parti = partis.get(i++);
			
			total += parti.getValue();
			
			final String majorite = (parti.getValue() >= CIRCONS_ABSOLU) ? CYAN + " [MAJORITÉ ABSOLUE]" + RESET : "";
			
			System.out.println("| " + i + ". " + parti.getKey() + " : " + parti.getValue(
					) + " " + category + majorite);
				
		}
		
		System.out.println("\n| Députés étudiés : " + total + " / " + CIRCONS_TOTAL);
	}
	
	private static void printForAll()
	{
		final Pair<Map<String, Integer>, Map<String, Integer>> elus = countForAll();
		final List<Map.Entry<String, Integer>> partisFirst = new ArrayList<>(elus.getLeft().entrySet());
		final List<Map.Entry<String, Integer>> partisElus = new ArrayList<>(elus.getRight().entrySet());
		
		printForCategory("députés en tête", partisFirst);
		printForCategory("députés élus (1er tour)", partisElus);
	}
	
	public static void main(String[] args)
	{
		printForAll();
	}

}
