package com.game.service;

import com.game.PlayerBadRequestException;
import com.game.PlayerNotFoundException;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class PlayerServiceImpl implements PlayerService{

    private PlayerRepository repository;

    @Autowired
    public void setRepository(PlayerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<Player> getPlayerList(Specification<Player> specification, Pageable sortedByName) {
        return repository.findAll(specification, sortedByName);
    }

    @Override
    public List<Player> getPlayerList(Specification<Player> specification) {
        return repository.findAll(specification);
    }

    @Override
    public List<Player> getPlayerList(){
        List<Player> playerList = repository.findAll();
        return playerList;
    }

    @Override
    public Player createPlayer(Player player) {
        if(player.getName() == null || player.getTitle() == null || player.getRace() == null
                || player.getProfession() == null || player.getBirthday() == null || player.getExperience() == null){
            throw new PlayerBadRequestException("Bad request, one of parm is null");
        }

        if(!validName(player) || !validTitle(player) || !validExperience(player) || !validBirthday(player)){
            throw new PlayerBadRequestException("Bad request, one of parm is not valid");
        }

        if(player.getBanned() == null) player.setBanned(false);

        recalculatedPlayer(player);

        return repository.save(player);
    }

    @Override
    public Player getPlayer(String id) {
        Long id_long = validId(id);
        return repository.findById(id_long).get();
    }

    @Override
    public Player updatePlayer(String id, Player player) {
        Player editedPlayer = getPlayer(id);

        if(player.getName() != null){
            if(!validName(player)) throw new PlayerBadRequestException("Bad request, Name not valid");
            editedPlayer.setName(player.getName());
        }

        if(player.getTitle() != null){
            if(!validTitle(player)) throw new PlayerBadRequestException("Bad request, Title not valid");
            editedPlayer.setTitle(player.getTitle());
        }

        if(player.getRace() != null){
            editedPlayer.setRace(player.getRace());
        }

        if(player.getProfession() != null){
            editedPlayer.setProfession(player.getProfession());
        }

        if(player.getExperience() != null){
            if(!validExperience(player)) throw new PlayerBadRequestException("Bad request, Experience not valid");
            editedPlayer.setExperience(player.getExperience());
            recalculatedPlayer(editedPlayer);
        }

        if(player.getBanned() != null){
            editedPlayer.setBanned(player.getBanned());
        }

        if(player.getBirthday() != null){
            if(!validBirthday(player)) throw new PlayerBadRequestException("Bad request, Birthday not valid");
            editedPlayer.setBirthday(player.getBirthday());
        }
        return repository.save(editedPlayer);
    }

    @Override
    public void deletePlayer(String id) {
        Long id_long = validId(id);
        repository.deleteById(id_long);
    }

    public void recalculatedPlayer(Player player){
        if(player.getExperience() >= 0 && player.getExperience() <= 10_000_000){
            Integer level;
            Integer untilNextLevel;
            Integer exp = player.getExperience();

            level = (int)(Math.sqrt(exp * 200 + 2500) - 50) / 100;
            untilNextLevel = 50 * (level + 1) * (level + 2) - exp;

            player.setLevel(level);
            player.setUntilNextLevel(untilNextLevel);
        }
    }

    public Long validId (String id){
        if(id == null) throw new PlayerBadRequestException("Bad request, Id is null");
        Long id_long;
        try{
            id_long = Long.parseLong(id);
            if(id_long <=0 ) throw new PlayerBadRequestException("Bad request, Id < 0");
            if(!repository.existsById(id_long)) throw new PlayerNotFoundException("Not Found, player");
            return id_long;
        }catch (NumberFormatException e){
            throw new PlayerBadRequestException("Bad request, Id not valid");
        }
    }

    public Boolean validName(Player player){
        if(player.getName().trim().length() == 0 || player.getName().length() > 12) return false;
        return true;
    }

    public Boolean validTitle(Player player){
        if(player.getTitle().length() > 30) return false;
        return true;
    }

    public Boolean validExperience(Player player){
        if(player.getExperience() < 0 || player.getExperience() > 10000000) return false;
        return true;
    }

    public Boolean validBirthday(Player player){
        if(player.getBirthday().getTime() < 0) return false;
        return true;
    }

    @Override
    public Specification<Player> selectByName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) return null;
            return criteriaBuilder.like(root.get("name"), "%" + name + "%");
        };
    }

    @Override
    public Specification<Player> selectByTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null) return null;
            return criteriaBuilder.like(root.get("title"), "%" + title + "%");
        };
    }

    @Override
    public Specification<Player> selectByRace(Race race) {
        return (root, query, criteriaBuilder) -> {
            if (race == null) return null;
            return criteriaBuilder.equal(root.get("race"), race);
        };
    }

    @Override
    public Specification<Player> selectByProfession(Profession profession) {
        return (root, query, criteriaBuilder) -> {
            if (profession == null) return null;
            return criteriaBuilder.equal(root.get("profession"), profession);
        };
    }

    @Override
    public Specification<Player> selectByBirthday(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null) return null;
            if (after == null) {
                Date date = new Date(before);
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), date);
            }
            if (before == null) {
                Date date = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), date);
            }
            Date before1 = new Date(before);
            Date after1 = new Date(after);
            return criteriaBuilder.between(root.get("birthday"), after1, before1);
        };
    }

    @Override
    public Specification<Player> selectByBanned(Boolean banned) {
        return (root, query, criteriaBuilder) -> {
            if(banned == null) return null;
            return banned ? criteriaBuilder.isTrue(root.get("banned"))
                    : criteriaBuilder.isFalse(root.get("banned"));
        };
    }

    @Override
    public Specification<Player> selectByExperience(Integer minExperience, Integer maxExperience) {
        return (root, query, criteriaBuilder) -> {
            if (minExperience == null && maxExperience == null) return null;

            if (minExperience == null) return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), maxExperience);

            if (maxExperience == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), minExperience);

            return criteriaBuilder.between(root.get("experience"), minExperience, maxExperience);
        };
    }

    @Override
    public Specification<Player> selectByLevel(Integer minLevel, Integer maxLevel) {
        return (root, query, criteriaBuilder) -> {
            if (minLevel == null && maxLevel == null) return null;

            if (minLevel == null) return criteriaBuilder.lessThanOrEqualTo(root.get("level"), maxLevel);

            if (maxLevel == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), minLevel);

            return criteriaBuilder.between(root.get("level"), minLevel, maxLevel);
        };
    }
}
