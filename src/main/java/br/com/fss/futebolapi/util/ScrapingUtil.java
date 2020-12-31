package br.com.fss.futebolapi.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.fss.futebolapi.dto.PartidaGoogleDTO;

public class ScrapingUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingUtil.class);
	private static final String BASE_URL_GOOGLE = "https://www.google.com/search?q=";
	private static final String COMPLEMENTO_URL_GOOGLE = "&hl=pt-BR";
	
	public static void main(String[] args) {
		
				
		ScrapingUtil scraping = new ScrapingUtil();
		String url = scraping.montarUrlGoogle("barcelona", "eibar");
		scraping.obterInformacoesPartida(url);
		
	}
	
	public PartidaGoogleDTO obterInformacoesPartida(String url) {
		PartidaGoogleDTO partida = new PartidaGoogleDTO();
		
		Document document = null;
		
		try {
			document = Jsoup.connect(url).get();
			
			String title = document.title();
			LOGGER.info("Titulo da pagina: {}", title);
			
			StatusPartida statusPartida = obtemStatusPartida(document);
			partida.setStatusPartida(statusPartida.toString());
			
			if (statusPartida != StatusPartida.PARTIDA_NAO_INICIADA) {
				String tempoPartida = obtemTempoPartida(document);
				partida.setTempoPartida(tempoPartida.toString());
				
				Integer placarEquipeCasa = recuperaPlacarEquipeCasa(document);
				partida.setPlacarEquipeCasa(placarEquipeCasa.toString());
				LOGGER.info("placar casa: {}", placarEquipeCasa);
				
				Integer placarEquipeVisitante = recuperaPlacarEquipeVisitante(document);
				partida.setPlacarEquipeVisitante(placarEquipeVisitante.toString());
				LOGGER.info("placar visitante: {}",placarEquipeVisitante);
				
				String golsEquipeCasa = recuperaGolsEquipeCasa(document);
				partida.setGolsEquipeCasa(golsEquipeCasa.toString());
				LOGGER.info("Gols equipe casa: {}", golsEquipeCasa);
				
				String golsEquipeVisitante = recuperaGolsEquipeVisitante(document);
				partida.setGolsEquipeVisitante(golsEquipeVisitante.toString());
				LOGGER.info("Gols equipe visitante: {}", golsEquipeVisitante);
				
			}
			
			String nomeEquipeCasa = recuperaNomeEquipeCasa(document);
			partida.setNomeEquipeCasa(nomeEquipeCasa.toString());
			LOGGER.info("Equipe casa: {}",nomeEquipeCasa);
			
			String nomeEquipeVisitante = recuperaNomeEquipeVisitante(document);
			partida.setNomeEquipeVisitante(nomeEquipeVisitante.toString());
			LOGGER.info("Equipe visitante: {}", nomeEquipeVisitante);
			
			String urlLogoEquipeCasa = recuperaLogoEquipeCasa(document);
			partida.setUrlLogoEquipeCasa(urlLogoEquipeCasa.toString());
			LOGGER.info(urlLogoEquipeCasa);
			
			String urlLogoEquipeVisitante = recuperaLogoEquipeVisitante(document);
			partida.setUrlLogoEquipeVisitante(urlLogoEquipeVisitante.toString());
			LOGGER.info(urlLogoEquipeVisitante);
			
			return partida;
		} catch (IOException e) {
			LOGGER.error("ERRO AO CONECTAR NO GOOGLE COM O JSOUP -> {}", e.getMessage());
			e.printStackTrace();
		}
		
		return null;
		
	}

	public StatusPartida obtemStatusPartida(Document document) {
		// Situacao da partida
		//1- Partida nao iniciada
		//2- Partida iniciada
		//3- Partida em andamento
		//4- Partida Encerrada
		//5- Partida penalidade
		
		StatusPartida statusPartida = StatusPartida.PARTIDA_NAO_INICIADA;
		
		boolean isTempoPartida = document.select("span[class=liveresults-sports-immersive__game-minute]").isEmpty();
		if (!isTempoPartida) {
			String tempoPartida = document.select("span[class=liveresults-sports-immersive__game-minute]").first().text();
			statusPartida = StatusPartida.PARTIDA_EM_ANDAMENTO;
			if (tempoPartida.contains("Pênaltis")) {
				statusPartida = StatusPartida.PARTIDA_PENALTIS;
			}
			
		}
		
		isTempoPartida = document.select("span[class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
		if (!isTempoPartida) {
			statusPartida = statusPartida.PARTIDA_ENCERRADA;
		}
		LOGGER.info(statusPartida.toString());
		return statusPartida;
	}
	
	public String obtemTempoPartida(Document document) {
		String tempoPartida = null;
		
		boolean isTempoPartida = document.select("span [class=liveresults-sports-immersive__game-minute]").isEmpty();
		if (!isTempoPartida) {
			tempoPartida = document.select("span [class=liveresults-sports-immersive__game-minute]").first().text();

		}
		
		isTempoPartida = document.select("span [class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").isEmpty();
		if (!isTempoPartida) {
			tempoPartida = document.select("span [class=imso_mh__ft-mtch imso-medium-font imso_mh__ft-mtchc]").first().text();
		}
		LOGGER.info(tempoPartida.toString());
		return tempoPartida;
	}
	
	public String recuperaNomeEquipeCasa(Document document) {
		
		Element element = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
		String nomeEquipe = element.select("span").text();
		
		return nomeEquipe;
	}
	
	public String recuperaNomeEquipeVisitante(Document document) {
		Element element = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
		String nomeEquipe = element.select("span").text();
		
		return nomeEquipe;
	}
	
	public String recuperaLogoEquipeCasa(Document document) {
		Element element = document.selectFirst("div[class=imso_mh__first-tn-ed imso_mh__tnal-cont imso-tnol]");
		String urlLogo = "https:" + element.select("img[class=imso_btl__mh-logo]").attr("src");
		
		return urlLogo;
	}
	
	public String recuperaLogoEquipeVisitante(Document document) {
		Element element = document.selectFirst("div[class=imso_mh__second-tn-ed imso_mh__tnal-cont imso-tnol]");
		String urlLogo = "https:" + element.select("img[class=imso_btl__mh-logo]").attr("src");
		
		return urlLogo;
	}
	
	public Integer recuperaPlacarEquipeCasa(Document document) {
		String placarEquipe = document.selectFirst("div[class=imso_mh__l-tm-sc imso_mh__scr-it imso-light-font]").text();
		
		return Integer.valueOf(placarEquipe);
	}
	
	public Integer recuperaPlacarEquipeVisitante(Document document) {
		String placarEquipe = document.selectFirst("div[class=imso_mh__r-tm-sc imso_mh__scr-it imso-light-font]").text();
		
		return Integer.valueOf(placarEquipe);
	}
	
	public String recuperaGolsEquipeCasa(Document document) {
		List<String> golsEquipe = new ArrayList<>();
		
		Elements elementos = document.select("div[class=imso_gs__tgs imso_gs__left-team]");
		
		for (Element e : elementos) {
			String infoGol = e.select("div[class=imso_gs__gs-r]").text();
			golsEquipe.add(infoGol);
		}
		 
		return String.join(", ", golsEquipe);
	}
	
	public String recuperaGolsEquipeVisitante(Document document) {
		List<String> golsEquipe = new ArrayList<>();
		
		Elements elementos = document.select("div[class=imso_gs__tgs imso_gs__right-team]");
		
		for (Element e : elementos) {
			String infoGol = e.select("div[class=imso_gs__gs-r]").text();
			golsEquipe.add(infoGol);	
		}
		
		return String.join(", ", golsEquipe);
	}
	
	public String montarUrlGoogle(String nomeEquipeCasa, String nomeEquipeVisitante) {
		try {
			String equipeCasa = nomeEquipeCasa.replace(" ", "+").replace("-", "+");
			String equipeVisitante = nomeEquipeVisitante.replace(" ", "+").replace("-", "+");
			
			return BASE_URL_GOOGLE + equipeCasa + "x" + equipeVisitante + COMPLEMENTO_URL_GOOGLE;
		} catch (Exception e) {
			LOGGER.info("ERRO: {}", e);
		}
	
		return null;
	}
	
}
