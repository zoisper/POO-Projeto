
import com.sun.source.tree.Tree;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.stream.Collectors;

public class State implements Serializable {

    private Map<String, FootballPlayer> players;
    private Map<String, FootballTeam> teams;
    private List<FootballMatch> games;

    public State(){
        this.players = new TreeMap<>();
        this.teams = new TreeMap<>();
        this.games = new ArrayList<>();
    }

    public State(Map<String, FootballPlayer> players, Map<String, FootballTeam> teams, List<FootballMatch> games ){
        setPlayers(players);
        setTeams(teams);
        setGames(games);
    }

    public State(State state){
        setPlayers(state.getPlayers());
        setTeams(state.getTeams());
        setGames(state.getGames());
    }

    public Map<String, FootballPlayer> getPlayers() {
        Map<String, FootballPlayer> r = new TreeMap<>();
        for(Map.Entry<String, FootballPlayer> me : this.players.entrySet())
            r.put(me.getKey(), me.getValue().clone());
        return r;

    }

    public void setPlayers(Map<String, FootballPlayer> players){
        this.players = new TreeMap<>();
        for(Map.Entry<String, FootballPlayer> m : players.entrySet())
            this.players.put(m.getKey(), m.getValue().clone());
    }

    public Map<String, FootballTeam> getTeams() {
        Map<String, FootballTeam> r = new TreeMap<>();
        for(Map.Entry<String, FootballTeam> me : this.teams.entrySet())
            r.put(me.getKey(), me.getValue().clone());
        return r;
    }

    public void setTeams(Map<String, FootballTeam> teams){
        this.teams = new TreeMap<>();
        for (Map.Entry<String, FootballTeam> m : teams.entrySet())
            this.teams.put(m.getKey(), m.getValue().clone());
    }

    public List<FootballMatch> getGames(){
        return this.games.stream().map(g->g.clone()).collect(Collectors.toList());
    }

    public void setGames(List<FootballMatch> games){
        this.games = new ArrayList<>();
        for(FootballMatch f : games)
            this.games.add(f.clone());
    }


    public State clone(){
        return new State(this);
    }

    public boolean equals(Object o){
        if(o == this)
            return true;
        if(o == null || o.getClass() != this.getClass())
            return false;
        State s = (State) o;
        return this.players.equals(s.getPlayers()) && this.teams.equals(s.getTeams()) && this.games.equals(s.getGames());
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Numero de Jogadores:").append(this.players.size()).append("\n");
        sb.append("Numero de Equipas:").append(this.teams.size()).append("\n");
        sb.append("Numero de Jogos:").append(this.games.size());
        return sb.toString();
    }

    public void transferPlayer(String playerName, String teamName){
        FootballPlayer fp = this.players.get(playerName);
        String team = fp.getTeam();
        if(this.teams.containsKey(team))
            this.teams.get(team).removePlayer(fp.getNumber());
        fp.switchTeam(teamName);
        this.teams.get(teamName).addPlayer(fp);

    }


    public void addPlayer(FootballPlayer fp) {
        this.players.putIfAbsent(fp.getName(),fp.clone());

    }

    public void addTeam(FootballTeam team){
        this.teams.putIfAbsent(team.getName(), team.clone());
    }

    public void addGame(FootballMatch match){
        this.games.add(match.clone());
    }

    public FootballPlayer removePlayer(String name){
        return this.players.remove(name);
    }


    public FootballTeam removeTeam(String name){
        return this.teams.remove(name);
    }

    public FootballTeam removeTeam(FootballTeam team){
        return this.removeTeam(team.getName());
    }

    public FootballMatch removeGame(int index){
        return this.games.remove(index);
    }


    public FootballPlayer getPlayer(String name){
        if (this.players.containsKey(name))
            return this.players.get(name).clone();
        return null;
    }



    public FootballTeam getTeam(String name){
        if (this.teams.containsKey(name))
            return this.teams.get(name).clone();
        return null;
    }



    public void parse(String filename) throws LinhaIncorretaException {
        List<String> linhas = lerFicheiro(filename);
        Map<String, FootballTeam> equipas = new TreeMap<>(); //nome, equipa
        Map<String, FootballPlayer> jogadores = new TreeMap<>(); //numero, jogador
        List<FootballMatch> jogos = new ArrayList<>();
        FootballTeam ultima = null; FootballPlayer j = null;
        String[] linhaPartida;
        for (String linha : linhas) {
            linhaPartida = linha.split(":", 2);
            switch(linhaPartida[0]){
                case "Equipa":
                    FootballTeam e = FootballTeam.parse(linhaPartida[1]);
                    equipas.put(e.getName(), e);
                    ultima = e;
                    break;
                case "Guarda-Redes":
                    j = GoalKeeper.parse(linhaPartida[1]);
                    j.setTeam(ultima.getName());
                    jogadores.put(j.getName(), j);
                    if (ultima == null) throw new LinhaIncorretaException(); //we need to insert the player into the team
                    ultima.addPlayer(j.clone()); //if no team was parsed previously, file is not well-formed
                    break;
                case "Defesa":
                    j = Defender.parse(linhaPartida[1]);
                    j.setTeam(ultima.getName());
                    jogadores.put(j.getName(), j);
                    if (ultima == null) throw new LinhaIncorretaException(); //we need to insert the player into the team
                    ultima.addPlayer(j.clone()); //if no team was parsed previously, file is not well-formed
                    break;
                case "Medio":
                    j = MidFielder.parse(linhaPartida[1]);
                    j.setTeam(ultima.getName());
                    jogadores.put(j.getName(), j);
                    if (ultima == null) throw new LinhaIncorretaException(); //we need to insert the player into the team
                    ultima.addPlayer(j.clone()); //if no team was parsed previously, file is not well-formed
                    break;
                case "Lateral":
                    j = Winger.parse(linhaPartida[1]);
                    j.setTeam(ultima.getName());
                    jogadores.put(j.getName(), j);
                    if (ultima == null) throw new LinhaIncorretaException(); //we need to insert the player into the team
                    ultima.addPlayer(j.clone()); //if no team was parsed previously, file is not well-formed
                    break;
                case "Avancado":
                    j = Striker.parse(linhaPartida[1]);
                    j.setTeam(ultima.getName());
                    jogadores.put(j.getName(), j);
                    if (ultima == null) throw new LinhaIncorretaException(); //we need to insert the player into the team
                    ultima.addPlayer(j.clone()); //if no team was parsed previously, file is not well-formed
                    break;
                case "Jogo":
                    FootballMatch jo = FootballMatch.parse(linhaPartida[1]);
                    jogos.add(jo);
                    break;
                default:
                    throw new LinhaIncorretaException();

            }
        }

        //debug
        /*for (FootballTeam e: equipas.values()){
            System.out.println(e.toString());
        }
        for (FootballMatch jog: jogos){
            System.out.println(jog.toString());
        }*/
        this.setGames(jogos);
        this.setPlayers(jogadores);
        this.setTeams(equipas);

    }
    public static List<String> lerFicheiro(String nomeFich) {
        List<String> lines;
        try { lines = Files.readAllLines(Paths.get(nomeFich), StandardCharsets.UTF_8); }
        catch(IOException exc) {
            System.out.println(exc.getMessage());
            lines = new ArrayList<>();
        }
        return lines;
    }

    public void save(String fileName) throws IOException{
        FileOutputStream fos = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.flush();
        oos.close();

    }

    public static State load(String fileName) throws ClassNotFoundException, IOException{
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        State state  = (State) ois.readObject();
        return state;

    }









    }


