package br.com.fss.futebolapi.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PartidaGoogleDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String statusPartida;
	private String tempoPartida;
	
	//informações da equipe da casa
	private String nomeEquipeCasa;
	private String urlLogoEquipeCasa;
	private String placarEquipeCasa;
	private String golsEquipeCasa;
	private String placarEstendidoEquipeCasa;
	
	//informações da equipe visitante
	private String nomeEquipeVisitante;
	private String urlLogoEquipeVisitante;
	private String placarEquipeVisitante;
	private String golsEquipeVisitante;
	private String placarEstendidoEquipeVisitante;	

}
