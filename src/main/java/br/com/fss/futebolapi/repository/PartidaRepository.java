package br.com.fss.futebolapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.fss.futebolapi.entity.Partida;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Long>{

}
