package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest")
public class PlayerController {

    private PlayerService service;

    @Autowired
    public void setService(PlayerService service) {
        this.service = service;
    }

    @GetMapping(value = "/players")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> getAllPlayers(@RequestParam(value = "name", required = false) String name,
                                    @RequestParam(value = "title", required = false) String title,
                                    @RequestParam(value = "race", required = false) Race race,
                                    @RequestParam(value = "profession", required = false)Profession profession,
                                    @RequestParam(value = "after", required = false) Long after,
                                    @RequestParam(value = "before", required = false) Long before,
                                    @RequestParam(value = "banned", required = false) Boolean banned,
                                    @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                    @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                    @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                    @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                    @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
                                    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        return service.getPlayerList(Specification.where(
                             service.selectByName(name)
                        .and(service.selectByTitle(title))
                        .and(service.selectByRace(race))
                        .and(service.selectByProfession(profession))
                        .and(service.selectByBirthday(after, before))
                        .and(service.selectByBanned(banned))
                        .and(service.selectByExperience(minExperience, maxExperience))
                        .and(service.selectByLevel(minLevel, maxLevel))), pageable)
                .getContent();
    }

    @RequestMapping(value = "/players/count", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Integer getCount(@RequestParam(value = "name", required = false) String name,
                                      @RequestParam(value = "title", required = false) String title,
                                      @RequestParam(value = "race", required = false) Race race,
                                      @RequestParam(value = "profession", required = false)Profession profession,
                                      @RequestParam(value = "after", required = false) Long after,
                                      @RequestParam(value = "before", required = false) Long before,
                                      @RequestParam(value = "banned", required = false) Boolean banned,
                                      @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                      @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                      @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                      @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                      @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
                                      @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        return service.getPlayerList(Specification.where(
                        service.selectByName(name)
                                .and(service.selectByTitle(title))
                                .and(service.selectByRace(race))
                                .and(service.selectByProfession(profession))
                                .and(service.selectByBirthday(after, before))
                                .and(service.selectByBanned(banned))
                                .and(service.selectByExperience(minExperience, maxExperience))
                                .and(service.selectByLevel(minLevel, maxLevel))))
                .size();
    }

    @RequestMapping(value = "/players", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Player createPlayer(@RequestBody Player player) {
        return service.createPlayer(player);
    }

    @RequestMapping(value = "/players/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Player getPlayer(@PathVariable(value = "id") String id){
        return service.getPlayer(id);
    }

    @RequestMapping(value = "/players/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public Player getPlayer(@PathVariable(value = "id") String id, @RequestBody Player player){
        return service.updatePlayer(id,player);
    }

    @RequestMapping(value = "/players/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deletePlayer(@PathVariable(value = "id") String id){
        service.deletePlayer(id);

    }
}
