package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;


public interface PlayerService {
    Page<Player> getPlayerList(Specification<Player> specification, Pageable sortedByName);
    List<Player> getPlayerList(Specification<Player> specification);
    List<Player> getPlayerList();
    Player createPlayer(Player player);
    Player getPlayer(String id);
    Player updatePlayer(String id, Player player);
    void deletePlayer(String id);

    Specification<Player> selectByName(String name);
    Specification<Player> selectByTitle(String title);
    Specification<Player> selectByRace(Race race);
    Specification<Player> selectByProfession(Profession profession);
    Specification<Player> selectByBirthday(Long after, Long before);
    Specification<Player> selectByBanned(Boolean banned);
    Specification<Player> selectByExperience(Integer minExperience, Integer maxExperience);
    Specification<Player> selectByLevel(Integer minLevel, Integer maxLevel);

}
